package com.stereowalker.survive.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.PriorityQueue;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.FoodUtils;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.FoodUtils.State;
import com.stereowalker.survive.api.IBlockPropertyHandler;
import com.stereowalker.survive.api.IBlockPropertyHandler.PropertyPair;
import com.stereowalker.survive.api.world.level.block.TemperatureEmitter;
import com.stereowalker.survive.compat.PneumaticraftCompat;
import com.stereowalker.survive.compat.SereneSeasonsCompat;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.TempMode;
import com.stereowalker.survive.json.BiomeJsonHolder;
import com.stereowalker.survive.json.BlockTemperatureJsonHolder;
import com.stereowalker.survive.json.ConsummableJsonHolder;
import com.stereowalker.survive.json.EntityTemperatureJsonHolder;
import com.stereowalker.survive.json.FluidJsonHolder;
import com.stereowalker.survive.needs.CustomFoodData;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.needs.TemperatureData;
import com.stereowalker.survive.needs.TemperatureUtil;
import com.stereowalker.survive.network.protocol.game.ClientboundDataTransferPacket;
import com.stereowalker.survive.network.protocol.game.ClientboundSurvivalStatsPacket;
import com.stereowalker.survive.network.protocol.game.ServerboundInteractWithWaterPacket;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.survive.world.item.enchantment.SEnchantmentHelper;
import com.stereowalker.survive.world.seasons.Season;
import com.stereowalker.survive.world.temperature.TemperatureModifier.ContributingFactor;
import com.stereowalker.survive.world.temperature.TemperatureQuery;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeInstance;
import com.stereowalker.unionlib.util.LoaderHelper;
import com.stereowalker.unionlib.util.ModHelper;
import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.unionlib.util.VersionHelper;
import com.stereowalker.unionlib.util.VersionHelper.VanillaComponents;
import com.stereowalker.unionlib.util.math.UnionMathHelper;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class SurviveEvents {
	
	public static void desyncClient(Player player) {
		if (!player.level().isClientSide && DataMaps.Server.syncedClients.containsKey(player.getUUID()) ) {
			Survive.getInstance().getLogger().info("Removing Client ("+player.getDisplayName().getString()+") From Survive Data Sync List");
			DataMaps.Server.syncedClients.put(player.getUUID(), false); 
		}
	}

	public static void sendToClient(LivingEntity living) {
		if (living != null && !living.level().isClientSide && living instanceof ServerPlayer player) {
			new ClientboundSurvivalStatsPacket(player, true).send(player);
			new ClientboundSurvivalStatsPacket(player, false).send(player);
			if (!DataMaps.Server.syncedClients.containsKey(player.getUUID()))
				DataMaps.Server.syncedClients.put(player.getUUID(), false); 
			if (!DataMaps.Server.syncedClients.get(player.getUUID())) {
				Survive.getInstance().getLogger().info("Syncing All Data To Client ("+player.getDisplayName().getString()+")");
				Survive.getInstance().getLogger().info("Syncing Armor Data");
				MutableInt a = new MutableInt(0);
				DataMaps.Server.armor.forEach((key, value) -> {
					new ClientboundDataTransferPacket(key, value, a.getValue() == 0).send(player);
					a.increment();
				});
				Survive.getInstance().getLogger().info("Done with Armors");
				Survive.getInstance().getLogger().info("Syncing Fluid Data");
				MutableInt f = new MutableInt(0);
				DataMaps.Server.fluid.forEach((key, value) -> {
					new ClientboundDataTransferPacket(key, value, f.getValue() == 0).send(player);
					f.increment();
				});
				Survive.getInstance().getLogger().info("Done with Fluids");
				Survive.getInstance().getLogger().info("Syncing Biome Data");
				MutableInt i = new MutableInt(0);
				DataMaps.Server.biome.forEach((key, value) -> {
					new ClientboundDataTransferPacket(key, value, i.getValue() == 0).send(player);
					i.increment();
				});
				Survive.getInstance().getLogger().info("Done with Biomes");
				Survive.getInstance().getLogger().info("Syncing Consummable Data");
				MutableInt c = new MutableInt(0);
				DataMaps.Server.consummableItem.forEach((key, value) -> {
					new ClientboundDataTransferPacket(key, value, c.getValue() == 0).send(player);
					c.increment();
				});
				Survive.getInstance().getLogger().info("Done with Consummables");
				DataMaps.Server.syncedClients.put(player.getUUID(), true); 
			}
		}
	}

	/**
	 * Check if precipitation is currently happening at a position
	 */
	@SuppressWarnings("deprecation")
	public static boolean isSnowingAt(Level world, BlockPos position) {
		if (!world.isRaining()) {
			return false;
		} else if (!world.canSeeSky(position)) {
			return false;
		} else if (world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, position).getY() > position.getY()) {
			return false;
		} else {
			Biome biome = world.getBiome(position).value();
			return biome.getPrecipitationAt(position) == Biome.Precipitation.SNOW || 
					biome.getTemperature(position) <= 0.15F || 
					ModHelper.isPrimalWinterLoaded() || 
					(ModHelper.isSereneSeasonsLoaded() && SereneSeasonsCompat.snowsHere(world, position));
		}
	}

	public static void updateEnvTemperature(LivingEntity living) {
		if (living != null && living instanceof ServerPlayer player && !living.level().isClientSide) {
			SurviveEntityStats.addWetTime(player, player.isUnderWater() ? 2 : player.isInWaterOrRain() ? 1 : -2);
		}
		if (living != null && living instanceof ServerPlayer player) {
			if (player.isAlive()) {
				for (Entry<ResourceLocation, Tuple<TemperatureQuery, ContributingFactor>> entry : TemperatureQuery.queries.entrySet()) {
					double queryValue = entry.getValue().getA().run(player, ((IRealisticEntity)player).temperatureData().getTemperatureLevel(), player.level(), player.blockPosition(), true);
					TemperatureData.setTemperatureModifier(player, entry.getKey(), queryValue, entry.getValue().getB());
				}
			}
		}
		if (living instanceof Player player) {
			FoodUtils.giveLifespanToFood(player.getInventory().items, player.level().getGameTime());
			for (ChunkPos chunk : TempEvents.GLOBAL_BLOCK_TEMPS.keySet()) {
				if (!player.level().hasChunk(chunk.x, chunk.z)) {
					TempEvents.discardChunk(chunk);
					TempEvents.log("Discarding Chunks "+chunk);
				}
			}
		}
	}

	public static float getModifierFromSlot(EquipmentSlot slot) {
		switch (slot) {
		case HEAD:return 0.05F;
		case CHEST:return 0.16F;
		case LEGS:return 0.13F;
		case FEET:return 0.06F;
		default:return 0F;
		}
	}
	
	private static record PathNode(BlockPos pos, double cost) implements Comparable<PathNode> {
		@Override
	    public int compareTo(PathNode other) {
	        return Double.compare(this.cost, other.cost);
	    }
	}
	private static record Offset(int dx, int dy, int dz) {
	}
	
	private static double getBlockTransmissionCost(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (!state.getFluidState().isEmpty() && !state.getFluidState().is(FluidTags.LAVA))
			return 1.2f;
		else if (state.isSolid())
			return 5.0f;
		return 1.0f;
	}
	
	public static Object2DoubleOpenHashMap<BlockPos> buildThermalCostMap(Level level, BlockPos startPos, double maxCost) {
		PriorityQueue<PathNode> queue = new PriorityQueue<>();
	    Object2DoubleOpenHashMap<BlockPos> bestCosts = new Object2DoubleOpenHashMap<>(500);
	    bestCosts.defaultReturnValue(Double.POSITIVE_INFINITY);
	    bestCosts.put(startPos, 0.0);
	    queue.add(new PathNode(startPos, 0));
	    while (!queue.isEmpty()) {
	        PathNode current = queue.poll();
	        if (current.cost > bestCosts.getDouble(current.pos)) continue;
	        
	        for (Direction direction : Direction.values()) {
	            BlockPos neighbor = current.pos.relative(direction);
	            double costToEnter = getBlockTransmissionCost(level, neighbor);
	            double newCost = current.cost + costToEnter;
	            
	            if (newCost <= maxCost && newCost < bestCosts.getDouble(neighbor)) {
	                bestCosts.put(neighbor, newCost);
	                queue.add(new PathNode(neighbor, newCost));
	            }
	        }
	    }
	    return bestCosts;
	}
	
	public static double getEffectiveDistance(Level level, BlockPos startPos, BlockPos target, double maxCost) {
		if (startPos.equals(target)) return 0;
		PriorityQueue<PathNode> queue = new PriorityQueue<>(/* Comparator.comparingDouble(node -> node.cost) */);
		Object2DoubleOpenHashMap<BlockPos> bestCosts = new Object2DoubleOpenHashMap<>();
		bestCosts.defaultReturnValue(Double.POSITIVE_INFINITY);
		bestCosts.put(startPos, 0.0);
		queue.add(new PathNode(startPos, 0));
		while (!queue.isEmpty()) {
			PathNode current = queue.poll();
			if (current.pos.equals(target)) return current.cost;
			if (current.cost > maxCost) continue;
			
			for (Direction direction : Direction.values()) {
				BlockPos neighbor = current.pos.relative(direction);
				double costToEnter = getBlockTransmissionCost(level, neighbor);
				double newCost = current.cost + costToEnter;
				if (newCost <= maxCost && newCost < bestCosts.getDouble(neighbor)) {
					bestCosts.put(neighbor, newCost);
					queue.add(new PathNode(neighbor, newCost));
				}
			}
		}
		return Double.POSITIVE_INFINITY;
	}

	private static final List<Offset> SPHERE_OFFSETS_RANGE_2 = buildSphereOffsets(2);
	private static final List<Offset> SPHERE_OFFSETS_RANGE_5 = buildSphereOffsets(5);
	private static final List<Offset> SPHERE_OFFSETS_RANGE_6 = buildSphereOffsets(6);
	private static List<Offset> buildSphereOffsets(int rangeInBlocks) {
		List<Offset> finalList = new ArrayList<>();
		for (int x = -rangeInBlocks; x <= rangeInBlocks; x++) {
			for (int y = -rangeInBlocks; y <= rangeInBlocks; y++) {
				for (int z = -rangeInBlocks; z <= rangeInBlocks; z++) {
					if (x*x + y*y + z*z <= rangeInBlocks*rangeInBlocks) {
						finalList.add(new Offset(x, y, z));
					}
				}
			}
		}
		return Collections.unmodifiableList(finalList);
	}

	public static double getExactTemperature(Level world, BlockPos pos, TempType type) {
		float skyLight = world.getChunkSource().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos);
		float gameTime = world.getDayTime() % 24000L;
		gameTime = gameTime/(200/3);
		gameTime = (float) Math.sin(Math.toRadians(gameTime));

		switch (type) {
		case SUN:
			float sunIntensity = 5.0f;
			Optional<ResourceKey<Biome>> biomeKey = world.getBiome(pos).unwrapKey();
			if (biomeKey.isPresent() && DataMaps.Server.biome.containsKey(biomeKey.get().location())) {
				sunIntensity = DataMaps.Server.biome.get(biomeKey.get().location()).getSunIntensity();
			}
			if (skyLight > 5.0F) return gameTime*sunIntensity;
			else return -1.0F * sunIntensity;

		case BIOME:
			float biomeTemp = (TemperatureUtil.getTemperature(world.getBiome(pos), pos)*2)-2;
			if (ModHelper.isPrimalWinterLoaded()) {
				biomeTemp = -0.7F;
			}
			return biomeTemp;

		case BLOCK:
			return TempEvents.tempOrCache(pos, SPHERE_OFFSETS_RANGE_2, SPHERE_OFFSETS_RANGE_5, (offsets) -> {
				float totalBlockTemp = 0;
				int rangeInBlocks = 5;
				var blockLightListener = world.getChunkSource().getLightEngine().getLayerListener(LightLayer.BLOCK);
				int currentRange = (offsets.size() < 100) ? 2 : 5;
				Object2DoubleOpenHashMap<BlockPos> thermalMap = buildThermalCostMap(world, pos, currentRange);
				for (Offset offset : offsets) {
					float blockTemp = 0;
					BlockPos heatSource = new BlockPos(pos.getX()+offset.dx, pos.getY()+offset.dy, pos.getZ()+offset.dz);
					
					float blockLight = blockLightListener.getLightValue(heatSource);
					BlockState heatState = world.getBlockState(heatSource);
					float sourceRange;
					TempEvents.TempData stateData = null;
					if (heatState.getBlock() instanceof TemperatureEmitter) {
						sourceRange = ((TemperatureEmitter)heatState.getBlock()).getModificationRange(heatState);
					} else {
						stateData = TempEvents.STATE_CACHE.get(heatState);
						sourceRange = stateData != null ? stateData.sourceRange() : 0; //DataMaps.Server.blockTemperature.containsKey(RegistryHelper.blocks().getKey(heatState.getBlock())) ? DataMaps.Server.blockTemperature.get(RegistryHelper.blocks().getKey(heatState.getBlock())).getRange() : 5;
					}

					if (pos.closerThan(heatSource, sourceRange)) {
						blockTemp += blockLight/500.0F;
						//Radiator Override
						if (heatState.getBlock() instanceof TemperatureEmitter) {
							blockTemp = ((TemperatureEmitter)heatState.getBlock()).getTemperatureModification(heatState);
						}
						else if (stateData != null) blockTemp += stateData.tempModifier();

						//Complex calculation for distance
						boolean doDistanceCalculation = true;
						if (doDistanceCalculation) {
							double effectiveDistance = thermalMap.getDouble(heatSource)/*getEffectiveDistance(world, heatSource, pos, rangeInBlocks)*/;
							if (effectiveDistance <= rangeInBlocks) {								
								totalBlockTemp+=blockTemp * (1 - effectiveDistance / rangeInBlocks);
							}
						}
						else totalBlockTemp+=blockTemp;
					}
				}
				return totalBlockTemp;
			});

		case SHADE:
			return ((skyLight / 7.5F) - 1);

		case ENTITY:
			float totalEntityTemp = 0;
			int rangeInBlocks = 5;
			for (Entity entity : world.getEntitiesOfClass(Entity.class, /*AABB.encapsulatingFullBlocks*/new AABB(pos.offset(rangeInBlocks, rangeInBlocks, rangeInBlocks), pos.offset(-rangeInBlocks, -rangeInBlocks, -rangeInBlocks)))) {
				ResourceLocation entityKey = RegistryHelper.entityTypes().getKey(entity.getType());
				float sourceRange = DataMaps.Server.entityTemperature.containsKey(entityKey) ? DataMaps.Server.entityTemperature.get(entityKey).getRange() : 5;
				if (pos.closerThan(entity.blockPosition(), sourceRange)) {
					if (DataMaps.Server.entityTemperature.containsKey(entityKey)) {
						EntityTemperatureJsonHolder entityTemperatureData = DataMaps.Server.entityTemperature.get(entityKey);
						totalEntityTemp+=entityTemperatureData.getTemperatureModifier();
					}
				}
			}
			return totalEntityTemp;

		default:
			return Survive.DEFAULT_TEMP;
		}
	}

	private enum TempType {
		BIOME("biome", 6, false), BLOCK("block", 8, true), ENTITY("entity", 9, true), SHADE("shade", 200, true), SUN("sun", 200, true);

		String name;
		double reductionAmount;
		boolean usingExact;
		private TempType(String name, double reductionAmountIn, boolean usingExactIn) {
			this.reductionAmount = reductionAmountIn;
			this.usingExact = usingExactIn;
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public double getReductionAmount() {
			return reductionAmount;
		}

		public boolean isUsingExact() {
			return usingExact;
		}
	}

	public static double getBlendedTemperature(Level world, BlockPos mainPos, BlockPos blendPos, TempType type) {
		float distance = (float) Math.sqrt(mainPos.distSqr(blendPos));// 2 - 10 - 0
		if (distance <= 5.0D) {
			float blendRatio0 = distance / 5.0F;   // 0.2 - 1.0 - 0.0
			float blendRatio1 = 1.0F - blendRatio0; // 0.8 - 0.0 - 1.0
			double temp0 = getExactTemperature(world, blendPos, type);
			double temp1 = getExactTemperature(world, mainPos, type);
			return ((temp0*blendRatio0)+(temp1*blendRatio1));
		} else {
			return getExactTemperature(world, mainPos, type);
		}
	}

	public static float getAverageTemperature(Level world, BlockPos pos, TempType type, int rangeInBlocks, TempMode mode) {
		float temp = 0;
		int tempAmount = 0;
		for (int x = -rangeInBlocks; x <= rangeInBlocks; x++) {
			for (int y = -rangeInBlocks; y <= rangeInBlocks; y++) {
				for (int z = -rangeInBlocks; z <= rangeInBlocks; z++) {
					if (mode == TempMode.BLEND)temp+=getBlendedTemperature(world, new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z), pos, type);
					else if (mode == TempMode.NORMAL)temp+=getExactTemperature(world, new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z), type);
					tempAmount++;
				}
			}
		}
		return temp/((float)tempAmount);
	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void interactWithWaterSourceBlock(PlayerInteractEvent.RightClickEmpty event) {
		HitResult raytraceresult = getPlayerPOVHitResult(event.getLevel(), event.getEntity(), ClipContext.Fluid.SOURCE_ONLY);
		BlockPos blockpos = ((BlockHitResult)raytraceresult).getBlockPos();
		if (event.getLevel().isClientSide && ServerboundInteractWithWaterPacket.isValidStack(event.getItemStack()) && event.getHand() == InteractionHand.MAIN_HAND) {
			//Source Block Of Water
			Fluid fluid = event.getLevel().getFluidState(blockpos).getType();
			if (DataMaps.Client.fluid.containsKey(RegistryHelper.fluids().getKey(fluid))) {
				FluidJsonHolder fluidHolder = DataMaps.Client.fluid.get(RegistryHelper.fluids().getKey(fluid));
				float thirstChance = fluidHolder.getThirstChance();
				if (DataMaps.Client.biome.containsKey(event.getLevel().getBiome(blockpos).unwrapKey().get().location())) {
					BiomeJsonHolder biomeData = DataMaps.Client.biome.get(event.getLevel().getBiome(blockpos).unwrapKey().get().location());
					if (biomeData.getThirstChance() >= 0)
						thirstChance = biomeData.getThirstChance();
				}
				new ServerboundInteractWithWaterPacket(blockpos, thirstChance, fluidHolder.getThirstAmount(), fluidHolder.getHydrationAmount(), event.getHand()).send();
			}
			//Air Block
			if (event.getLevel().isRainingAt(blockpos)) {
				new ServerboundInteractWithWaterPacket(event.getPos(), 0.0f, 1.0D, 0.5D, event.getHand()).send();
			}
		}
	}
	
	protected static BlockHitResult getPlayerPOVHitResult(Level pLevel, Player pPlayer, ClipContext.Fluid pFluidMode) {
        /*Vec3 vec3 = pPlayer.getEyePosition();
        Vec3 vec31 = vec3.add(pPlayer.calculateViewVector(pPlayer.getXRot(), pPlayer.getYRot()).scale(pPlayer.blockInteractionRange()));
        return pLevel.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, pFluidMode, pPlayer));*/
		
		float f = pPlayer.getXRot();
		float f1 = pPlayer.getYRot();
		Vec3 vec3d = pPlayer.getEyePosition(1.0F);
		float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
		float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = pPlayer.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue();
		Vec3 vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
		return pLevel.clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, pFluidMode, pPlayer));
    }

	public static void restoreStats(Player thisPlayer, Player thatPlayer, boolean keepEverything) {
		SurviveEntityStats.getOrCreateModNBT(thisPlayer);
		if (keepEverything) {
			IRealisticEntity entity = ((IRealisticEntity)thisPlayer);
			IRealisticEntity original = ((IRealisticEntity)thatPlayer);
			entity.setTemperatureData(original.temperatureData());
			entity.setNutritionData(original.nutritionData());
			entity.setWellbeingData(original.wellbeingData());
			entity.setHygieneData(original.hygieneData());
			entity.setStaminaData(original.staminaData());
			entity.setSleepData(original.sleepData());
			entity.setWaterData(original.waterData());
			SurviveEntityStats.setWetTime(thisPlayer, SurviveEntityStats.getWetTime(thatPlayer));
		}
	}

	public static void addReload(LevelAccessor lvl) {
		TempEvents.buildStateCache();
		Survive.getInstance().getLogger().info("Start Resistering Temperature Queries");
		//Environment
		for (TempType type : TempType.values()) {
			TemperatureQuery.registerQuery("survive:"+type.getName(), ContributingFactor.ENVIRONMENTAL, (player, temp, level, pos, applyTemp)-> {
				double temperature;
				if (type.isUsingExact()) {
					temperature = getExactTemperature(level, pos, type);
				} else {
					temperature = getAverageTemperature(level, pos, type, 5, Survive.TEMPERATURE_CONFIG.tempMode);
				}
				return UnionMathHelper.roundDecimal(3, (temperature)/type.getReductionAmount());
			});
		}
		TemperatureQuery.registerQuery("survive:snow", ContributingFactor.ENVIRONMENTAL, (player, temp, level, pos, applyTemp)-> {
			double snow = 0.0D;
			if (isSnowingAt(level, pos)) {
				snow = -2.0D;
			}
			return snow;
		});
		TemperatureQuery.registerQuery("survive:season", ContributingFactor.ENVIRONMENTAL, (player, temp, level, pos, applyTemp)-> {
			float seasonMod = 0;
			if (ModHelper.isSereneSeasonsLoaded()) {
				Season season = SereneSeasonsCompat.modifyTemperatureBySeason(level, pos);
				if (level.getBiome(pos).unwrapKey().isPresent() && DataMaps.Server.biome.containsKey(level.getBiome(pos).unwrapKey().get().location())) {
					seasonMod = DataMaps.Server.biome.get(level.getBiome(pos).unwrapKey().get().location()).getSeasonModifiers().get(season);
				} else {
					seasonMod = season.getModifier();
				}
				if (ModHelper.isPrimalWinterLoaded()) {
					seasonMod = -1.0F;
				}
			}
			return seasonMod;
		});
		TemperatureQuery.registerQuery("survive:dimension", ContributingFactor.ENVIRONMENTAL, (player, temp, level, pos, applyTemp)->{
			for (String dimensionList : ServerConfig.dimensionModifiers) {
				String[] dimension = dimensionList.split(",");
				ResourceLocation loc = VersionHelper.toLoc(dimension[0]);
				if (RegistryHelper.matchesRegistryKey(loc, level.dimension())) {
					return Float.parseFloat(dimension[1]);
				}
			}
			return 0;
		});
		//Internal
		TemperatureQuery.registerQuery("survive:wetness", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			if (level.getBiome(pos).unwrapKey().isPresent() && DataMaps.Server.biome.containsKey(level.getBiome(pos).unwrapKey().get().location())) {
				float f = DataMaps.Server.biome.get(level.getBiome(pos).unwrapKey().get().location()).getWetnessModifier();
				return ((double)(SurviveEntityStats.getWetTime(player)) / -1800.0D) * f;
			} else {
				return (double)(SurviveEntityStats.getWetTime(player)) / -1800.0D;
			}
		});
		TemperatureQuery.registerQuery("survive:aircon", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			float airconMod = 0;
			if (LoaderHelper.isModLoaded("pneumaticcraft")) {
				airconMod = PneumaticraftCompat.getACMod(player, temp, level, pos, applyTemp);
			}
			return airconMod;
		});
		TemperatureQuery.registerQuery("survive:cooling_enchantment", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			double coolingMod = 0.0D;
			coolingMod -= 0.05D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemBySlot(EquipmentSlot.HEAD));
			coolingMod -= 0.16D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemBySlot(EquipmentSlot.CHEST));
			coolingMod -= 0.13D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemBySlot(EquipmentSlot.LEGS));
			coolingMod -= 0.06D * (float)SEnchantmentHelper.getCoolingModifier(player.getItemBySlot(EquipmentSlot.FEET));
			return coolingMod;
		});
		TemperatureQuery.registerQuery("survive:warming_enchantment", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			double warmingMod = 0.0D;
			warmingMod += 0.05D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemBySlot(EquipmentSlot.HEAD));
			warmingMod += 0.16D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemBySlot(EquipmentSlot.CHEST));
			warmingMod += 0.13D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemBySlot(EquipmentSlot.LEGS));
			warmingMod += 0.06D * (float)SEnchantmentHelper.getWarmingModifier(player.getItemBySlot(EquipmentSlot.FEET));
			return warmingMod;
		});
		TemperatureQuery.registerQuery("survive:thirst_cooldown", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			if (((IRealisticEntity)player).waterData().shouldTempDrop()) {
				if (applyTemp) ((IRealisticEntity)player).waterData().applyTempDrop(player);
				return 1.0D;
			}
			return 0.0D;
		});
		TemperatureQuery.registerQuery("survive:adjusted_cooling_enchantment", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			if (temp > Survive.DEFAULT_TEMP) {
				for (EquipmentSlot types : EquipmentSlot.values()) {
					if (SEnchantmentHelper.hasAdjustedCooling(player.getItemBySlot(types))) {
						return 2.0D;
					}
				}
			}
			return 0.0D;
		});
		TemperatureQuery.registerQuery("survive:adjusted_warming_enchantment", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			if (temp < Survive.DEFAULT_TEMP) {
				for (EquipmentSlot types : EquipmentSlot.values()) {
					if (SEnchantmentHelper.hasAdjustedWarming(player.getItemBySlot(types))) {
						return 2.0D;
					}
				}
			}
			return 0.0D;
		});
		TemperatureQuery.registerQuery("survive:fats", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			IRealisticEntity real = (IRealisticEntity)player;
			if (Survive.CONFIG.nutrition_enabled && temp < (Survive.DEFAULT_TEMP + TemperatureUtil.firstCold(player)) / 2f) {
				if (real.nutritionData().fat().level() > 2500) {
					real.nutritionData().fat().remove(3);
					return 6D;
				} else if (real.nutritionData().fat().level() > 1500) {
					real.nutritionData().fat().remove(2);
					return 3D;
				} else if (real.nutritionData().fat().level() > 500) {
					real.nutritionData().fat().remove(1);
					return 1D;
				}
			}
			return 0.0D;
		});
		TemperatureQuery.registerQuery("survive:armor", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			double armorMod = 0.0D;
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot.getType() == Type.ARMOR) {
					if (!player.getItemBySlot(slot).isEmpty()) {
						Item armor = player.getItemBySlot(slot).getItem();
						float modifier = 1.0F;
						if (DataMaps.Server.armor.containsKey(RegistryHelper.items().getKey(armor))) {
							for (Pair<String,TemperatureChangeInstance> instance : DataMaps.Server.armor.get(RegistryHelper.items().getKey(armor)).getTemperatureModifier()) {
								if (instance.getSecond().shouldChangeTemperature(player)) {
									modifier = instance.getSecond().getTemperature();
									break;
								}
							}
						}
						armorMod += getModifierFromSlot(slot) * modifier;
					}
				}
			}
			return armorMod;
		});
		TemperatureQuery.registerQuery("survive:chilled_effect", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			if (player.hasEffect(SMobEffects.CHILLED.holder().value()))
				return -(0.05F * (float)(player.getEffect(SMobEffects.CHILLED.holder().value()).getAmplifier() + 1));
			else
				return 0;
		});
		TemperatureQuery.registerQuery("survive:heated_effect", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			if (player.hasEffect(SMobEffects.HEATED.holder().value()))
				return +(0.05F * (float)(player.getEffect(SMobEffects.HEATED.holder().value()).getAmplifier() + 1));
			else
				return 0;
		});
		TemperatureQuery.registerQuery("survive:main_held_item", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			if (player.getMainHandItem().getItem() == Items.TORCH && DataMaps.Server.blockTemperature.containsKey(RegistryHelper.blocks().getKey(Blocks.TORCH))) {
				return DataMaps.Server.blockTemperature.get(RegistryHelper.blocks().getKey(Blocks.TORCH)).getTemperatureModifier();
			} else return 0;
		});
		TemperatureQuery.registerQuery("survive:off_held_item", ContributingFactor.INTERNAL, (player, temp, level, pos, applyTemp)->{
			if (player.getOffhandItem().getItem() == Items.TORCH && DataMaps.Server.blockTemperature.containsKey(RegistryHelper.blocks().getKey(Blocks.TORCH))) {
				return DataMaps.Server.blockTemperature.get(RegistryHelper.blocks().getKey(Blocks.TORCH)).getTemperatureModifier();
			} else return 0;
		});
		System.out.println("Done Resistering Temperature Queries");
	}
	
	public static void eat(LivingEntity user, ItemStack stack) {
		if (user instanceof IRealisticEntity ire) {
			if (VanillaComponents.FOOD.hasData(stack) && user instanceof Player player && player.getFoodData() instanceof CustomFoodData custom) {
				FoodProperties foodproperties = VanillaComponents.FOOD.getData(stack);
				for (Pair<MobEffectInstance, Float> effect : foodproperties.getEffects()) {
					if (effect.getFirst().getEffect() == MobEffects.HUNGER || custom.IsSpoiled() == State.Spoiled) {
						custom.consumeUnclean();
						break;
					}
				}
			}
			ire.staminaData().eat(stack.getItem(), stack, user);
			ire.waterData().drink(stack.getItem(), stack, user);
			ire.getRealFoodData().markAsSpoiled(stack, user);
		}
	}
	
	public static void eatNutrition(LivingEntity user, ItemStack stack) {
		if (Survive.CONFIG.nutrition_enabled && user instanceof IRealisticEntity ire) {
			float protein = 1;
			float carbs = 1;
			float fats = 1;
			if (DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(stack.getItem()))) {
				ConsummableJsonHolder data = DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(stack.getItem()));
				protein = data.getProteinRatio();
				carbs = data.getCarbohydrateRatio();
				fats = data.getFatRatio();
			}
			FoodProperties food = VanillaComponents.FOOD.getData(stack);
			float total = protein+carbs+fats;
			ire.nutritionData().carbs().add(food.getNutrition()*Mth.ceil((carbs/total)*100));
			ire.nutritionData().protein().add(food.getNutrition()*Mth.ceil((protein/total)*100));
			ire.nutritionData().fat().add(food.getNutrition()*Mth.ceil((fats/total)*100));
		}
	}
}
