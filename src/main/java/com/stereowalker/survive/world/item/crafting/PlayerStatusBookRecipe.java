package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.world.item.SItems;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PlayerStatusBookRecipe extends CustomRecipe {

	public PlayerStatusBookRecipe(ResourceLocation idIn, CraftingBookCategory pCategory) {
		super(idIn, pCategory);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		if (!ServerConfig.canCraftStatusBook) return false;
		int thermometer = 0;
		int book = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() == SItems.THERMOMETER) {
				thermometer++;
			} else if (stack.getItem() == Items.WRITTEN_BOOK && stack.getTag() != null && !stack.getTag().contains("status_owner")) {
				book++;
			}
			if (thermometer > 1 || book > 1) {
				return false;
			}
		}
		return thermometer == 1 && book == 1;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess ra) {
		if (!ServerConfig.canCraftStatusBook) return ItemStack.EMPTY;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() == Items.WRITTEN_BOOK) {
				return Survive.convertToPlayerStatusBook(stack);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

		for(int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack itemstack = inv.getItem(i);
			if (itemstack.getItem() == SItems.CHARCOAL_FILTER) {
				ItemStack filterClone = itemstack.copy();
				if (filterClone.hurt(1, RandomSource.create(), null)) {
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
		return SRecipeSerializer.CRAFTING_PLAYER_STATUS_BOOK;
	}

}
