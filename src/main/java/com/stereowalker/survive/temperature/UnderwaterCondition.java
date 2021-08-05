package com.stereowalker.survive.temperature;

import com.google.gson.JsonObject;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;

public class UnderwaterCondition extends TemperatureChangeCondition<UnderwaterCondition.Instance>{

	public UnderwaterCondition() {
		super();
	}

	@Override
	public Instance createInstance(JsonObject object) {
		float temperatureIn = 0;
		int depthIn = 0;
		String operationIn = "";
		if(object.has("temperature") && object.get("temperature").isJsonPrimitive()) {
			temperatureIn = object.get("temperature").getAsFloat();
		}
		if(object.has("depth") && object.get("depth").isJsonPrimitive()) {
			depthIn = object.get("depth").getAsInt();
		}
		if(object.has("operation") && object.get("operation").isJsonPrimitive()) {
			operationIn = object.get("operation").getAsString();
		}
		return new Instance(temperatureIn, depthIn, operationIn);
	}
	
	@Override
	public Instance createInstance(CompoundNBT nbt) {
		float temperatureIn = nbt.getFloat("temperature");
		int depthIn = nbt.getInt("depth");
		String operationIn = nbt.getString("operation");
		return new Instance(temperatureIn, depthIn, operationIn);
	}
	
	static class Instance extends TemperatureChangeInstance {
		private int depth;
		private String operation;

		public Instance(float temperatureIn, int depthIn, String operationIn) {
			super(temperatureIn);
			this.depth = depthIn;
			this.operation = operationIn;
		}

		@Override
		public boolean shouldChangeTemperature(PlayerEntity player) {
			boolean flag = true;
			int i = 0;
			while(flag) {
				if (player.world.getFluidState(player.getPosition().up(i)).isTagged(FluidTags.WATER)) {
					i++;
				} else {
					flag = false;
				}
			}
			if (player.isInWater()) {
				if (this.depth > 0) {
					if (this.operation.equals("y_=")) {
						return player.getPosY() == this.depth;
					} else if (this.operation.equals("y_>=")) {
						return player.getPosY() >= this.depth;
					} else if (this.operation.equals("y_<=")) {
						return player.getPosY() <= this.depth;
					} else if (this.operation.equals("y_>")) {
						return player.getPosY() > this.depth;
					} else if (this.operation.equals("y_<")) {
						return player.getPosY() < this.depth;
					} else if (this.operation.equals("water_=")) {
						return i == this.depth;
					} else if (this.operation.equals("water_>=")) {
						return i >= this.depth;
					} else if (this.operation.equals("water_<=")) {
						return i <= this.depth;
					} else if (this.operation.equals("water_>")) {
						return i > this.depth;
					} else if (this.operation.equals("water_<")) {
						return i < this.depth;
					}
					return i <= this.depth;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
		
	}
}
