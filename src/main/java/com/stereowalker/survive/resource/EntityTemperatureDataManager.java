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
import com.stereowalker.survive.json.EntityTemperatureJsonHolder;
import com.stereowalker.unionlib.resource.IResourceReloadListener;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

/**
 * Loads block temperatures from json
 * @author Stereowalker
 */
public class EntityTemperatureDataManager implements IResourceReloadListener<Map<Identifier, EntityTemperatureJsonHolder>> {
	@Override
	public CompletableFuture<Map<Identifier, EntityTemperatureJsonHolder>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Identifier, EntityTemperatureJsonHolder> drinkMap = new HashMap<>();

			for (Entry<Identifier, Resource> resource : manager.listResources("survive_modifiers/entities", (s) -> s.toString().endsWith(".json")).entrySet()) {
				Identifier entityId = VersionHelper.toLoc(
						resource.getKey().getNamespace(),
						resource.getKey().getPath().replace("survive_modifiers/entities/", "").replace(".json", "")
						);

				if (BuiltInRegistries.ENTITY_TYPE.containsKey(entityId)) {
					try {
						try (InputStream stream = resource.getValue().open(); 
								InputStreamReader reader = new InputStreamReader(stream)) {
							
							JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
							EntityTemperatureJsonHolder blockData = new EntityTemperatureJsonHolder(entityId, object);
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
	public CompletableFuture<Void> apply(Map<Identifier, EntityTemperatureJsonHolder> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (Identifier drinkId : data.keySet()) {
				Survive.registerEntityTemperatures(drinkId, data.get(drinkId));
			}
		});
	}

	@Override
	public Identifier id() {
		return VersionHelper.toLoc("survive:entity_data");
	}
}
