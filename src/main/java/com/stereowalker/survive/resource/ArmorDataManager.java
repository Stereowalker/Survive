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
import com.stereowalker.survive.json.ArmorJsonHolder;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.unionlib.resource.IResourceReloadListener;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Maps marker type to texture.
 * @author Stereowalker
 */
public class ArmorDataManager implements IResourceReloadListener<Map<Identifier, ArmorJsonHolder>> {
	@Override
	public CompletableFuture<Map<Identifier, ArmorJsonHolder>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Identifier, ArmorJsonHolder> drinkMap = new HashMap<>();

			for (Entry<Identifier, Resource> resource : manager.listResources("survive_modifiers/armors", (s) -> s.toString().endsWith(".json")).entrySet()) {
				Identifier drinkId = VersionHelper.toLoc(
						resource.getKey().getNamespace(),
						resource.getKey().getPath().replace("survive_modifiers/armors/", "").replace(".json", "")
						);

				if (ForgeRegistries.ITEMS.containsKey(drinkId)) {
					try {
						try (InputStream stream = resource.getValue().open(); 
								InputStreamReader reader = new InputStreamReader(stream)) {
							
							JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
							ArmorJsonHolder drinkData = new ArmorJsonHolder(drinkId, object);
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
	public CompletableFuture<Void> apply(Map<Identifier, ArmorJsonHolder> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			DataMaps.Server.syncedClients.clear();
			for (Identifier drinkId : data.keySet()) {
				Survive.registerArmorTemperatures(drinkId, data.get(drinkId));
			}
		});
	}

	@Override
	public Identifier id() {
		return VersionHelper.toLoc("survive:armor_data");
	}
}
