package com.stereowalker.survive.world.level.block.state.properties;

import com.stereowalker.unionlib.util.math.Color;
import com.stereowalker.unionlib.util.math.ImmutableColor;

import net.minecraft.util.StringRepresentable;

public enum TempRegulationPlateType implements StringRepresentable {
	CHILLER("chiller", new ImmutableColor(.616f, .659f, .686f)), 
	HEATER("heater", new ImmutableColor(.702f, .659f, .635f));

	private final String name;
	private final Color color;

	private TempRegulationPlateType(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public Color getColor() {
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
