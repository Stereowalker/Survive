package com.stereowalker.survive.compat;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Lists;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.crafting.CharcoalFilterRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

@JeiPlugin
public class JEICompat implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return Survive.getInstance().location("recipe_handler");
	}

//	@Override
//	//TODO: Figure our how to make this work better later
//	public void registerRecipes(IRecipeRegistration registration) {
//		Collection<Recipe<?>> collection = new ArrayList<Recipe<?>>();
//		collection.add(new CharcoalFilterRecipe(Survive.getInstance().location("purified_water_canteen_from_charcoal_filtering")) {
//			@Override
//			public NonNullList<Ingredient> getIngredients() {
//				NonNullList<Ingredient> ingredients = NonNullList.create();
//				ingredients.add(0, Ingredient.of(SItems.CHARCOAL_FILTER));
//				ingredients.add(1, Ingredient.of(CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.PURIFIED_WATER_CANTEEN), 1)));
//				return ingredients;
//			}
//			@Override
//			public ItemStack getResultItem() {
//				return CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.PURIFIED_WATER_CANTEEN), 1);
//			}
//		});
//		collection.add(new CharcoalFilterRecipe(Survive.getInstance().location("purified_water_bucket_from_charcoal_filtering_test")) {
//			@Override
//			public NonNullList<Ingredient> getIngredients() {
//				NonNullList<Ingredient> ingredients = NonNullList.create();
//				ingredients.add(0, Ingredient.of(SItems.CHARCOAL_FILTER));
//				ingredients.add(1, Ingredient.of(Items.WATER_BUCKET));
//				return ingredients;
//			}
//			@Override
//			public ItemStack getResultItem() {
//				return new ItemStack(SItems.PURIFIED_WATER_BUCKET);
//			}
//		});
//		registration.addRecipes(collection, VanillaRecipeCategoryUid.CRAFTING);
//	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addCategoryExtension(CharcoalFilterRecipe.class, (a)-> {
			if (a.getId().equals(Survive.getInstance().location("purified_water_bottle_from_charcoal_filtering"))) {
				return new ICraftingCategoryExtension() {
					@Override
					public void setIngredients(IIngredients ingredients) {
						ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(SItems.PURIFIED_WATER_BOTTLE));
						ingredients.setInputIngredients(Lists.newArrayList(Ingredient.of(SItems.CHARCOAL_FILTER), Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER))));
					}};
			}
			else if (a.getId().equals(Survive.getInstance().location("purified_water_bowl_from_charcoal_filtering"))) {
				return new ICraftingCategoryExtension() {
					@Override
					public void setIngredients(IIngredients ingredients) {
						ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(SItems.PURIFIED_WATER_BOWL));
						ingredients.setInputIngredients(Lists.newArrayList(Ingredient.of(SItems.CHARCOAL_FILTER), Ingredient.of(SItems.WATER_BOWL)));
					}};
			}
			else if (a.getId().equals(Survive.getInstance().location("purified_water_bucket_from_charcoal_filtering"))) {
				return new ICraftingCategoryExtension() {
					@Override
					public void setIngredients(IIngredients ingredients) {
						ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(SItems.PURIFIED_WATER_BUCKET));
						ingredients.setInputIngredients(Lists.newArrayList(Ingredient.of(SItems.CHARCOAL_FILTER), Ingredient.of(Items.WATER_BUCKET)));
					}};
			}
			else {
				return new ICraftingCategoryExtension() {
				@Override
				public void setIngredients(IIngredients ingredients) {
					ingredients.setOutput(VanillaTypes.ITEM, CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.PURIFIED_WATER_CANTEEN), 1));
					ingredients.setInputIngredients(Lists.newArrayList(Ingredient.of(SItems.CHARCOAL_FILTER), Ingredient.of(CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.PURIFIED_WATER_CANTEEN), 1))));
				}};
			}
		});
	}

}
