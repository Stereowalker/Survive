package com.stereowalker.survive.compat.jei;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.unionlib.util.LoaderHelper;
import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.unionlib.util.VersionHelper;

import mezz.jei.api.helpers.IStackHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class CanteenFillingRecipeMaker {
	public static List<RecipeHolder<CraftingRecipe>> createRecipes(IStackHelper stackHelper, Item empty, Item filled, int amount) {
		String group = "survive.fill.canteen";

		return RegistryHelper.potions().stream()
			.<RecipeHolder<CraftingRecipe>>map(potion -> {				
				ItemStack canteenStack = new ItemStack(empty);
				Ingredient canteenIngredient = LoaderHelper.createStackIngredient(canteenStack);
				
				ItemStack input = PotionContents.createItemStack(Items.POTION, RegistryHelper.potions().wrapAsHolder(potion));
				ItemStack output = CanteenItem.addToCanteen(new ItemStack(filled), amount, RegistryHelper.potions().wrapAsHolder(potion));
				Ingredient potionIngredient = LoaderHelper.createStackIngredient(input);
				List<Ingredient> ingredients = Lists.newArrayList();
				ingredients.add(canteenIngredient);
				for (int i = 0; i <= amount; i++) ingredients.add(potionIngredient);
				List<Ingredient> inputs = List.of(ingredients.toArray(new Ingredient[0]));
					Identifier id = VersionHelper.toLoc(Survive.MOD_ID, "survive.fill" + amount + ".canteen."  + output.getItem().getDescriptionId() );
				return new RecipeHolder<CraftingRecipe>(ResourceKey.create(Registries.RECIPE, id), new ShapelessRecipe(RecipeBuilder.createCraftingCommonInfo(true), RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, group), ItemStackTemplate.fromNonEmptyStack(output), inputs));
			})
			.toList();
	}

	private CanteenFillingRecipeMaker() {

	}
}
