package com.stereowalker.survive.events;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.IBlockPropertyHandler;
import com.stereowalker.survive.api.IBlockPropertyHandler.PropertyPair;
import com.stereowalker.survive.api.world.level.block.TemperatureEmitter;
import com.stereowalker.survive.json.BlockTemperatureJsonHolder;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.unionlib.util.LoaderHelper;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class TempEvents {
	private static final int MAX_PENDING_TASKS = 10;
	public static final Map<ChunkPos, Map<BlockPos, Float>> GLOBAL_BLOCK_TEMPS = new ConcurrentHashMap<>();
	public static final Map<BlockPos, Float> INVALID_PRE_COMPUTED_TEMPS = new ConcurrentHashMap<>();
	private static ExecutorService ex = null; 
	public static Logger logger = LogManager.getLogger("TempEvents");

	protected static final Map<BlockState, TempData> STATE_CACHE = new ConcurrentHashMap<>();
	protected static final Queue<ProcessQueue<?>> TO_PROCESS = new ConcurrentLinkedQueue<>();

	protected static record TempData(float tempModifier, float conductionCoeff /*Add implementation later*/, float sourceRange) {
	}
	protected static record ProcessQueue<T>(BlockPos pos, T object, Function<T,Float> function) {
	}

	public static void serverStart(MinecraftServer server) {
		if (ex == null || ex.isShutdown() || ex.isTerminated()) {
			ex = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(MAX_PENDING_TASKS), new ThreadPoolExecutor.DiscardOldestPolicy());
		}
	}

	public static void serverStop(MinecraftServer server) {
		ex.shutdown();
		TO_PROCESS.clear();
		GLOBAL_BLOCK_TEMPS.clear();
		INVALID_PRE_COMPUTED_TEMPS.clear();
	}

	protected static class JobEntry {
		final BlockPos pos;
		final long enqueuedAt;
		public JobEntry(BlockPos pos) {
			this.pos = pos;
			this.enqueuedAt = System.nanoTime();
		}
	}

	public static void log(String m) {
		if (LoaderHelper.isDevEnvironment()) logger.info(m);
	}

	public static void discardChunk(ChunkPos chunk) {
		GLOBAL_BLOCK_TEMPS.remove(chunk);
	}
	public static void invalidateBlockTemp(BlockPos block) {
		int rangeInBlocks = 5;
		for (int x = -rangeInBlocks; x <= rangeInBlocks; x++) {
			for (int y = -rangeInBlocks; y <= rangeInBlocks; y++) {
				for (int z = -rangeInBlocks; z <= rangeInBlocks; z++) {
					BlockPos heatSource = new BlockPos(block.getX()+x, block.getY()+y, block.getZ()+z);
					ChunkPos chunk = ChunkPos.containing(heatSource);
					Map<BlockPos, Float> chunkMap = GLOBAL_BLOCK_TEMPS.get(chunk);
					if (chunkMap != null) {
						Float removed = chunkMap.remove(local(chunk, heatSource));
						if (removed != null) INVALID_PRE_COMPUTED_TEMPS.put(heatSource, removed);
					}
				}
			}
		}
	}
	public static BlockPos local(ChunkPos chunk, BlockPos block) {
		return new BlockPos(block.getX() - (chunk.x() * 16), block.getY(), block.getZ() - (chunk.z() * 16));
	}
	public static void cacheTemp(BlockPos block, float temp) {
		ChunkPos chunk = ChunkPos.containing(block);
		GLOBAL_BLOCK_TEMPS.computeIfAbsent(chunk, k -> new ConcurrentHashMap<>());
		GLOBAL_BLOCK_TEMPS.get(chunk).put(local(chunk, block), temp);
	}
	public static <T> float tempOrCache(BlockPos block, T quick, T full, Function<T,Float> calc) {
		ChunkPos chunk = ChunkPos.containing(block);
		float temp = 0;
		if (GLOBAL_BLOCK_TEMPS.containsKey(chunk) && GLOBAL_BLOCK_TEMPS.get(chunk).containsKey(local(chunk, block))) {
			temp = GLOBAL_BLOCK_TEMPS.get(chunk).get(local(chunk, block));
			//			log("Pulled Cache at"+block+" "+chunk+" "+local(chunk, block)+" "+temp);
			return temp;
		}
		else {
			if (INVALID_PRE_COMPUTED_TEMPS.containsKey(block))
				temp = INVALID_PRE_COMPUTED_TEMPS.remove(block);
			else
				temp = calc.apply(quick);
			cacheTemp(block, temp);
			TO_PROCESS.add(new ProcessQueue<T>(block, full, calc));
			ex.submit(() -> {
				while (TO_PROCESS.size() > 5) TO_PROCESS.poll(); 

				@SuppressWarnings("unchecked")
				ProcessQueue<T> task = (ProcessQueue<T>) TO_PROCESS.poll();
				if (task != null) {
					long start = System.nanoTime();
					cacheTemp(task.pos(), task.function().apply(task.object()));
					long end = System.nanoTime();
//					log("Caching "+block+" "+((end - start) / 1000000.0D)+"ms");	
				}
			});
			return temp;
		}
	}


	public static void buildStateCache() {
		Survive.getInstance().getLogger().info("Started Building BlockState temp cache");
		long start = System.nanoTime();
		STATE_CACHE.clear();
		for (Block block : RegistryHelper.blocks()) {
			for (BlockState state : block.getStateDefinition().getPossibleStates()) {
				if (state.getBlock() instanceof TemperatureEmitter) continue;
				STATE_CACHE.put(state, computeTempDataFor(state));
			}
		}
		long end = System.nanoTime();
		Survive.getInstance().getLogger().info("Finished Building BlockState temp cache in "+((end - start) / 1000000.0D)+"ms");
	}

	private static TempData computeTempDataFor(BlockState heatState) {
		float blockTemp = 0;
		float range = 5;
		if (DataMaps.Server.blockTemperature.containsKey(RegistryHelper.blocks().getKey(heatState.getBlock()))) {
			BlockTemperatureJsonHolder blockTemperatureData = DataMaps.Server.blockTemperature.get(RegistryHelper.blocks().getKey(heatState.getBlock()));
			range = blockTemperatureData.getRange();
			if (blockTemperatureData.getStateChangeProperty() != null) {
				boolean setTemp = false;
				heatState.getBlock().getStateDefinition().getPossibleStates();
				List<Triple<IBlockPropertyHandler<?>,List<PropertyPair<?>>,Map<String,Float>>> changeProperty = blockTemperatureData.getStateChangeProperty();
				first:
					for (Triple<IBlockPropertyHandler<?>, List<PropertyPair<?>>, Map<String, Float>> handler : changeProperty) {
						boolean meets = true;
						for (PropertyPair<?> requirements : handler.getMiddle()) {
							if (!heatState.getValue(requirements.getFirst()).equals(requirements.getSecond())) {
								meets = false;
								break;
							}
						}
						if (meets)
							for (String prop2 : handler.getRight().keySet()) {
								Property<?> property = null;
								for (var p : heatState.getProperties()) {
									if (p.equals(handler.getLeft().derivedProperty())) {
										property = p;
										break;
									}
								}
								if (property == null) {
									logger.error("Could not find property {} in block {}", handler.getLeft().derivedProperty(), heatState);
								}
								else if (heatState.getValue(property).equals(handler.getLeft().getValue(prop2))) {
									blockTemp += handler.getRight().get(prop2);
									setTemp = true;
									break first;
								}
							}
					}
				if (!setTemp) blockTemp += blockTemperatureData.getTemperatureModifier();
			}
			else {
				blockTemp += blockTemperatureData.getTemperatureModifier();

				if (blockTemperatureData.usesLevelProperty()) {
					if (heatState.hasProperty(BlockStateProperties.LEVEL)) {
						blockTemp*=(heatState.getValue(BlockStateProperties.LEVEL)+1)/16;
					}
					else if (heatState.hasProperty(BlockStateProperties.LEVEL_COMPOSTER)) {
						blockTemp*=(heatState.getValue(BlockStateProperties.LEVEL_COMPOSTER)+1)/9;
					}
					else if (heatState.hasProperty(BlockStateProperties.LEVEL_FLOWING)) {
						blockTemp*=(heatState.getValue(BlockStateProperties.LEVEL_FLOWING))/8;
					}
					else if (heatState.hasProperty(BlockStateProperties.LEVEL_CAULDRON)) {
						blockTemp*=(heatState.getValue(BlockStateProperties.LEVEL_CAULDRON)+1)/4;
					}
				}
			}
		}
		return new TempData(blockTemp, 1, range);
	}
}
