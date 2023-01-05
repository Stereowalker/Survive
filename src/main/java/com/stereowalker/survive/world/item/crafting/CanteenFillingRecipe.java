package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.SItems;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CanteenFillingRecipe extends CustomRecipe {

	public CanteenFillingRecipe(ResourceLocation idIn, CraftingBookCategory pCategory) {
		super(idIn, pCategory);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		Potion savedPotion = null;
		int bottles = 0;
		int canteens = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() == SItems.CANTEEN) {
				canteens++;
			}
			else if (stack.getItem() == Items.POTION) {
				if (savedPotion == null) {
					savedPotion = PotionUtils.getPotion(stack);
					bottles++;
				} else if (savedPotion.equals(PotionUtils.getPotion(stack))) {
					bottles++;
				} else {
					return false;
				}
			} else if (!stack.isEmpty()) {
				return false;
			}
			if (bottles > Survive.THIRST_CONFIG.canteen_fill_amount) {
				return false;
			}
		}
		return savedPotion != null && bottles <= Survive.THIRST_CONFIG.canteen_fill_amount && canteens == 1;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv) {
		int count = 0;
		Potion savedPotion = null;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() == Items.POTION) {
				count++;
				savedPotion = PotionUtils.getPotion(stack);
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
