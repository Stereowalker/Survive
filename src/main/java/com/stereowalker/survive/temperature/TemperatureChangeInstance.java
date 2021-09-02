package com.stereowalker.survive.temperature;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class TemperatureChangeInstance {
	
	private float temperature;

	public TemperatureChangeInstance(float temperatureIn) {
		this.temperature = temperatureIn;
	}
	
	public abstract boolean shouldChangeTemperature(PlayerEntity player);
	
	public abstract CompoundNBT serialize();
	
	public float getTemperature() {
		return temperature;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Nullable
	public ITextComponent getAdditionalContext() {
		return null;
	}
}
