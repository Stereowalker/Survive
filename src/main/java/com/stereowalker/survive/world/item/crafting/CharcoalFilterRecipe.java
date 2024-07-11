package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.alchemy.SPotions;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CharcoalFilterRecipe extends CustomRecipe {

	public CharcoalFilterRecipe(CraftingBookCategory pCategory) {
		super(pCategory);
	}

	@Override
	public boolean matches(CraftingInput inv, Level worldIn) {
		int charcoalFilter = 0;
		int waterBottle = 0;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
			if (stack.getItem() == SItems.CHARCOAL_FILTER) {
				charcoalFilter++;
			} else if (stack.getItem() == Items.POTION && contents != null && contents.is(Potions.WATER)) {
				waterBottle++;
			} else if (stack.getItem() == Items.WATER_BUCKET) {
				waterBottle++;
			} else if (stack.getItem() == SItems.FILLED_CANTEEN && contents != null && contents.is(Potions.WATER)) {
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
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider ra) {
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
			if (stack.getItem() == Items.POTION && contents != null && contents.is(Potions.WATER)) {
				ItemStack copy = stack.copy();
				copy.set(DataComponents.POTION_CONTENTS, new PotionContents(SPotions.PURIFIED_WATER.holder()));
				return copy;
			} else if (stack.getItem() == SItems.WATER_BOWL) {
				return new ItemStack(SItems.PURIFIED_WATER_BOWL);
			} else if (stack.getItem() == SItems.FILLED_CANTEEN && contents != null && contents.is(Potions.WATER)) {
				ItemStack copy = stack.copy();
				copy.set(DataComponents.POTION_CONTENTS, new PotionContents(SPotions.PURIFIED_WATER.holder()));
				return copy;
			} else if (stack.getItem() == Items.WATER_BUCKET) {
				return new ItemStack(SItems.PURIFIED_WATER_BUCKET);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

		for(int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack itemstack = inv.getItem(i);
			if (itemstack.getItem() == SItems.CHARCOAL_FILTER) {
				ItemStack filterClone = itemstack.copy();
				filterClone.shrink(1);
				if (filterClone.isEmpty()) {
					nonnulllist.set(i, ItemStack.EMPTY);
				} else {
					nonnulllist.set(i, filterClone);
				}
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
