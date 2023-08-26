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
import com.stereowalker.survive.json.BiomeJsonHolder;
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
public class BiomeDataManager implements IResourceReloadListener<Map<ResourceLocation, BiomeJsonHolder>> {
	@Override
	public CompletableFuture<Map<ResourceLocation, BiomeJsonHolder>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, BiomeJsonHolder> drinkMap = new HashMap<>();

			for (Entry<ResourceLocation, Resource> resource : manager.listResources("survive_modifiers/biomes", (s) -> s.toString().endsWith(".json")).entrySet()) {
				ResourceLocation blockId = new ResourceLocation(
						resource.getKey().getNamespace(),
						resource.getKey().getPath().replace("survive_modifiers/biomes/", "").replace(".json", "")
						);

				if (!ForgeRegistries.BIOMES.containsKey(blockId)) {
					Survive.getInstance().getLogger().warn("Did not find biome " + blockId + " in the forge registry. This is a temporary warning and will be removed after the fabric release");
				}
				try {
					try (InputStream stream = resource.getValue().open(); 
							InputStreamReader reader = new InputStreamReader(stream)) {

						JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
						BiomeJsonHolder biomeData = new BiomeJsonHolder(blockId, object);
						drinkMap.put(blockId, biomeData);
						Survive.getInstance().getLogger().info("Registered modifier for the biome \""+blockId+"\"");
					}
				} catch (Exception e) {
					Survive.getInstance().getLogger().warn("Error reading the biomes temperature modifier for the biome " + blockId + "!", e);
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, BiomeJsonHolder> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
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
