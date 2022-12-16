package com.stereowalker.survive.api.event;

import com.stereowalker.survive.world.temperature.TemperatureModifier;

import net.minecraftforge.eventbus.api.Event;

public class TemperatureModifierSetEvent extends Event {
	TemperatureModifier modifier;
	
	public TemperatureModifierSetEvent(TemperatureModifier modifier) {
		this.modifier = modifier;
	}
	
	public TemperatureModifier getModifier() {
		return modifier;
	}
	
	public void setModifier(TemperatureModifier modifier) {
		this.modifier = modifier;
	}
}
