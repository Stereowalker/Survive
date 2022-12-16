package com.stereowalker.survive.world.item;

import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateSize;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateType;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TemperatureRegulatorPlateItem extends Item {
	TempRegulationPlateType type;
	TempRegulationPlateSize size;

	public TemperatureRegulatorPlateItem(TempRegulationPlateType type, TempRegulationPlateSize size, Properties pProperties) {
		super(pProperties);
		this.type = type;
		this.size = size;
	}
	
	public TempRegulationPlateType getType() {
		return type;
	}
	
	public TempRegulationPlateSize getSize() {
		return size;
	}
	
	public static int getColor(ItemStack stack) {
		if (stack.getItem() instanceof TemperatureRegulatorPlateItem) {
			return ((TemperatureRegulatorPlateItem)stack.getItem()).getType().getColor();
		} else return 0xffffff;
	}

}
