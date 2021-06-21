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
import com.stereowalker.survive.util.data.ArmorData;
import com.stereowalker.unionlib.resource.IResourceReloadListener;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Maps marker type to texture.
 * @author Stereowalker
 */
public class ArmorDataManager implements IResourceReloadListener<Map<ResourceLocation, ArmorData>> {
	private static final JsonParser parser = new JsonParser();

	@Override
	public CompletableFuture<Map<ResourceLocation, ArmorData>> load(IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, ArmorData> drinkMap = new HashMap<>();

			for (ResourceLocation id : manager.getAllResourceLocations("survive_modifiers/armors", (s) -> s.endsWith(".json"))) {
				ResourceLocation drinkId = new ResourceLocation(
						id.getNamespace(),
						id.getPath().replace("survive_modifiers/armors/", "").replace(".json", "")
						);

				if (ForgeRegistries.ITEMS.containsKey(drinkId)) {
					try {
						IResource resource = manager.getResource(id);
						try (InputStream stream = resource.getInputStream(); 
								InputStreamReader reader = new InputStreamReader(stream)) {
							
							JsonObject object = parser.parse(reader).getAsJsonObject();
							ArmorData drinkData = new ArmorData(drinkId, object);
							Survive.getInstance().getLogger().info("Found armor modifier for the item {}", drinkId);
							
							drinkMap.put(drinkId, drinkData);
						}
					} catch (Exception e) {
						Survive.getInstance().getLogger().warn("Error reading the armor modifier for the item {}!", drinkId, e);
					}
				} else {
					Survive.getInstance().getLogger().warn("No such armor exists with the item id {}!", drinkId);
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, ArmorData> data, IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation drinkId : data.keySet()) {
				Survive.registerArmorTemperatures(drinkId, data.get(drinkId));
			}
		});
	}
}
