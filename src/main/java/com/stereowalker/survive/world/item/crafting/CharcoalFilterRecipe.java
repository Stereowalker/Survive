package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.alchemy.SPotions;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CharcoalFilterRecipe extends CustomRecipe {

	public CharcoalFilterRecipe(ResourceLocation idIn, CraftingBookCategory pCategory) {
		super(idIn, pCategory);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		int charcoalFilter = 0;
		int waterBottle = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() == SItems.CHARCOAL_FILTER || stack.getItem() == SItems.USED_CHARCOAL_FILTER) {
				charcoalFilter++;
			} else if (PotionUtils.getPotion(stack) == Potions.WATER && PotionUtils.getPotion(stack) != Potions.EMPTY) {
				waterBottle++;
			} else if (stack.getItem() == Items.WATER_BUCKET) {
				waterBottle++;
			} else if (stack.getItem() == SItems.WATER_BOWL) {
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
	public ItemStack assemble(CraftingContainer inv, RegistryAccess ra) {
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
//			PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
			if (stack.getItem() == SItems.WATER_BOWL) {
				return new ItemStack(SItems.PURIFIED_WATER_BOWL);
			} else if (stack.getItem() == Items.WATER_BUCKET) {
				return new ItemStack(SItems.PURIFIED_WATER_BUCKET);
			} else if (PotionUtils.getPotion(stack) == Potions.WATER && PotionUtils.getPotion(stack) != Potions.EMPTY) {
				return PotionUtils.setPotion(stack.copy(), SPotions.PURIFIED_WATER.holder().value());
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
		int emptySpace = -1;
		boolean needsSpace = false;

		ItemStack filterClone = null;
		for(int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack itemstack = inv.getItem(i);
			if (itemstack.isEmpty()) {
				if (emptySpace == -1) emptySpace = i;
			}
			else if (itemstack.getItem() == SItems.USED_CHARCOAL_FILTER) {
				filterClone = itemstack.copy();
				filterClone.hurt(1, RandomSource.create(), null);
				if (filterClone.isEmpty()) {
					nonnulllist.set(i, ItemStack.EMPTY);
				} else {
					nonnulllist.set(i, filterClone);
				}
			} else if (itemstack.getItem() == SItems.CHARCOAL_FILTER) {
//				filterClone = itemstack.transmuteCopy(SItems.USED_CHARCOAL_FILTER, 1);
				filterClone = new ItemStack(SItems.USED_CHARCOAL_FILTER, 1);
				filterClone.setTag(itemstack.getTag());
				filterClone.setDamageValue(1);
				if (itemstack.getCount() == 1)
					nonnulllist.set(i, filterClone);
				else {
					needsSpace = true;
				}
			}
		}

		
		
		if (needsSpace) {
			NonNullList<ItemStack> nonnulllist2 = NonNullList.withSize(inv.getContainerSize()+1, ItemStack.EMPTY);
			nonnulllist2.set(0, filterClone);
			for(int i = 0; i < nonnulllist.size(); ++i) {
				nonnulllist2.set(i+1, nonnulllist.get(i));
			}
			return nonnulllist2;
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
