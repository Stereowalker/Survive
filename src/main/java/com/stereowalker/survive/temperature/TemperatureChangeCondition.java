package com.stereowalker.survive.temperature;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class TemperatureChangeCondition<T extends TemperatureChangeInstance> extends ForgeRegistryEntry<TemperatureChangeCondition<?>> {
	
	public TemperatureChangeCondition() {
	}
	
	public abstract T createInstance(JsonObject object);
	public abstract T createInstance(CompoundNBT nbt);
}
