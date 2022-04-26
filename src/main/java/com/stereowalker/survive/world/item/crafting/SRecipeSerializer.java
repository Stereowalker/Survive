package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

@RegistryHolder(registry = RecipeSerializer.class)
public class SRecipeSerializer {
	@RegistryObject("crafting_charcoal_filter")
	public static final SimpleRecipeSerializer<CharcoalFilterRecipe> CRAFTING_SPECIAL_CHARCOAL_FILTERING = new SimpleRecipeSerializer<>(CharcoalFilterRecipe::new);
	@RegistryObject("purified_water_bottle")
	public static final WaterBottleCookingSerializer PURIFIED_WATER_BOTTLE = new WaterBottleCookingSerializer(WaterBottleSmeltingRecipe::new, 200);
}
