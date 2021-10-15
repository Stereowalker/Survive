package com.stereowalker.survive.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public class TemperatureModifierSetEvent extends Event {
	private ResourceLocation id;
	double modifier;
	
	public TemperatureModifierSetEvent(ResourceLocation id, double modifier) {
		this.id = id;
		this.modifier = modifier;
	}
	
	public ResourceLocation getId() {
		return id;
	}
	
	public double getModifier() {
		return modifier;
	}
	
	public void setModifier(double modifier) {
		this.modifier = modifier;
	}
}
