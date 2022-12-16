package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

@RegistryHolder(registry = RecipeSerializer.class)
public class SRecipeSerializer {
	@RegistryObject("crafting_charcoal_filter")
	public static final SimpleRecipeSerializer<CharcoalFilterRecipe> CRAFTING_SPECIAL_CHARCOAL_FILTERING = new SimpleRecipeSerializer<>(CharcoalFilterRecipe::new);
	@RegistryObject("crafting_canteen_filling")
	public static final SimpleRecipeSerializer<CanteenFillingRecipe> CRAFTING_SPECIAL_CANTEEN_FILLING = new SimpleRecipeSerializer<>(CanteenFillingRecipe::new);
	@RegistryObject("crafting_shapeless_with_purified_water")
	public static final PurifiedWaterCraftingSerializer CRAFTING_SHAPELESS_WITH_PURIFIED_WATER = new PurifiedWaterCraftingSerializer();
	@RegistryObject("purified_water_bottle")
	public static final WaterBottleCookingSerializer PURIFIED_WATER_BOTTLE = new WaterBottleCookingSerializer(WaterBottleSmeltingRecipe::new, 200);
}
