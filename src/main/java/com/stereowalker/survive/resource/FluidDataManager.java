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
import com.stereowalker.survive.json.FluidJsonHolder;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.unionlib.resource.IResourceReloadListener;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidDataManager implements IResourceReloadListener<Map<Identifier, FluidJsonHolder>> {
	@Override
	public CompletableFuture<Map<Identifier, FluidJsonHolder>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Identifier, FluidJsonHolder> drinkMap = new HashMap<>();

			for (Entry<Identifier, Resource> resource : manager.listResources("survive_modifiers/fluids", (s) -> s.toString().endsWith(".json")).entrySet()) {
				Identifier drinkId = VersionHelper.toLoc(
						resource.getKey().getNamespace(),
						resource.getKey().getPath().replace("survive_modifiers/fluids/", "").replace(".json", "")
						);

				if (ForgeRegistries.FLUIDS.containsKey(drinkId)) {
					try {
						try (InputStream stream = resource.getValue().open(); 
								InputStreamReader reader = new InputStreamReader(stream)) {
							
							JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
							FluidJsonHolder drinkData = new FluidJsonHolder(drinkId, object);
							Survive.getInstance().getLogger().info("Found fluid data for "+drinkId);
							
							drinkMap.put(drinkId, drinkData);
						}
					} catch (Exception e) {
						Survive.getInstance().getLogger().warn("Error reading fluid data " + drinkId + "!", e);
					}
				} else {
					Survive.getInstance().getLogger().warn("No such fluid exists with the id " + drinkId + "!");
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<Identifier, FluidJsonHolder> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (Identifier drinkId : data.keySet()) {
				DataMaps.Server.fluid.put(drinkId, data.get(drinkId));
			}
		});
	}

	@Override
	public Identifier id() {
		return VersionHelper.toLoc("survive:fluid_data");
	}
}
