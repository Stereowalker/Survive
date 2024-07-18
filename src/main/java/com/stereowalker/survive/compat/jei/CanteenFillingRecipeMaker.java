package com.stereowalker.survive.compat.jei;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.unionlib.util.VersionHelper;

import mezz.jei.api.helpers.IStackHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class CanteenFillingRecipeMaker {
	public static List<RecipeHolder<CraftingRecipe>> createRecipes(IStackHelper stackHelper, Item empty, Item filled, int amount) {
		String group = "survive.fill.canteen";

		return RegistryHelper.potions().holders()
			.<RecipeHolder<CraftingRecipe>>map(potion -> {				
				ItemStack canteenStack = new ItemStack(empty);
				Ingredient canteenIngredient = Ingredient.of(canteenStack);
				
				ItemStack input = PotionContents.createItemStack(Items.POTION, potion);
				ItemStack output = CanteenItem.addToCanteen(new ItemStack(filled), amount, potion);
				Ingredient potionIngredient = Ingredient.of(input);
				List<Ingredient> ingredients = Lists.newArrayList();
				ingredients.add(canteenIngredient);
				for (int i = 0; i <= amount; i++) ingredients.add(potionIngredient);
				NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(new Ingredient[0]));
				ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "survive.fill"+amount+".canteen." + output.getDescriptionId());
				return new RecipeHolder<CraftingRecipe>(id, new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs));
			})
			.toList();
	}

	private CanteenFillingRecipeMaker() {

	}
}
