package com.stereowalker.survive.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum TempRegulationPlateType implements StringRepresentable {
	CHILLER("chiller", 0x9da8af), 
	HEATER("heater", 0xb3a8a2);

	private final String name;
	private final int color;

	private TempRegulationPlateType(String name, int color) {
		this.name = name;
		this.color = color;
	}
	
	public int getColor() {
		return color;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

}
