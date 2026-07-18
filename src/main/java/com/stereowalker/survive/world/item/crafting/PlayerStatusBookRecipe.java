package com.stereowalker.survive.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PlayerStatusBookRecipe extends CustomRecipe {
    public static final PlayerStatusBookRecipe INSTANCE = new PlayerStatusBookRecipe();
    public static final MapCodec<PlayerStatusBookRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerStatusBookRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	public PlayerStatusBookRecipe() {
		super();
	}

	@Override
	public boolean matches(CraftingInput inv, Level worldIn) {
		if (!ServerConfig.canCraftStatusBook) return false;
		int thermometer = 0;
		int book = 0;
		for (int i = 0; i < inv.size(); i++) {
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
	public ItemStack assemble(CraftingInput inv) {
		if (!ServerConfig.canCraftStatusBook) return ItemStack.EMPTY;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.getItem() == Items.WRITTEN_BOOK) {
				return Survive.convertToPlayerStatusBook(stack);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return SRecipeSerializer.CRAFTING_PLAYER_STATUS_BOOK;
	}

}
