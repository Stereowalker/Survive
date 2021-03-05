package com.stereowalker.survive.resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.util.data.ConsummableData;

import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Loads the item drink data from json
 * @author Stereowalker
 */
public class ItemConsummableDataManager implements IResourceReloadListener<Map<ResourceLocation, ConsummableData>> {
	private static final JsonParser parser = new JsonParser();

	@Override
	public CompletableFuture<Map<ResourceLocation, ConsummableData>> load(IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, ConsummableData> drinkMap = new HashMap<>();

			for (ResourceLocation id : manager.getAllResourceLocations("survive_modifiers/consumables/items", (s) -> s.endsWith(".json"))) {
				ResourceLocation drinkId = new ResourceLocation(
						id.getNamespace(),
						id.getPath().replace("survive_modifiers/consumables/items/", "").replace(".json", "")
						);

				if (ForgeRegistries.ITEMS.containsKey(drinkId)) {
					try {
						IResource resource = manager.getResource(id);
						try (InputStream stream = resource.getInputStream(); 
								InputStreamReader reader = new InputStreamReader(stream)) {

							JsonObject object = parser.parse(reader).getAsJsonObject();
							ConsummableData drinkData = new ConsummableData(drinkId, object);

							if (ForgeRegistries.ITEMS.getValue(drinkId).food != null && Survive.defaultFoodMap.containsKey(drinkId)) {
								ForgeRegistries.ITEMS.getValue(drinkId).food.value = drinkData.overwritesDefaultHunger() ? drinkData.getHungerAmount() : Survive.defaultFoodMap.get(drinkId).value;
								ForgeRegistries.ITEMS.getValue(drinkId).food.saturation = drinkData.overwritesDefaultSaturation() ? drinkData.getSaturationAmount() : Survive.defaultFoodMap.get(drinkId).saturation;

								Pair<EffectInstance, Float> defaultEffect = null;
								Pair<EffectInstance, Float> itemEffect = null;
								for (Pair<EffectInstance, Float> effect : ForgeRegistries.ITEMS.getValue(drinkId).food.getEffects()) {
									if (effect.getLeft().getPotion() == Effects.HUNGER) {
										itemEffect = effect;
									}
								}

								for (Pair<EffectInstance, Float> effect : Survive.defaultFoodMap.get(drinkId).getEffects()) {
									if (effect.getLeft().getPotion() == Effects.HUNGER) {
										defaultEffect = effect;
									}
								}

								if (itemEffect != null) {
									ForgeRegistries.ITEMS.getValue(drinkId).food.getEffects().remove(itemEffect);
								}

								if (drinkData.overwritesDefaultHungerChance()) {
									ForgeRegistries.ITEMS.getValue(drinkId).food.getEffects().add(Pair.of(new EffectInstance(Effects.HUNGER, 30*20, 0), drinkData.getHungerChance()));
								} else if (defaultEffect != null) {
									ForgeRegistries.ITEMS.getValue(drinkId).food.getEffects().add(defaultEffect);
								}
							}
							if (drinkData.overwritesDefaultFood() && ForgeRegistries.ITEMS.getValue(drinkId).food == null) {
								ForgeRegistries.ITEMS.getValue(drinkId).food = (new Food.Builder()).hunger(drinkData.getHungerAmount()).saturation(drinkData.getSaturationAmount()).effect(() -> new EffectInstance(Effects.HUNGER, 600, 0), drinkData.getHungerChance()).build();
							}
							if (!Survive.defaultFoodMap.containsKey(drinkId) && !drinkData.overwritesDefaultFood() && ForgeRegistries.ITEMS.getValue(drinkId).food != null) {
								ForgeRegistries.ITEMS.getValue(drinkId).food = null;
							}
							Survive.LOGGER.info("Found item consummable data for "+drinkId);
							
							drinkMap.put(drinkId, drinkData);
						}
					} catch (Exception e) {
						Survive.LOGGER.warn("Error reading item drink data " + drinkId + "!", e);
					}
				} else {
					Survive.LOGGER.warn("No such item exists with the id " + drinkId + "!");
				}
			}

			return drinkMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, ConsummableData> data, IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation drinkId : data.keySet()) {
				Survive.registerDrinkDataForItem(drinkId, data.get(drinkId));
			}
		});
	}
}
