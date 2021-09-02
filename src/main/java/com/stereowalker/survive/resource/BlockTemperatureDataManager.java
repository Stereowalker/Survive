package com.stereowalker.survive.resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.util.data.BlockTemperatureData;
import com.stereowalker.unionlib.resource.IResourceReloadListener;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Loads block temperatures from json
 * @author Stereowalker
 */
public class BlockTemperatureDataManager implements IResourceReloadListener<Map<ResourceLocation, BlockTemperatureData>> {
	private static final JsonParser parser = new JsonParser();

	@Override
	public CompletableFuture<Map<ResourceLocation, BlockTemperatureData>> load(IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, BlockTemperatureData> drinkMap = new HashMap<>();

			for (ResourceLocation id : manager.getAllResourceLocations("survive_modifiers/blocks", (s) -> s.endsWith(".json"))) {
				ResourceLocation blockId = new ResourceLocation(
						id.getNamespace(),
						id.getPath().replace("survive_modifiers/blocks/", "").replace(".json", "")
						);

				if (ForgeRegistries.BLOCKS.containsKey(blockId)) {
					try {
						IResource resource = manager.getResource(id);
						try (InputStream stream = resource.getInputStream(); 
								InputStreamReader reader = new InputStreamReader(stream)) {
							
							JsonObject object = parser.parse(reader).getAsJsonObject();
							BlockTemperatureData blockData = new BlockTemperatureData(blockId, object);
							Survive.getInstance().getLogger().info("Found block temperature modifier for the item "+blockId);
							
							drinkMap.put(blockId, blockData);
						}
					} catch (Exception e) {
						Survive.getInstance().getLogger().warn("Error reading the block temperature modifier for the item " + blockId + "!", e);
					}
				} else {
					Survive.getInstance().getLogger().warn("No such block exists with the block id " + blockId + "!");
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, BlockTemperatureData> data, IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation drinkId : data.keySet()) {
				Survive.registerBlockTemperatures(drinkId, data.get(drinkId));
			}
		});
	}
}
