package com.stereowalker.survive.needs;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * @author stereowalker
 *
 */
public class WellbeingData extends SurviveData {

	private boolean isWell;
	public int timeUntilUnwell = 0;
	public int timeUntilWell = 0;
	public int timeUntilHypothermia;
	public int timeUntilHyperthermia;
	private int intensity;
	private String reason;

	/**
	 * Sets the time (in ticks) before the player becomes unwell
	 * @param min - The minimum amount of ticks
	 * @param max - The maximum amount of ticks
	 */
	public void setTimer(int min, int max, String reason) {
		if (this.timeUntilUnwell == 0 && this.isWell && this.shouldTick()) {
			this.timeUntilUnwell = min+this.rng.nextInt(max-min);
			this.reason = reason;
		}
	}

	/**
	 * Sets the time (in ticks) before the player becomes unwell
	 * @param min - The minimum amount of ticks
	 * @param max - The maximum amount of ticks
	 * @param intensity - This sets the amplifier of the illness
	 */
	public void setTimer(int min, int max, int intensity, String reason) {
		if (this.timeUntilUnwell == 0 && this.isWell && this.shouldTick()) {
			this.timeUntilUnwell = min+this.rng.nextInt(max-min);
			this.reason = reason;
			this.intensity = intensity;
		}
	}

	public WellbeingData() {
		super();
		this.isWell = true;
		this.reason = "";
		this.timeUntilHyperthermia = 6000;
		this.timeUntilHyperthermia = 6000;
		this.intensity = -1;
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
			if (this.intensity == -1) {
				int max = 0;
				for (int i = 1; i <= 9; i++) max+=i;
				int nte = rng.nextInt(max+1);
				for (int i = 9; i >= 0; i--) {
					if (nte >= max) {
						this.intensity = i;
						break;
					}
					else max -= 10 - i;
				}	
			}
			
			int rgn = rng.nextInt(2);
			if (rgn == 0)
				player.addEffect(new MobEffectInstance(SMobEffects.SLOWNESS_ILLNESS, this.timeUntilWell, this.intensity));
			else
				player.addEffect(new MobEffectInstance(SMobEffects.WEAKNESS_ILLNESS, this.timeUntilWell, this.intensity));
		}
		//As long as the player is not well
		else if (this.timeUntilWell > 1 && !this.isWell) {
			this.timeUntilUnwell = 0;
			this.timeUntilWell--;
		}
		//The moment that timer hits 1, set all timers to zero and make the player well
		else if (this.timeUntilWell == 1) {
			this.isWell = true;
			this.timeUntilWell = 0;
			this.reason = "";
			this.intensity = -1;
		}

		//This should be logic for hypothermia
		if (!Survive.TEMPERATURE_CONFIG.useLegacyTemperatureSystem && Survive.TEMPERATURE_CONFIG.enabled) {
			TemperatureData data = SurviveEntityStats.getTemperatureStats(player);

			double tempLocation = data.getTemperatureLevel() - Survive.DEFAULT_TEMP;
			double f = 0;
			if (tempLocation > 0) {
				double maxTemp = 0.0D;
				if (player.getAttribute(SAttributes.HEAT_RESISTANCE) != null) {
					maxTemp = player.getAttributeValue(SAttributes.HEAT_RESISTANCE);
				}
				double div = tempLocation / maxTemp;
				f = Mth.clamp(div, 0, 1.0D+(28.0D/63.0D));
			}
			if (tempLocation < 0) {
				double maxTemp = 0.0D;
				if (player.getAttribute(SAttributes.COLD_RESISTANCE) != null) {
					maxTemp = player.getAttributeValue(SAttributes.COLD_RESISTANCE);
				}
				double div = tempLocation / maxTemp;
				f = Mth.clamp(div, -1.0D-(28.0D/63.0D), 0);
			}

			if (f > 0.7f && !player.hasEffect(SMobEffects.HYPERTHERMIA)) {
				this.timeUntilHyperthermia--;
			} else {
				this.timeUntilHyperthermia = 6000;
			}

			if (f < -0.7f && !player.hasEffect(SMobEffects.HYPOTHERMIA)) {
				this.timeUntilHypothermia--;
			} else {
				this.timeUntilHypothermia = 6000;
			}

			if (this.timeUntilHyperthermia <= 0) {
				if (!player.hasEffect(SMobEffects.HYPERTHERMIA))player.addEffect(new MobEffectInstance(SMobEffects.HYPERTHERMIA, 6000));
			}
			if (this.timeUntilHypothermia <= 0) {
				if (!player.hasEffect(SMobEffects.HYPOTHERMIA))player.addEffect(new MobEffectInstance(SMobEffects.HYPOTHERMIA, 6000));
			}
		}
	}

	@Override
	public void read(CompoundTag compound) {
		if (compound.contains("timeUntilUnwell", 99)) {
			this.isWell = compound.getBoolean("isWell");
			this.timeUntilWell = compound.getInt("timeUntilWell");
			this.timeUntilUnwell = compound.getInt("timeUntilUnwell");
			this.timeUntilHypothermia = compound.getInt("timeUntilHypothermia");
			this.timeUntilHyperthermia = compound.getInt("timeUntilHyperthermia");
			this.intensity = compound.getInt("unwellIntensity");
			this.reason = compound.getString("unwellReason");
		}
	}

	@Override
	public void write(CompoundTag compound) {
		compound.putBoolean("isWell", this.isWell);
		compound.putInt("timeUntilWell", this.timeUntilWell);
		compound.putInt("timeUntilUnwell", this.timeUntilUnwell);
		compound.putInt("timeUntilHypothermia", this.timeUntilHypothermia);
		compound.putInt("timeUntilHyperthermia", this.timeUntilHyperthermia);
		compound.putInt("unwellIntensity", this.intensity);
		compound.putString("unwellReason", this.reason);
	}

	@Override
	public void save(LivingEntity player) {
	}

	@Override
	public boolean shouldTick() {
		return Survive.WELLBEING_CONFIG.enabled;
	}

	/**
	 * Is the player not ill?
	 */
	public boolean isWell() {
		return isWell;
	}
	
	/**
	 * Gets the intensity of the applied illness
	 */
	public int getIntensity() {
		return intensity;
	}
	
	/**
	 * The cause of the current illness in the player
	 */
	public String getReason() {
		return reason;
	}

}
