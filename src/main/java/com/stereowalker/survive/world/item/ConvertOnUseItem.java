package com.stereowalker.survive.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ConvertOnUseItem extends Item {
	Item convertsTo;

	public ConvertOnUseItem(Item convertsTo, Properties pProperties) {
		super(pProperties);
		this.convertsTo = convertsTo;
	}

	@Override
	public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		ItemStack stack = pPlayer.getItemInHand(pUsedHand).copy();
		stack.shrink(1);
		ItemStack convertStack = new ItemStack(convertsTo);
		pPlayer.setItemInHand(pUsedHand, convertStack);
		pPlayer.addItem(stack);
		return InteractionResult.CONSUME;
	}

}
