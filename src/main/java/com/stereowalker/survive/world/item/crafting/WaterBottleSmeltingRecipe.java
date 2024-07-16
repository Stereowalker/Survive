package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.survive.world.item.alchemy.SPotions;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

public class WaterBottleSmeltingRecipe extends SmeltingRecipe {

	public WaterBottleSmeltingRecipe(String pGroup, CookingBookCategory pCategory, Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime) {
		super(pGroup, pCategory, Ingredient.of(PotionContents.createItemStack(Items.POTION, Potions.WATER)), PotionContents.createItemStack(Items.POTION, SPotions.PURIFIED_WATER.holder()), pExperience, pCookingTime);
	}
	
	public WaterBottleSmeltingRecipe(String pGroup, CookingBookCategory pCategory, float pExperience, int pCookingTime) {
		this(pGroup, pCategory, null, null, pExperience, pCookingTime);
	}

	@Override
	public boolean matches(SingleRecipeInput pInv, Level pLevel) {
		if (pInv.getItem(0).has(DataComponents.POTION_CONTENTS) && pInv.getItem(0).get(DataComponents.POTION_CONTENTS).potion().get() == Potions.WATER) return this.ingredient.test(pInv.getItem(0)); else return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SRecipeSerializer.PURIFIED_WATER_BOTTLE;
	}

}
