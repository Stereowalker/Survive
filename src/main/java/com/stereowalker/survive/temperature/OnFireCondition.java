package com.stereowalker.survive.temperature;

import com.google.gson.JsonObject;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class OnFireCondition extends TemperatureChangeCondition<OnFireCondition.Instance>{

	public OnFireCondition() {
		super();
	}

	@Override
	public Instance createInstance(JsonObject object) {
		float temperatureIn = 0;
		int fireTimerIn = 0;
		String operationIn = "";
		if(object.has("temperature") && object.get("temperature").isJsonPrimitive()) {
			temperatureIn = object.get("temperature").getAsFloat();
		}
		if(object.has("fireTimer") && object.get("fireTimer").isJsonPrimitive()) {
			fireTimerIn = object.get("fireTimer").getAsInt();
		}
		if(object.has("operation") && object.get("operation").isJsonPrimitive()) {
			operationIn = object.get("operation").getAsString();
		}
		return new Instance(temperatureIn, fireTimerIn, operationIn);
	}
	
	@Override
	public Instance createInstance(CompoundNBT nbt) {
		float temperatureIn = nbt.getFloat("temperature");
		int fireTimerIn = nbt.getInt("fireTimer");
		String operationIn = nbt.getString("operation");
		return new Instance(temperatureIn, fireTimerIn, operationIn);
	}

	static class Instance extends TemperatureChangeInstance {
		private int fireTimer;
		private String operation;

		public Instance(float temperatureIn, int fireTimerIn, String operationIn) {
			super(temperatureIn);
			this.fireTimer = fireTimerIn;
			this.operation = operationIn;
		}

		@Override
		public boolean shouldChangeTemperature(PlayerEntity player) {
			if (player.isBurning()) {
				if (this.fireTimer > 0) {
					if (this.operation.equals("=")) {
						return player.getFireTimer() == this.fireTimer;
					} else if (this.operation.equals(">=")) {
						return player.getFireTimer() >= this.fireTimer;
					} else if (this.operation.equals("<=")) {
						return player.getFireTimer() <= this.fireTimer;
					} else if (this.operation.equals(">")) {
						return player.getFireTimer() > this.fireTimer;
					} else if (this.operation.equals("<")) {
						return player.getFireTimer() < this.fireTimer;
					}
					return player.getFireTimer() < this.fireTimer;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
	}
}
