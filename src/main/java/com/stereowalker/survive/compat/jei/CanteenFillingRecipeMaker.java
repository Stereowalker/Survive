package com.stereowalker.survive.compat.jei;

import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.unionlib.util.RegistryHelper;

import mezz.jei.api.helpers.IStackHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class CanteenFillingRecipeMaker {
	public static List<RecipeHolder<CraftingRecipe>> createRecipes(IStackHelper stackHelper) {
		String group = "survive.fill.canteen";

		return RegistryHelper.potions().holders()
			.<RecipeHolder<CraftingRecipe>>map(potion -> {				
				ItemStack canteenStack = new ItemStack(SItems.CANTEEN);
				Ingredient canteenIngredient = Ingredient.of(canteenStack);
				
				ItemStack input = PotionContents.createItemStack(Items.POTION, potion);
				ItemStack output = CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), 3, potion);
				Ingredient potionIngredient = Ingredient.of(input);
				NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
						canteenIngredient, potionIngredient, potionIngredient, potionIngredient
				);
				ResourceLocation id = new ResourceLocation(Survive.MOD_ID, "survive.fill.canteen." + output.getDescriptionId());
				return new RecipeHolder<CraftingRecipe>(id, new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs));
			})
			.toList();
	}

	private CanteenFillingRecipeMaker() {

	}
}
