package com.stereowalker.survive.world.item.crafting;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PlayerStatusBookRecipe extends CustomRecipe {

	public PlayerStatusBookRecipe(CraftingBookCategory pCategory) {
		super(pCategory);
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
			} else if (stack.getItem() == Items.WRITTEN_BOOK && !SDataComponents.STATUS_OWNER_D.hasData(stack)) {
				book++;
			}
			if (thermometer > 1 || book > 1) {
				return false;
			}
		}
		return thermometer == 1 && book == 1;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, Provider pRegistries) {
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
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SRecipeSerializer.CRAFTING_PLAYER_STATUS_BOOK;
	}

}
