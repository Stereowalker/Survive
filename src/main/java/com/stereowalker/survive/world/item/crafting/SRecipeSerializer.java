package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SRecipeSerializer {
	@RegistryObject("crafting_charcoal_filter")
	public static final RecipeSerializer<CharcoalFilterRecipe> CRAFTING_SPECIAL_CHARCOAL_FILTERING = new RecipeSerializer<>(CharcoalFilterRecipe.MAP_CODEC, CharcoalFilterRecipe.STREAM_CODEC);
	@RegistryObject("crafting_canteen_filling")
	public static final RecipeSerializer<CanteenFillingRecipe> CRAFTING_SPECIAL_CANTEEN_FILLING = new RecipeSerializer<>(CanteenFillingRecipe.MAP_CODEC, CanteenFillingRecipe.STREAM_CODEC);
	@RegistryObject("crafting_player_status_book")
	public static final RecipeSerializer<PlayerStatusBookRecipe> CRAFTING_PLAYER_STATUS_BOOK = new RecipeSerializer<>(PlayerStatusBookRecipe.MAP_CODEC, PlayerStatusBookRecipe.STREAM_CODEC);
	@RegistryObject("crafting_shapeless_with_purified_water")
	public static final RecipeSerializer<PurifiedWaterCraftingRecipe> CRAFTING_SHAPELESS_WITH_PURIFIED_WATER = new RecipeSerializer<>(PurifiedWaterCraftingRecipe.MAP_CODEC, PurifiedWaterCraftingRecipe.STREAM_CODEC);;
	@RegistryObject("purified_water_bottle")
	public static final RecipeSerializer<SmeltingRecipe> PURIFIED_WATER_BOTTLE = new RecipeSerializer<>(WaterBottleSmeltingRecipe.MAP_CODEC, WaterBottleSmeltingRecipe.STREAM_CODEC);
}
