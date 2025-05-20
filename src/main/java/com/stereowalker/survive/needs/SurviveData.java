package com.stereowalker.survive.needs;

import java.util.Random;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public abstract class SurviveData {
	Random rng;
	public abstract void tick(Player player);
	public abstract void read(CompoundTag compound);
	public abstract void write(CompoundTag compound, boolean reducedData);
	public abstract void save(LivingEntity player);
	public abstract boolean shouldTick();
	
	public CompoundTag write(boolean reducedData) {
		CompoundTag tag = new CompoundTag();
		write(tag, reducedData);
		return tag;
	}
	
	public SurviveData() {
		this.rng = new Random();
	}
	
	public void baseTick(Player player) {
		if (shouldTick()) {
			tick(player);
			save(player);
		}
	}
	
	public void baseClientTick(Player player) {
		if (shouldTick()) {
			clientTick(player);
		}
	}
	
	public void clientTick(Player player) {
		
	}
}
