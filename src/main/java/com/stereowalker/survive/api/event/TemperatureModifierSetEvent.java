package com.stereowalker.survive.api.event;

import com.stereowalker.survive.world.temperature.TemperatureModifier;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;

public class TemperatureModifierSetEvent implements InheritableEvent {
	public static final EventBus<TemperatureModifierSetEvent> BUS = EventBus.create(TemperatureModifierSetEvent.class);
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
