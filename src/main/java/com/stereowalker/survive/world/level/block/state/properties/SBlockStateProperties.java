package com.stereowalker.survive.world.level.block.state.properties;

import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SBlockStateProperties {
	public static final EnumProperty<TempRegulationPlateType> TEMP_REG_TYPE = EnumProperty.create("temperature_regulation_type", TempRegulationPlateType.class);
	public static final EnumProperty<TempRegulationPlateSize> TEMP_REG_SIZE = EnumProperty.create("temperature_regulation_size", TempRegulationPlateSize.class);
	public static final IntegerProperty PLATE_COUNT = IntegerProperty.create("plate_count", 0, 4);
}
