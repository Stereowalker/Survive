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
import com.stereowalker.survive.json.PotionJsonHolder;
import com.stereowalker.unionlib.resource.IResourceReloadListener;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Maps marker type to texture.
 * @author Hunternif
 */
public class PotionDrinkDataManager implements IResourceReloadListener<Map<ResourceLocation, PotionJsonHolder>> {
	@Override
	public CompletableFuture<Map<ResourceLocation, PotionJsonHolder>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, PotionJsonHolder> drinkMap = new HashMap<>();

			for (Entry<ResourceLocation, Resource> resource : manager.listResources("survive_modifiers/consumables/potions", (s) -> s.toString().endsWith(".json")).entrySet()) {
				ResourceLocation drinkId = new ResourceLocation(
						resource.getKey().getNamespace(),
						resource.getKey().getPath().replace("survive_modifiers/consumables/potions/", "").replace(".json", "")
						);

				if (ForgeRegistries.POTIONS.containsKey(drinkId)) {
					try {
						try (InputStream stream = resource.getValue().open(); 
								InputStreamReader reader = new InputStreamReader(stream)) {
							
							JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
							PotionJsonHolder drinkData = new PotionJsonHolder(drinkId, object);
							Survive.getInstance().getLogger().info("Found potion drink data for "+drinkId);
							
							drinkMap.put(drinkId, drinkData);
						}
					} catch (Exception e) {
						Survive.getInstance().getLogger().warn("Error reading potion drink data " + drinkId + "!", e);
					}
				} else {
					Survive.getInstance().getLogger().warn("No such potion exists with the id " + drinkId + "!");
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, PotionJsonHolder> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation drinkId : data.keySet()) {
				Survive.registerDrinkDataForPotion(drinkId, data.get(drinkId));
			}
		});
	}
}
