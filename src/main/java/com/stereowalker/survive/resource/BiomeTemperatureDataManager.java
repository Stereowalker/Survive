package com.stereowalker.survive.resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.json.BiomeTemperatureJsonHolder;
import com.stereowalker.unionlib.resource.IResourceReloadListener;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Loads block temperatures from json
 * @author Stereowalker
 */
public class BiomeTemperatureDataManager implements IResourceReloadListener<Map<ResourceLocation, BiomeTemperatureJsonHolder>> {
	@Override
	public CompletableFuture<Map<ResourceLocation, BiomeTemperatureJsonHolder>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, BiomeTemperatureJsonHolder> drinkMap = new HashMap<>();

			for (Entry<ResourceLocation, Resource> resource : manager.listResources("survive_modifiers/biomes", (s) -> s.toString().endsWith(".json")).entrySet()) {
				ResourceLocation blockId = new ResourceLocation(
						resource.getKey().getNamespace(),
						resource.getKey().getPath().replace("survive_modifiers/biomes/", "").replace(".json", "")
						);

				if (ForgeRegistries.BIOMES.containsKey(blockId)) {
					try {
						try (InputStream stream = resource.getValue().open(); 
								InputStreamReader reader = new InputStreamReader(stream)) {
							
							JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
							BiomeTemperatureJsonHolder biomeData = new BiomeTemperatureJsonHolder(blockId, object);
							Survive.getInstance().getLogger().info("Found biome temperature modifier for the biome "+blockId);
							
							drinkMap.put(blockId, biomeData);
						}
					} catch (Exception e) {
						Survive.getInstance().getLogger().warn("Error reading the biomes temperature modifier for the biome " + blockId + "!", e);
					}
				} else {
					Survive.getInstance().getLogger().warn("No such biome exists with the block id " + blockId + "!");
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, BiomeTemperatureJsonHolder> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation drinkId : data.keySet()) {
				Survive.registerBiomeTemperatures(drinkId, data.get(drinkId));
			}
		});
	}

	@Override
	public ResourceLocation id() {
		return new ResourceLocation("survive:biome_data");
	}
}
