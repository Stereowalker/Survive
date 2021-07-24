package com.stereowalker.survive.util;

import java.util.Random;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.particles.SParticleTypes;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HygieneStats extends SurviveStats {
	private int uncleanLevel = 10;
	private int hygieneTimer;

	public HygieneStats() {
		this.uncleanLevel = 20;
	}

	/**
	 * Attempts to clean the player
	 * @param cleanLevelIn - Caps at zero
	 */
	public void clean(int cleanLevelIn) {
		this.uncleanLevel = Math.max(cleanLevelIn - this.uncleanLevel, 0);
	}

	/**
	 * Attempts to dirty the player
	 * @param cleanLevelIn - Caps at zero
	 */
	public void dirty(int dirtyLevelIn) {
		this.uncleanLevel = Math.max(dirtyLevelIn + this.uncleanLevel, 0);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientTick(AbstractClientPlayerEntity player) {
		if (this.needsABath()) {
			Random rand = new Random();
			for(int i = 0; i < 2; ++i) {
				player.world.addParticle(SParticleTypes.STINK, player.getPosXRandom(0.5D), player.getPosYRandom() - 0.25D, player.getPosZRandom(0.5D), (rand.nextDouble() - 0.5D) * 0.5D, -rand.nextDouble() * 0.5D, (rand.nextDouble() - 0.5D) * 0.5D);
			}
		}
	}

	/**
	 * Handles the water game logic.
	 */
	public void tick(PlayerEntity player) {
		if (!player.isCreative() && !player.isSpectator()) {
			++this.hygieneTimer;
			if (this.hygieneTimer >= 200 && player.isWet()) {
				this.clean(1);
				this.hygieneTimer = 0;
			} else if (this.hygieneTimer >= 500) {
				this.dirty(1);
				this.hygieneTimer = 0;
			}
			
			if (this.uncleanLevel > 100 && Config.wellbeing_enabled) {
				SurviveEntityStats.getWellbeingStats(player).setTimer(6000, 24000);
			}
		} else {
			this.hygieneTimer = 0;
		}

	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundNBT compound) {
		if (compound.contains("uncleanLevel", 99)) {
			this.uncleanLevel = compound.getInt("uncleanLevel");
			this.hygieneTimer = compound.getInt("hygieneTimer");
		}

	}

	/**
	 * Writes the water data for the player.
	 */
	public void write(CompoundNBT compound) {
		compound.putFloat("uncleanLevel", this.uncleanLevel);
		compound.putInt("hygieneTimer", this.hygieneTimer);
	}

	/**
	 * Get the player's water level.
	 */
	public int getUncleanLevel() {
		return this.uncleanLevel;
	}

	/**
	 * Get whether the player should take a shower.
	 */
	public boolean needsABath() {
		return this.uncleanLevel > 25;
	}

	@Override
	public void save(LivingEntity player) {
		SurviveEntityStats.setHygieneStats(player, this);
	}

	@Override
	public boolean shouldTick() {
		return Config.enable_hygiene;
	}
}
