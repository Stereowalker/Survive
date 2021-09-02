package com.stereowalker.survive.util;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.potion.SEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;

public class SleepStats extends SurviveStats {
	private int awakeTimer;

	public SleepStats() {
		this.awakeTimer = 0;
	}

	public void addAwakeTime(ServerPlayerEntity player, int awakeTime) {
		if (player.interactionManager.survivalOrAdventure()) {
			this.awakeTimer+=awakeTime;
			if (this.awakeTimer < 0 ) {
				this.awakeTimer = 0;
			}
		}
	}


	@Override
	public void tick(PlayerEntity player) {
		if (!player.world.isRemote) {
			ServerPlayerEntity serverplayer = (ServerPlayerEntity)player;
			if (player.isSleeping())
				addAwakeTime(serverplayer, -player.getSleepTimer());
			else if (serverplayer.getServerWorld().getDimensionType().doesBedWork())
				addAwakeTime(serverplayer, 1);
			if (player.ticksExisted % 20 == 0) {
				addTiredEffect(serverplayer);
			}
		}
	}

	public void addTiredEffect(ServerPlayerEntity player) {
		if (tirednessAmplifier(player) >= 0 && !player.isPotionActive(SEffects.ENERGIZED)) {
			player.addPotionEffect(new EffectInstance(SEffects.TIREDNESS, 200, Math.min(tirednessAmplifier(player), Config.tiredTimeStacks), false, false, true));
		}
	}

	public int tirednessAmplifier(PlayerEntity player) {
		SleepStats stats = SurviveEntityStats.getSleepStats(player);
		float extraTime = stats.getAwakeTimer() - Config.initialTiredTime;
		return MathHelper.floor(extraTime/Config.tiredTimeStep);
	}

	@Override
	public void read(CompoundNBT compound) {
		if (compound.contains("awakeTimer", 99)) {
			this.awakeTimer = compound.getInt("awakeTimer");
		}
	}

	@Override
	public void write(CompoundNBT compound) {
		compound.putInt("awakeTimer", this.awakeTimer);
	}

	@Override
	public void save(LivingEntity player) {
		SurviveEntityStats.setSleepStats(player, this);
	}

	@Override
	public boolean shouldTick() {
		return Config.enable_sleep;
	}

	public int getAwakeTimer() {
		return awakeTimer;
	}

	public void setAwakeTimer(int awakeTimer) {
		this.awakeTimer = awakeTimer;
	}

}
