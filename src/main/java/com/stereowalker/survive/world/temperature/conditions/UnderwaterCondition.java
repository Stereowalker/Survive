package com.stereowalker.survive.world.temperature.conditions;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	public Instance createInstance(CompoundTag nbt) {
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
		public boolean shouldChangeTemperature(Player player) {
			boolean flag = true;
			int i = 0;
			while(flag) {
				if (player.level.getFluidState(player.blockPosition().above(i)).is(FluidTags.WATER)) {
					i++;
				} else {
					flag = false;
				}
			}
			if (player.isInWater()) {
				if (this.depth > 0) {
					if (this.operation.equals("y_=")) {
						return player.getY() == this.depth;
					} else if (this.operation.equals("y_>=")) {
						return player.getY() >= this.depth;
					} else if (this.operation.equals("y_<=")) {
						return player.getY() <= this.depth;
					} else if (this.operation.equals("y_>")) {
						return player.getY() > this.depth;
					} else if (this.operation.equals("y_<")) {
						return player.getY() < this.depth;
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

		@Override
		public CompoundTag serialize() {
			CompoundTag nbt = new CompoundTag();
			nbt.putFloat("temperature", this.getTemperature());
			nbt.putInt("depth", this.depth);
			nbt.putString("operation", this.operation);
			return nbt;
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public Component getAdditionalContext() {
			return new TranslatableComponent("temperature_context.underwater");
		}
		
	}
}
