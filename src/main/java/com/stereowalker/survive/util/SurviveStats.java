package com.stereowalker.survive.util;

import java.util.Random;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public abstract class SurviveStats {
	Random rng;
	public abstract void tick(PlayerEntity player);
	public abstract void read(CompoundNBT compound);
	public abstract void write(CompoundNBT compound);
	public abstract void save(LivingEntity player);
	public abstract boolean shouldTick();
	
	public SurviveStats() {
		this.rng = new Random();
	}
	
	public void baseTick(PlayerEntity player) {
		if (shouldTick()) {
			tick(player);
			save(player);
		}
	}
	
	public void baseClientTick(AbstractClientPlayerEntity player) {
		if (shouldTick()) {
			clientTick(player);
		}
	}
	
	public void clientTick(AbstractClientPlayerEntity player) {
		
	}
}
