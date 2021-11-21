package com.stereowalker.survive.world.seasons;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class Season extends ForgeRegistryEntry<Season> {
	private final float modifier;
	
	public Season(float modifier) {
		this.modifier = modifier;
	}

	public float getModifier() {
		return modifier;
	}
	
}