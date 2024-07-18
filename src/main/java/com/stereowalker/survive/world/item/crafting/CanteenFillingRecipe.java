package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.SItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CanteenFillingRecipe extends CustomRecipe {

	public CanteenFillingRecipe(CraftingBookCategory pCategory) {
		super(pCategory);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		PotionContents savedPotion = null;
		int bottles = 0;
		int canteens = 0;
		boolean nether = false;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() == SItems.CANTEEN) {
				canteens++;
			}
			if (stack.getItem() == SItems.NETHERITE_CANTEEN) {
				canteens++;
				nether = true;
			}
			else if (stack.getItem() == Items.POTION) {
				if (savedPotion == null) {
					savedPotion = stack.get(DataComponents.POTION_CONTENTS);
					bottles++;
				} else if (savedPotion.potion().get().value().equals(stack.get(DataComponents.POTION_CONTENTS).potion().get().value())) {
					bottles++;
				} else {
					return false;
				}
			} else if (!stack.isEmpty()) {
				return false;
			}
			if (bottles > Survive.THIRST_CONFIG.canteenFillAmount(nether)) {
				return false;
			}
		}
		return savedPotion != null && bottles <= Survive.THIRST_CONFIG.canteenFillAmount(nether) && canteens == 1;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, HolderLookup.Provider ra) {
		int count = 0;
		PotionContents savedPotion = null;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() == Items.POTION) {
				count++;
				savedPotion = stack.get(DataComponents.POTION_CONTENTS);
			}
		}
		if (savedPotion != null && count > 0) {
			return CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), count, savedPotion);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer pContainer) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pContainer.getContainerSize(), ItemStack.EMPTY);
	      for(int i = 0; i < nonnulllist.size(); ++i) {
	         ItemStack item = pContainer.getItem(i);
	         if (item.hasCraftingRemainingItem()) {
	            nonnulllist.set(i, item.getCraftingRemainingItem());
	         }
	         if (item.getItem() == Items.POTION) {
	        	 nonnulllist.set(i, new ItemStack(Items.GLASS_BOTTLE));
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
		return SRecipeSerializer.CRAFTING_SPECIAL_CANTEEN_FILLING;
	}

}
