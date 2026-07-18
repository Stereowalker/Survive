package com.stereowalker.survive.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.unionlib.util.LoaderHelper;
import com.stereowalker.unionlib.util.VersionHelper;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class JEICompat implements IModPlugin {

	@Override
	public Identifier getPluginUid() {
		return VersionHelper.toLoc("survive:recipe_handler");
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IStackHelper stackHelper = jeiHelpers.getStackHelper();
		List<RecipeHolder<CraftingRecipe>> collection = new ArrayList<RecipeHolder<CraftingRecipe>>();
		collection.add(new RecipeHolder<CraftingRecipe>(ResourceKey.create(Registries.RECIPE, Survive.getInstance().location("player_status_book")), new ShapelessRecipe(RecipeBuilder.createCraftingCommonInfo(true), 
				RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, "status_book"), ItemStackTemplate.fromNonEmptyStack(Survive.convertToPlayerStatusBook(new ItemStack(Items.WRITTEN_BOOK))), 
				List.of(
						Ingredient.of(SItems.THERMOMETER), 
						Ingredient.of(Items.WRITTEN_BOOK)))));
		collection.add(new RecipeHolder<CraftingRecipe>(ResourceKey.create(Registries.RECIPE, Survive.getInstance().location("purified_water_bucket_from_charcoal_filtering")), new ShapelessRecipe(RecipeBuilder.createCraftingCommonInfo(true), 
				RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, "charcoal_filtering"), new ItemStackTemplate(SItems.PURIFIED_WATER_BUCKET), 
				List.of(
						Ingredient.of(SItems.USED_CHARCOAL_FILTER, SItems.CHARCOAL_FILTER), 
						Ingredient.of(Items.WATER_BUCKET)))));
		collection.add(new RecipeHolder<CraftingRecipe>(ResourceKey.create(Registries.RECIPE, Survive.getInstance().location("purified_water_bowl_from_charcoal_filtering")), new ShapelessRecipe(RecipeBuilder.createCraftingCommonInfo(true), 
				RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, "charcoal_filtering"), new ItemStackTemplate(SItems.PURIFIED_WATER_BOWL), 
				List.of(
						Ingredient.of(SItems.USED_CHARCOAL_FILTER, SItems.CHARCOAL_FILTER), 
						Ingredient.of(SItems.WATER_BOWL)))));
		collection.add(new RecipeHolder<CraftingRecipe>(ResourceKey.create(Registries.RECIPE, Survive.getInstance().location("purified_water_canteen_from_charcoal_filtering")), new ShapelessRecipe(RecipeBuilder.createCraftingCommonInfo(true), 
				RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, "charcoal_filtering"), ItemStackTemplate.fromNonEmptyStack(CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), Survive.THIRST_CONFIG.canteen_fill_amount, SPotions.PURIFIED_WATER.holder())), 
				List.of(
						Ingredient.of(SItems.USED_CHARCOAL_FILTER, SItems.CHARCOAL_FILTER), 
						LoaderHelper.createStackIngredient(CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), Survive.THIRST_CONFIG.canteen_fill_amount, Potions.WATER))))));
		collection.add(new RecipeHolder<CraftingRecipe>(ResourceKey.create(Registries.RECIPE, Survive.getInstance().location("purified_netherite_water_canteen_from_charcoal_filtering")), new ShapelessRecipe(RecipeBuilder.createCraftingCommonInfo(true), 
				RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, "charcoal_filtering"), ItemStackTemplate.fromNonEmptyStack(CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_NETHERITE_CANTEEN), Survive.THIRST_CONFIG.nether_canteen_fill_amount, SPotions.PURIFIED_WATER.holder())), 
				List.of(
						Ingredient.of(SItems.USED_CHARCOAL_FILTER, SItems.CHARCOAL_FILTER), 
						LoaderHelper.createStackIngredient(CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_NETHERITE_CANTEEN), Survive.THIRST_CONFIG.nether_canteen_fill_amount, Potions.WATER))))));
		collection.add(new RecipeHolder<CraftingRecipe>(ResourceKey.create(Registries.RECIPE, Survive.getInstance().location("purified_water_bottle_from_charcoal_filtering")), new ShapelessRecipe(RecipeBuilder.createCraftingCommonInfo(true), 
				RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, "charcoal_filtering"), ItemStackTemplate.fromNonEmptyStack(PotionContents.createItemStack(Items.POTION, SPotions.PURIFIED_WATER.holder())),
				List.of(
						Ingredient.of(SItems.USED_CHARCOAL_FILTER, SItems.CHARCOAL_FILTER), 
						LoaderHelper.createStackIngredient(PotionContents.createItemStack(Items.POTION, Potions.WATER))))));
		for (int i = 1; i < Math.min(Survive.THIRST_CONFIG.canteen_fill_amount, 8); i++)
		collection.addAll(CanteenFillingRecipeMaker.createRecipes(stackHelper, SItems.CANTEEN, SItems.FILLED_CANTEEN, i));
		for (int i = 1; i < Math.min(Survive.THIRST_CONFIG.nether_canteen_fill_amount, 8); i++)
		collection.addAll(CanteenFillingRecipeMaker.createRecipes(stackHelper, SItems.NETHERITE_CANTEEN, SItems.FILLED_NETHERITE_CANTEEN, i));
		registration.addRecipes(RecipeTypes.CRAFTING, collection);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(SItems.FILLED_CANTEEN, PotionSubtypeInterpreter.INSTANCE);
		registration.registerSubtypeInterpreter(SItems.FILLED_NETHERITE_CANTEEN, PotionSubtypeInterpreter.INSTANCE);
	}

}
