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
import com.stereowalker.survive.util.data.BiomeTemperatureData;
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
public class BiomeTemperatureDataManager implements IResourceReloadListener<Map<ResourceLocation, BiomeTemperatureData>> {
	private static final JsonParser parser = new JsonParser();

	@Override
	public CompletableFuture<Map<ResourceLocation, BiomeTemperatureData>> load(IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, BiomeTemperatureData> drinkMap = new HashMap<>();

			for (ResourceLocation id : manager.getAllResourceLocations("survive_modifiers/biomes", (s) -> s.endsWith(".json"))) {
				ResourceLocation blockId = new ResourceLocation(
						id.getNamespace(),
						id.getPath().replace("survive_modifiers/biomes/", "").replace(".json", "")
						);

				if (ForgeRegistries.BIOMES.containsKey(blockId)) {
					try {
						IResource resource = manager.getResource(id);
						try (InputStream stream = resource.getInputStream(); 
								InputStreamReader reader = new InputStreamReader(stream)) {
							
							JsonObject object = parser.parse(reader).getAsJsonObject();
							BiomeTemperatureData biomeData = new BiomeTemperatureData(blockId, object);
							Survive.getInstance().LOGGER.info("Found biome temperature modifier for the biome "+blockId);
							
							drinkMap.put(blockId, biomeData);
						}
					} catch (Exception e) {
						Survive.getInstance().LOGGER.warn("Error reading the biomes temperature modifier for the biome " + blockId + "!", e);
					}
				} else {
					Survive.getInstance().LOGGER.warn("No such biome exists with the block id " + blockId + "!");
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, BiomeTemperatureData> data, IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation drinkId : data.keySet()) {
				Survive.registerBiomeTemperatures(drinkId, data.get(drinkId));
			}
		});
	}
}
