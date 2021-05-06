package com.stereowalker.survive.temperature;

import com.google.gson.JsonObject;

import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class TemperatureChangeCondition<T extends TemperatureChangeInstance> extends ForgeRegistryEntry<TemperatureChangeCondition<?>> {
	
	public TemperatureChangeCondition() {
	}
	
	public abstract T createInstance(JsonObject object);
}
