package com.stereowalker.survive.world.temperature.conditions;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class TemperatureChangeInstance {
	
	private float temperature;

	public TemperatureChangeInstance(float temperatureIn) {
		this.temperature = temperatureIn;
	}
	
	public abstract boolean shouldChangeTemperature(Player player);
	
	public abstract CompoundTag serialize();
	
	public float getTemperature() {
		return temperature;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Nullable
	public Component getAdditionalContext() {
		return null;
	}
}
