package com.stereowalker.survive.needs;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.world.effect.SEffects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SleepData extends SurviveData {
	private int awakeTimer;

	public SleepData() {
		this.awakeTimer = 0;
	}

	public void addAwakeTime(ServerPlayer player, int awakeTime) {
		if (player.gameMode.isSurvival()) {
			this.awakeTimer+=awakeTime;
			if (this.awakeTimer < 0 ) {
				this.awakeTimer = 0;
			}
		}
	}


	@Override
	public void tick(Player player) {
		if (!player.level.isClientSide) {
			ServerPlayer serverplayer = (ServerPlayer)player;
			if (player.isSleeping())
				addAwakeTime(serverplayer, -player.getSleepTimer());
			else if (serverplayer.getLevel().dimensionType().bedWorks())
				addAwakeTime(serverplayer, 1);
			if (player.tickCount % 20 == 0) {
				addTiredEffect(serverplayer);
			}
		}
	}

	public void addTiredEffect(ServerPlayer player) {
		if (tirednessAmplifier(player) >= 0 && !player.hasEffect(SEffects.ENERGIZED)) {
			player.addEffect(new MobEffectInstance(SEffects.TIREDNESS, 200, Math.min(tirednessAmplifier(player), Survive.CONFIG.tiredTimeStacks), false, false, true));
		}
	}

	public int tirednessAmplifier(Player player) {
		SleepData stats = SurviveEntityStats.getSleepStats(player);
		float extraTime = stats.getAwakeTimer() - Survive.CONFIG.initialTiredTime;
		return Mth.floor(extraTime/Survive.CONFIG.tiredTimeStep);
	}

	@Override
	public void read(CompoundTag compound) {
		if (compound.contains("awakeTimer", 99)) {
			this.awakeTimer = compound.getInt("awakeTimer");
		}
	}

	@Override
	public void write(CompoundTag compound) {
		compound.putInt("awakeTimer", this.awakeTimer);
	}

	@Override
	public void save(LivingEntity player) {
		SurviveEntityStats.setSleepStats(player, this);
	}

	@Override
	public boolean shouldTick() {
		return Survive.CONFIG.enable_sleep;
	}

	public int getAwakeTimer() {
		return awakeTimer;
	}

	public void setAwakeTimer(int awakeTimer) {
		this.awakeTimer = awakeTimer;
	}

}
