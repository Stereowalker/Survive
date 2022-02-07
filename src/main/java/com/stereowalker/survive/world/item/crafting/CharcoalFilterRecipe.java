package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.SItems;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CharcoalFilterRecipe extends CustomRecipe {

	public CharcoalFilterRecipe(ResourceLocation idIn) {
		super(idIn);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		int charcoalFilter = 0;
		int waterBottle = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() == SItems.CHARCOAL_FILTER) {
				charcoalFilter++;
			} else if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER && PotionUtils.getPotion(stack) != Potions.EMPTY) {
				waterBottle++;
			} else if (stack.getItem() == SItems.WATER_BOWL) {
				waterBottle++;
			} else if (stack.getItem() == SItems.WATER_CANTEEN) {
				waterBottle++;
			} else if (!stack.isEmpty()){
				return false;
			}
			if (charcoalFilter > 1 || waterBottle > 1) {
				return false;
			}
		}
		return charcoalFilter == 1 && waterBottle == 1;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv) {
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() == Items.POTION && PotionUtils.getPotion(stack) == Potions.WATER && PotionUtils.getPotion(stack) != Potions.EMPTY) {
				return new ItemStack(SItems.PURIFIED_WATER_BOTTLE);
			} else if (stack.getItem() == SItems.WATER_BOWL) {
				return new ItemStack(SItems.PURIFIED_WATER_BOWL);
			} else if (stack.getItem() == SItems.WATER_CANTEEN) {
				return CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.PURIFIED_WATER_CANTEEN), ((CanteenItem)stack.getItem()).getDrinksLeft(stack));
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

		for(int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack item = inv.getItem(i);
			if (item.getItem() == Items.WATER_BUCKET) {
				nonnulllist.set(i, ItemStack.EMPTY);
			}
		}

		return nonnulllist;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SRecipeSerializer.CRAFTING_SPECIAL_CHARCOAL_FILTERING;
	}

}
