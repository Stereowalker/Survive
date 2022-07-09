package com.stereowalker.survive.compat;

import java.util.List;

import com.google.common.collect.Lists;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.survive.world.item.crafting.CharcoalFilterRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

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
//		collection.add(new CharcoalFilterRecipe(Survive.getInstance().location("purified_water_bottle_from_charcoal_filtering_test")) {
//			@Override
//			public NonNullList<Ingredient> getIngredients() {
//				NonNullList<Ingredient> ingredients = NonNullList.create();
//				ingredients.add(0, Ingredient.of(SItems.CHARCOAL_FILTER));
//				ingredients.add(1, Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
//				return ingredients;
//			}
//			@Override
//			public ItemStack getResultItem() {
//				return new ItemStack(SItems.PURIFIED_WATER_BOTTLE);
//			}
//		});
//		collection.add(new CharcoalFilterRecipe(Survive.getInstance().location("purified_water_bucket_from_charcoal_filtering")) {
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
//		collection.add(new CharcoalFilterRecipe(Survive.getInstance().location("purified_water_bowl_from_charcoal_filtering_test")) {
//			@Override
//			public NonNullList<Ingredient> getIngredients() {
//				NonNullList<Ingredient> ingredients = NonNullList.create();
//				ingredients.add(0, Ingredient.of(SItems.CHARCOAL_FILTER));
//				ingredients.add(1, Ingredient.of(SItems.WATER_BOWL));
//				return ingredients;
//			}
//			@Override
//			public ItemStack getResultItem() {
//				return new ItemStack(SItems.PURIFIED_WATER_BOWL);
//			}
//		});
//		registration.addRecipes(collection, VanillaRecipeCategoryUid.CRAFTING);
//	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addCategoryExtension(CharcoalFilterRecipe.class, (a)-> {
			List<ItemStack> filter = Lists.newArrayList(new ItemStack(SItems.CHARCOAL_FILTER));
			if (a.getId().equals(Survive.getInstance().location("purified_water_bottle_from_charcoal_filtering"))) {
				return new ICraftingCategoryExtension() {
					@Override
					public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
						List<ItemStack> fluid = Lists.newArrayList(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
						craftingGridHelper.setInputs(builder, VanillaTypes.ITEM, Lists.newArrayList(filter, fluid), 0, 0);
						craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM, Lists.newArrayList(PotionUtils.setPotion(new ItemStack(Items.POTION), SPotions.PURIFIED_WATER)));
					}};
			}
			else if (a.getId().equals(Survive.getInstance().location("purified_water_bucket_from_charcoal_filtering"))) {
				return new ICraftingCategoryExtension() {
					@Override
					public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
						List<ItemStack> fluid = Lists.newArrayList(new ItemStack(Items.WATER_BUCKET));
						craftingGridHelper.setInputs(builder, VanillaTypes.ITEM, Lists.newArrayList(filter, fluid), 0, 0);
						craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM, Lists.newArrayList(new ItemStack(SItems.PURIFIED_WATER_BUCKET)));
					}};
			}
			else if (a.getId().equals(Survive.getInstance().location("purified_water_bowl_from_charcoal_filtering"))) {
				return new ICraftingCategoryExtension() {
					@Override
					public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
						List<ItemStack> fluid = Lists.newArrayList(new ItemStack(SItems.WATER_BOWL));
						craftingGridHelper.setInputs(builder, VanillaTypes.ITEM, Lists.newArrayList(filter, fluid), 0, 0);
						craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM, Lists.newArrayList(new ItemStack(SItems.PURIFIED_WATER_BOWL)));
					}};
			}
			else {
				return new ICraftingCategoryExtension() {
					@Override
					public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
						List<ItemStack> fluid = Lists.newArrayList(CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.WATER_CANTEEN), 1));
						craftingGridHelper.setInputs(builder, VanillaTypes.ITEM, Lists.newArrayList(filter, fluid), 0, 0);
						craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM, Lists.newArrayList(CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.PURIFIED_WATER_CANTEEN), 1)));
					}};
			}
		});
	}

}
