package com.stereowalker.survive.needs;

import java.util.Random;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class SurviveData {
	private static final Logger LOGGER = LogUtils.getLogger();
	Random rng;
	public abstract void tick(Player player);
	public abstract void read(ValueInput compound);
	public abstract void write(ValueOutput compound, boolean reducedData);
	public abstract void save(LivingEntity player);
	public abstract boolean shouldTick();
	
	public CompoundTag write(boolean reducedData) {
		try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
			TagValueOutput tag = TagValueOutput.createWithoutContext(reporter);
			write(tag, reducedData);
			return tag.buildResult();
		}
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
