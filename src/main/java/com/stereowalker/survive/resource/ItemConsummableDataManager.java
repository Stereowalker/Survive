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
import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.json.FoodJsonHolder;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.unionlib.resource.IResourceReloadListener;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Loads the item drink data from json
 * @author Stereowalker
 */
public class ItemConsummableDataManager implements IResourceReloadListener<Map<ResourceLocation, FoodJsonHolder>> {
	@Override
	public CompletableFuture<Map<ResourceLocation, FoodJsonHolder>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, FoodJsonHolder> drinkMap = new HashMap<>();

			for (Entry<ResourceLocation, Resource> resource : manager.listResources("survive_modifiers/consumables/items", (s) -> s.toString().endsWith(".json")).entrySet()) {
				ResourceLocation drinkId = new ResourceLocation(
						resource.getKey().getNamespace(),
						resource.getKey().getPath().replace("survive_modifiers/consumables/items/", "").replace(".json", "")
						);

				if (ForgeRegistries.ITEMS.containsKey(drinkId)) {
					try {
						try (InputStream stream = resource.getValue().open(); 
								InputStreamReader reader = new InputStreamReader(stream)) {

							JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
							FoodJsonHolder drinkData = new FoodJsonHolder(drinkId, object);

							//Overrides the current food if it is edible. Omitting any modifiers will set that modifier to what is is by default
							if (ForgeRegistries.ITEMS.getValue(drinkId).isEdible() && DataMaps.Server.defaultFood.containsKey(drinkId)) {
								ForgeRegistries.ITEMS.getValue(drinkId).getFoodProperties().nutrition = drinkData.overwritesDefaultHunger() ? drinkData.getHungerAmount() : DataMaps.Server.defaultFood.get(drinkId).getNutrition();
								ForgeRegistries.ITEMS.getValue(drinkId).getFoodProperties().saturationModifier = drinkData.overwritesDefaultSaturation() ? drinkData.getSaturationAmount() : DataMaps.Server.defaultFood.get(drinkId).getSaturationModifier();

								Pair<MobEffectInstance, Float> defaultEffect = null;
								Pair<MobEffectInstance, Float> itemEffect = null;
								for (Pair<MobEffectInstance, Float> effect : ForgeRegistries.ITEMS.getValue(drinkId).getFoodProperties().getEffects()) {
									if (effect.getFirst().getEffect() == MobEffects.HUNGER) {
										itemEffect = effect;
									}
								}

								for (Pair<MobEffectInstance, Float> effect : DataMaps.Server.defaultFood.get(drinkId).getEffects()) {
									if (effect.getFirst().getEffect() == MobEffects.HUNGER) {
										defaultEffect = effect;
									}
								}

								if (itemEffect != null) {
									ForgeRegistries.ITEMS.getValue(drinkId).getFoodProperties().getEffects().remove(itemEffect);
								}

								if (drinkData.overwritesDefaultHungerChance()) {
									ForgeRegistries.ITEMS.getValue(drinkId).getFoodProperties().getEffects().add(Pair.of(new MobEffectInstance(MobEffects.HUNGER, 30*20, 0), drinkData.getHungerChance()));
								} else if (defaultEffect != null) {
									ForgeRegistries.ITEMS.getValue(drinkId).getFoodProperties().getEffects().add(defaultEffect);
								}
							}
							//Makes any item edible if it naturally isn't supposed to be edible
							if (drinkData.overwritesDefaultFood() && !ForgeRegistries.ITEMS.getValue(drinkId).isEdible()) {
								ForgeRegistries.ITEMS.getValue(drinkId).foodProperties = (new FoodProperties.Builder()).nutrition(drinkData.getHungerAmount()).saturationMod(drinkData.getSaturationAmount()).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), drinkData.getHungerChance()).build();
							}
							//Makes non edible items that were edible no longer edible. Basically, if a datapack that made stone edible was removed, this will reset the edibility of stone
							if (!DataMaps.Server.defaultFood.containsKey(drinkId) && !drinkData.overwritesDefaultFood() && ForgeRegistries.ITEMS.getValue(drinkId).isEdible()) {
								ForgeRegistries.ITEMS.getValue(drinkId).foodProperties = null;
							}
							Survive.getInstance().getLogger().info("Found item consummable data for "+drinkId);
							
							drinkMap.put(drinkId, drinkData);
						}
					} catch (Exception e) {
						Survive.getInstance().getLogger().warn("Error reading item drink data " + drinkId + "!", e);
					}
				} else {
					Survive.getInstance().getLogger().warn("No such item exists with the id " + drinkId + "!");
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, FoodJsonHolder> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation drinkId : data.keySet()) {
				Survive.registerDrinkDataForItem(drinkId, data.get(drinkId));
			}
		});
	}
}
