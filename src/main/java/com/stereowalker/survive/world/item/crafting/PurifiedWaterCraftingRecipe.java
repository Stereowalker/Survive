package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.unionlib.world.item.crafting.NoRemainderShaplessRecipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PurifiedWaterCraftingRecipe extends NoRemainderShaplessRecipe {

	public PurifiedWaterCraftingRecipe(ResourceLocation pId, String pGroup, ItemStack pResult, NonNullList<Ingredient> pIngredients) {
		super(pId, pGroup, pResult, pIngredients);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SRecipeSerializer.CRAFTING_SHAPELESS_WITH_PURIFIED_WATER;
	}

}
