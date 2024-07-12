package com.stereowalker.survive.api.event;

import com.stereowalker.survive.world.temperature.TemperatureModifier;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class TemperatureModifierSetEvent extends Event implements ICancellableEvent {
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

    /**
     * @see #setSuccessful(boolean)
     */
    @Override
    public void setCanceled(boolean canceled) {
        ICancellableEvent.super.setCanceled(canceled);
    }
}
