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
import com.stereowalker.survive.util.data.ConsummableData;
import com.stereowalker.unionlib.resource.IResourceReloadListener;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Maps marker type to texture.
 * @author Hunternif
 */
public class PotionDrinkDataManager implements IResourceReloadListener<Map<ResourceLocation, ConsummableData>> {
	private static final JsonParser parser = new JsonParser();

	@Override
	public CompletableFuture<Map<ResourceLocation, ConsummableData>> load(IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, ConsummableData> drinkMap = new HashMap<>();

			for (ResourceLocation id : manager.getAllResourceLocations("survive_modifiers/consumables/potions", (s) -> s.endsWith(".json"))) {
				ResourceLocation drinkId = new ResourceLocation(
						id.getNamespace(),
						id.getPath().replace("survive_modifiers/consumables/potions/", "").replace(".json", "")
						);

				if (ForgeRegistries.POTION_TYPES.containsKey(drinkId)) {
					try {
						IResource resource = manager.getResource(id);
						try (InputStream stream = resource.getInputStream(); 
								InputStreamReader reader = new InputStreamReader(stream)) {
							
							JsonObject object = parser.parse(reader).getAsJsonObject();
							ConsummableData drinkData = new ConsummableData(drinkId, object);
							Survive.getInstance().LOGGER.info("Found potion drink data for "+drinkId);
							
							drinkMap.put(drinkId, drinkData);
						}
					} catch (Exception e) {
						Survive.getInstance().LOGGER.warn("Error reading potion drink data " + drinkId + "!", e);
					}
				} else {
					Survive.getInstance().LOGGER.warn("No such potion exists with the id " + drinkId + "!");
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, ConsummableData> data, IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation drinkId : data.keySet()) {
				Survive.registerDrinkDataForPotion(drinkId, data.get(drinkId));
			}
		});
	}
}
