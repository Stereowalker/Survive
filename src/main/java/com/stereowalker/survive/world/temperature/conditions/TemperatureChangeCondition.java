package com.stereowalker.survive.world.temperature.conditions;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;

public abstract class TemperatureChangeCondition<T extends TemperatureChangeInstance> {
	
	public TemperatureChangeCondition() {
	}
	
	public abstract T createInstance(JsonObject object);
	public abstract T createInstance(CompoundTag nbt);
}
