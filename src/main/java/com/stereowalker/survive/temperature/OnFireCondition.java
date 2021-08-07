package com.stereowalker.survive.temperature;

import com.google.gson.JsonObject;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

		@Override
		public CompoundNBT serialize() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putFloat("temperature", this.getTemperature());
			nbt.putInt("fireTimer", this.fireTimer);
			nbt.putString("operation", this.operation);
			return nbt;
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public ITextComponent getAdditionalContext() {
			if (this.fireTimer > 0) {
				return new TranslationTextComponent("temperature_context.on_fire", this.operation.equals("") ? "<" : this.operation, this.fireTimer);
			} else {
				return new TranslationTextComponent("temperature_context.on_fire_no_timer");
			}
		}
	}
}
