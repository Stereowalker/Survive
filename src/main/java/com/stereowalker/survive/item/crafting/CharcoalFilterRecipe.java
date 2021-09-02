package com.stereowalker.survive.item.crafting;

import com.stereowalker.survive.item.CanteenItem;
import com.stereowalker.survive.item.SItems;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CharcoalFilterRecipe extends SpecialRecipe {

	public CharcoalFilterRecipe(ResourceLocation idIn) {
		super(idIn);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		int charcoalFilter = 0;
		int waterBottle = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == SItems.CHARCOAL_FILTER) {
				charcoalFilter++;
			} else if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) == Potions.WATER && PotionUtils.getPotionFromItem(stack) != Potions.EMPTY) {
				waterBottle++;
			} else if (stack.getItem() == SItems.WATER_BOWL) {
				waterBottle++;
			} else if (stack.getItem() == SItems.WATER_CANTEEN) {
				waterBottle++;
			} else if (stack.getItem() == Items.WATER_BUCKET) {
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
	public ItemStack getCraftingResult(CraftingInventory inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) == Potions.WATER && PotionUtils.getPotionFromItem(stack) != Potions.EMPTY) {
				return new ItemStack(SItems.PURIFIED_WATER_BOTTLE);
			} else if (stack.getItem() == SItems.WATER_BOWL) {
				return new ItemStack(SItems.PURIFIED_WATER_BOWL);
			} else if (stack.getItem() == SItems.WATER_CANTEEN) {
				return CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.PURIFIED_WATER_CANTEEN), ((CanteenItem)stack.getItem()).getDrinksLeft(stack));
			} else if (stack.getItem() == Items.WATER_BUCKET) {
				return new ItemStack(SItems.PURIFIED_WATER_BUCKET);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for(int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack item = inv.getStackInSlot(i);
			if (item.getItem() == Items.WATER_BUCKET) {
				nonnulllist.set(i, ItemStack.EMPTY);
			}
		}

		return nonnulllist;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SRecipeSerializer.CRAFTING_SPECIAL_CHARCOAL_FILTERING;
	}

}
