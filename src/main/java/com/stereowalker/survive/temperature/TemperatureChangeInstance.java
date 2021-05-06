package com.stereowalker.survive.temperature;

import net.minecraft.entity.player.PlayerEntity;

public abstract class TemperatureChangeInstance {
	
	private float temperature;

	public TemperatureChangeInstance(float temperatureIn) {
		this.temperature = temperatureIn;
	}
	
	public abstract boolean shouldChangeTemperature(PlayerEntity player);
	
	public float getTemperature() {
		return temperature;
	}
}
