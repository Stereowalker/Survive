package com.stereowalker.survive.world.level.block;

import com.stereowalker.survive.world.item.TemperatureRegulatorPlateItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public class TemperatureRegulatorBlock extends AbstractTemperatureRegulatorBlock {
	
	public TemperatureRegulatorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canAddPlate(BlockState pState, ItemStack plate) {
		if (plate.getItem() == Items.REPEATER) {
			return true;
		} else if (plate.getItem() == Items.COMPARATOR) {
			return true;
		}
		else return plate.getItem() instanceof TemperatureRegulatorPlateItem;
	}
	
	@Override
	public boolean canRemovePlates(BlockState pState) {
		return false;
	}

	@Override
	public ItemStack getPlateStack(BlockState pState) {
		return ItemStack.EMPTY;
	}
}
