package com.stereowalker.survive.world.temperature;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class TemperatureChangeCondition<T extends TemperatureChangeInstance> extends ForgeRegistryEntry<TemperatureChangeCondition<?>> {
	
	public TemperatureChangeCondition() {
	}
	
	public abstract T createInstance(JsonObject object);
	public abstract T createInstance(CompoundTag nbt);
}
