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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Maps marker type to texture.
 * @author Hunternif
 */
public class FluidDataManager implements IResourceReloadListener<Map<ResourceLocation, FluidJsonHolder>> {
	@Override
	public CompletableFuture<Map<ResourceLocation, FluidJsonHolder>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, FluidJsonHolder> drinkMap = new HashMap<>();

			for (Entry<ResourceLocation, Resource> resource : manager.listResources("survive_modifiers/fluids", (s) -> s.toString().endsWith(".json")).entrySet()) {
				ResourceLocation drinkId = new ResourceLocation(
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
	public CompletableFuture<Void> apply(Map<ResourceLocation, FluidJsonHolder> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation drinkId : data.keySet()) {
				DataMaps.Server.fluid.put(drinkId, data.get(drinkId));
			}
		});
	}
}
