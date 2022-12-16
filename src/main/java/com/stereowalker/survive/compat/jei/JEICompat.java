package com.stereowalker.survive.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.alchemy.SPotions;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

@JeiPlugin
public class JEICompat implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return Survive.getInstance().location("recipe_handler");
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IStackHelper stackHelper = jeiHelpers.getStackHelper();
		List<CraftingRecipe> collection = new ArrayList<CraftingRecipe>();
		collection.add(new ShapelessRecipe(Survive.getInstance().location("purified_water_bucket_from_charcoal_filtering"), "charcoal_filtering", 
				new ItemStack(SItems.PURIFIED_WATER_BUCKET), 
				NonNullList.of(Ingredient.EMPTY, 
						Ingredient.of(SItems.CHARCOAL_FILTER), 
						Ingredient.of(Items.WATER_BUCKET))));
		collection.add(new ShapelessRecipe(Survive.getInstance().location("purified_water_bowl_from_charcoal_filtering"), "charcoal_filtering", 
				new ItemStack(SItems.PURIFIED_WATER_BOWL), 
				NonNullList.of(Ingredient.EMPTY, 
						Ingredient.of(SItems.CHARCOAL_FILTER), 
						Ingredient.of(SItems.WATER_BOWL))));
		collection.add(new ShapelessRecipe(Survive.getInstance().location("purified_water_canteen_from_charcoal_filtering"), "charcoal_filtering", 
				CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), Survive.THIRST_CONFIG.canteen_fill_amount, SPotions.PURIFIED_WATER), 
				NonNullList.of(Ingredient.EMPTY, 
						Ingredient.of(SItems.CHARCOAL_FILTER), 
						Ingredient.of(CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), Survive.THIRST_CONFIG.canteen_fill_amount, Potions.WATER)))));
		collection.add(new ShapelessRecipe(Survive.getInstance().location("purified_water_bottle_from_charcoal_filtering"), "charcoal_filtering", 
				PotionUtils.setPotion(new ItemStack(Items.POTION), SPotions.PURIFIED_WATER),
				NonNullList.of(Ingredient.EMPTY, 
						Ingredient.of(SItems.CHARCOAL_FILTER), 
						Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)))));
		collection.addAll(CanteenFillingRecipeMaker.createRecipes(stackHelper));
		registration.addRecipes(RecipeTypes.CRAFTING, collection);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.useNbtForSubtypes(SItems.FILLED_CANTEEN);
	}

}
