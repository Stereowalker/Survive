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
import com.stereowalker.survive.util.data.EntityTemperatureData;
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
public class EntityTemperatureDataManager implements IResourceReloadListener<Map<ResourceLocation, EntityTemperatureData>> {
	private static final JsonParser parser = new JsonParser();

	@Override
	public CompletableFuture<Map<ResourceLocation, EntityTemperatureData>> load(IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, EntityTemperatureData> drinkMap = new HashMap<>();

			for (ResourceLocation id : manager.getAllResourceLocations("survive_modifiers/entities", (s) -> s.endsWith(".json"))) {
				ResourceLocation entityId = new ResourceLocation(
						id.getNamespace(),
						id.getPath().replace("survive_modifiers/entities/", "").replace(".json", "")
						);

				if (ForgeRegistries.ENTITIES.containsKey(entityId)) {
					try {
						IResource resource = manager.getResource(id);
						try (InputStream stream = resource.getInputStream(); 
								InputStreamReader reader = new InputStreamReader(stream)) {
							
							JsonObject object = parser.parse(reader).getAsJsonObject();
							EntityTemperatureData blockData = new EntityTemperatureData(entityId, object);
							Survive.getInstance().getLogger().info("Found entity temperature modifier for the entity "+entityId);
							
							drinkMap.put(entityId, blockData);
						}
					} catch (Exception e) {
						Survive.getInstance().getLogger().warn("Error reading the entity temperature modifier for the entity " + entityId + "!", e);
					}
				} else {
					Survive.getInstance().getLogger().warn("No such entity exists with the entity id " + entityId + "!");
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, EntityTemperatureData> data, IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation drinkId : data.keySet()) {
				Survive.registerEntityTemperatures(drinkId, data.get(drinkId));
			}
		});
	}
}
