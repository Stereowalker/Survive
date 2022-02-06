package com.stereowalker.survive.needs;

import java.util.Random;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.world.effect.SEffects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class WellbeingData extends SurviveData {

	public boolean isWell;
	public int timeUntilUnwell = 0;
	public int timeUntilWell = 0;
	public int timeUntilHypothermia;
	public int timeUntilHyperthermia;

	public void setTimer(int min, int max) {
		if (this.timeUntilUnwell == 0 && this.isWell && this.shouldTick())
			this.timeUntilUnwell = min+this.rng.nextInt(max-min);
	}

	public WellbeingData() {
		super();
		this.isWell = true;
		this.timeUntilHyperthermia = 6000;
		this.timeUntilHyperthermia = 6000;
	}

	@Override
	public void tick(Player player) {
		//This is logic for diseases
		//If we have a timer to make us unwell, then tick that timer down
		if (this.timeUntilUnwell > 1 && this.isWell) {	
			this.timeUntilWell = 0;
			this.timeUntilUnwell--;
		} 
		//The moment that timer hits 1, set it to zero, make the player unwell and start the wellness timer
		else if (this.timeUntilUnwell == 1) {
			this.timeUntilUnwell = 0;
			this.isWell = false;
			//Set custom timers
			this.timeUntilWell = 6000;
			int rgn = new Random().nextInt(2);
			if (rgn == 0)
				player.addEffect(new MobEffectInstance(SEffects.SLOWNESS_ILLNESS, 6000));
			else
				player.addEffect(new MobEffectInstance(SEffects.WEAKNESS_ILLNESS, 6000));
		}
		//As long as the player is not well
		else if (this.timeUntilWell > 1 && !this.isWell) {
			this.timeUntilUnwell = 0;
			this.timeUntilWell--;
		}
		//The moment that timer hits 1, set all timers to zero and make the player well
		else if (this.timeUntilWell == 1) {
			this.timeUntilWell = 0;
			this.isWell = true;
			this.timeUntilWell = 0;
		}

		//This should be logic for hypothermia
		if (Survive.TEMPERATURE_CONFIG.useExperimentalTemperatureSystem && Survive.TEMPERATURE_CONFIG.enabled) {
			TemperatureData data = SurviveEntityStats.getTemperatureStats(player);
			if (data.getTemperatureLevel() > 0.7f) {
				this.timeUntilHyperthermia--;
			} else {
				this.timeUntilHyperthermia = 6000;
			}

			if (data.getTemperatureLevel() < -0.7f) {
				this.timeUntilHypothermia--;
			} else {
				this.timeUntilHypothermia = 6000;
			}

			if (this.timeUntilHyperthermia <= 0) {
				this.timeUntilHyperthermia = 0;
				if (!player.hasEffect(SEffects.HYPERTHERMIA))player.addEffect(new MobEffectInstance(SEffects.HYPERTHERMIA, 6000));
			}
			if (this.timeUntilHypothermia <= 0) {
				this.timeUntilHypothermia = 0;
				if (!player.hasEffect(SEffects.HYPOTHERMIA))player.addEffect(new MobEffectInstance(SEffects.HYPOTHERMIA, 6000));
			}
		}
	}

	@Override
	public void read(CompoundTag compound) {
		if (compound.contains("timeUntilUnwell", 99)) {
			this.timeUntilUnwell = compound.getInt("timeUntilUnwell");
			this.timeUntilWell = compound.getInt("timeUntilWell");
			this.timeUntilHypothermia = compound.getInt("timeUntilHypothermia");
			this.timeUntilHyperthermia = compound.getInt("timeUntilHyperthermia");
			this.isWell = compound.getBoolean("timeUntilWell");
		}
	}

	@Override
	public void write(CompoundTag compound) {
		compound.putInt("timeUntilUnwell", this.timeUntilUnwell);
		compound.putInt("timeUntilHypothermia", this.timeUntilHypothermia);
		compound.putInt("timeUntilHyperthermia", this.timeUntilHyperthermia);
		compound.putBoolean("isWell", this.isWell);
	}

	@Override
	public void save(LivingEntity player) {
		SurviveEntityStats.setWellbeingStats(player, this);
	}

	@Override
	public boolean shouldTick() {
		return Survive.WELLBEING_CONFIG.enabled;
	}

}
