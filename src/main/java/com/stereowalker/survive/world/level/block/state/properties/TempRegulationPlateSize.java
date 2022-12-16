package com.stereowalker.survive.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum TempRegulationPlateSize implements StringRepresentable {
	SMALL("small", 3, 8.0F), 
	MEDIUM("medium", 4, 9.0F), 
	LARGE("large", 5, 10.0F);

	private final String name;
	private final int range;
	private final float modifier;

	private TempRegulationPlateSize(String name, int range, float modifier) {
		this.name = name;
		this.range = range;
		this.modifier = modifier;
	}

	public String getName() {
		return this.name;
	}
	
	public int getRange() {
		return range;
	}
	
	public float getModifier() {
		return modifier;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

}
