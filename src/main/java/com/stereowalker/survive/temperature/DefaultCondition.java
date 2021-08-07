package com.stereowalker.survive.temperature;

import com.google.gson.JsonObject;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class DefaultCondition extends TemperatureChangeCondition<DefaultCondition.Instance>{

	public DefaultCondition() {
		super();
	}

	@Override
	public Instance createInstance(JsonObject object) {
		float temperatureIn = 0;
		if(object.has("temperature") && object.get("temperature").isJsonPrimitive()) {
			temperatureIn = object.get("temperature").getAsFloat();
		}
		return new Instance(temperatureIn);
	}
	
	@Override
	public Instance createInstance(CompoundNBT nbt) {
		float temperatureIn = nbt.getFloat("temperature");
		return new Instance(temperatureIn);
	}
	
	static class Instance extends TemperatureChangeInstance {

		public Instance(float temperatureIn) {
			super(temperatureIn);
		}

		@Override
		public boolean shouldChangeTemperature(PlayerEntity player) {
			return true;
		}

		@Override
		public CompoundNBT serialize() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putFloat("temperature", this.getTemperature());
			return nbt;
		}
		
	}
}
