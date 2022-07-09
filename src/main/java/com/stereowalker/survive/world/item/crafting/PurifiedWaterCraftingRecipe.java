package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.unionlib.world.item.crafting.NoRemainderShaplessRecipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PurifiedWaterCraftingRecipe extends NoRemainderShaplessRecipe {

	public PurifiedWaterCraftingRecipe(ResourceLocation pId, String pGroup, ItemStack pResult, NonNullList<Ingredient> pIngredients) {
		super(pId, pGroup, pResult, add(pIngredients));
	}
	
	private static NonNullList<Ingredient> add(NonNullList<Ingredient> ing){
		ing.add(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), SPotions.PURIFIED_WATER)));
		return ing;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SRecipeSerializer.CRAFTING_SHAPELESS_WITH_PURIFIED_WATER;
	}

}
