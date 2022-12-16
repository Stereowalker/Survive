package com.stereowalker.survive.needs;

import java.util.Random;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.particles.SParticleTypes;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HygieneData extends SurviveData {
	private int uncleanLevel = 10;
	private int hygieneTimer;

	public HygieneData() {
		this.uncleanLevel = 20;
	}

	/**
	 * Attempts to clean the player
	 * @param cleanLevelIn - Caps at zero
	 */
	public void clean(int cleanLevelIn, boolean isSkinnyDipping) {
		if ((this.uncleanLevel > 7 && isSkinnyDipping) || !isSkinnyDipping)
			this.uncleanLevel = Math.max(this.uncleanLevel - cleanLevelIn, 0);
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
	public void clientTick(AbstractClientPlayer player) {
		if (!player.isCreative() && !player.isSpectator()) {
			if (this.needsABath()) {
				Random rand = new Random();
				for(int i = 0; i < ((this.uncleanLevel-25)/10)+2; ++i) {
					player.level.addParticle(SParticleTypes.STINK, player.getRandomX(0.5D), player.getRandomY() - 0.25D, player.getRandomZ(0.5D), (rand.nextDouble() - 0.5D) * 0.5D, -rand.nextDouble() * 0.5D, (rand.nextDouble() - 0.5D) * 0.5D);
				}
			}
			if (this.isSqueakyClean()) {
				Random rand = new Random();
				player.level.addParticle(SParticleTypes.CLEAN, player.getRandomX(0.5D), player.getRandomY() - 0.25D, player.getRandomZ(0.5D), (rand.nextDouble() - 0.5D) * 0.5D, -rand.nextDouble() * 0.5D, (rand.nextDouble() - 0.5D) * 0.5D);
			}
		}
	}

	/**
	 * Handles the water game logic.
	 */
	public void tick(Player player) {
		if (!player.isCreative() && !player.isSpectator()) {
			++this.hygieneTimer;
			if (this.hygieneTimer >= 200 && player.isInWaterOrRain()) {
				this.clean(1, true);
				this.hygieneTimer = 0;
			} else if (Survive.HYGIENE_CONFIG.dirtyTickRate >= 0 && this.hygieneTimer >= Survive.HYGIENE_CONFIG.dirtyTickRate) {
				this.dirty(1);
				this.hygieneTimer = 0;
			}
			
			if (this.uncleanLevel > 100 && Survive.WELLBEING_CONFIG.enabled) {
				SurviveEntityStats.getWellbeingStats(player).setTimer(6000, 24000);
			}
		} else {
			this.hygieneTimer = 0;
		}

	}

	/**
	 * Reads the hygiene data for the player.
	 */
	public void read(CompoundTag compound) {
		if (compound.contains("uncleanLevel", 99)) {
			this.uncleanLevel = compound.getInt("uncleanLevel");
			this.hygieneTimer = compound.getInt("hygieneTimer");
		}

	}

	/**
	 * Writes the hygiene data for the player.
	 */
	public void write(CompoundTag compound) {
		compound.putFloat("uncleanLevel", this.uncleanLevel);
		compound.putInt("hygieneTimer", this.hygieneTimer);
	}

	/**
	 * Get the player's unclean level.
	 */
	public int getUncleanLevel() {
		return this.uncleanLevel;
	}
	
	/**
	 * Get whether the player is plenty clean.
	 */
	public boolean isSqueakyClean() {
		return this.uncleanLevel < 5;
	}

	/**
	 * Get whether the player should take a shower.
	 */
	public boolean needsABath() {
		return this.uncleanLevel > 25;
	}

	/**
	 * Should the player be avoided by all animals except pigs?
	 */
	public boolean shouldBeAvoidedByAnimals() {
		return this.uncleanLevel > 35;
	}

	/**
	 * Should the player be avoided by pigs?
	 */
	public boolean shouldBeAvoidedByPigs() {
		return this.uncleanLevel > 45;
	}

	@Override
	public void save(LivingEntity player) {
		SurviveEntityStats.setHygieneStats(player, this);
	}

	@Override
	public boolean shouldTick() {
		return Survive.HYGIENE_CONFIG.enabled;
	}
}
