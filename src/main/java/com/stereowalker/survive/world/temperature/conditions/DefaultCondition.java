package com.stereowalker.survive.world.temperature.conditions;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

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
	public Instance createInstance(CompoundTag nbt) {
		float temperatureIn = nbt.getFloat("temperature");
		return new Instance(temperatureIn);
	}
	
	static class Instance extends TemperatureChangeInstance {

		public Instance(float temperatureIn) {
			super(temperatureIn);
		}

		@Override
		public boolean shouldChangeTemperature(Player player) {
			return true;
		}

		@Override
		public CompoundTag serialize() {
			CompoundTag nbt = new CompoundTag();
			nbt.putFloat("temperature", this.getTemperature());
			return nbt;
		}
		
	}
}
