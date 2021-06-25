package com.stereowalker.survive.util;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.potion.SEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;

public class WellbeingStats extends SurviveStats {

	public boolean isWell;
	public int timeUntilUnwell = 0;
	public int timeUntilWell = 0;

	public void setTimer(int min, int max) {
		if (this.timeUntilUnwell == 0 && this.isWell && this.shouldTick())
			this.timeUntilUnwell = min+this.rng.nextInt(max-min);
	}
	
	public WellbeingStats() {
		super();
		this.isWell = true;
	}

	@Override
	public void tick(PlayerEntity player) {
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
			player.addPotionEffect(new EffectInstance(SEffects.SLOWNESS_ILLNESS, 6000));
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
	}

	@Override
	public void read(CompoundNBT compound) {
		if (compound.contains("timeUntilUnwell", 99)) {
			this.timeUntilUnwell = compound.getInt("timeUntilUnwell");
			this.timeUntilWell = compound.getInt("timeUntilWell");
			this.isWell = compound.getBoolean("timeUntilWell");
		}
	}

	@Override
	public void write(CompoundNBT compound) {
		compound.putInt("timeUntilUnwell", this.timeUntilUnwell);
		compound.putInt("timeUntilWell", this.timeUntilWell);
		compound.putBoolean("isWell", this.isWell);
	}

	@Override
	public void save(LivingEntity player) {
		SurviveEntityStats.setWellbeingStats(player, this);
	}

	@Override
	public boolean shouldTick() {
		return Config.wellbeing_enabled;
	}

}
