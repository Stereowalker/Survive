package com.stereowalker.survive.compat.jei;

import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.SItems;

import mezz.jei.api.helpers.IStackHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class CanteenFillingRecipeMaker {
	public static List<CraftingRecipe> createRecipes(IStackHelper stackHelper) {
		String group = "survive.fill.canteen";

		return Registry.POTION.stream()
			.<CraftingRecipe>map(potion -> {				
				ItemStack canteenStack = new ItemStack(SItems.CANTEEN);
				Ingredient canteenIngredient = Ingredient.of(canteenStack);
				
				ItemStack input = PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
				ItemStack output = CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), 3, potion);
				Ingredient potionIngredient = Ingredient.of(input);
				NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
						canteenIngredient, potionIngredient, potionIngredient, potionIngredient
				);
				ResourceLocation id = new ResourceLocation(Survive.MOD_ID, "survive.fill.canteen." + output.getDescriptionId());
				return new ShapelessRecipe(id, group, output, inputs);
			})
			.toList();
	}

	private CanteenFillingRecipeMaker() {

	}
}
