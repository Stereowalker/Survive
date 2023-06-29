package com.stereowalker.survive.world.effect;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.SDamageSource;
import com.stereowalker.survive.needs.StaminaData;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class UnwellMobEffect extends MobEffect {

	public UnwellMobEffect(MobEffectCategory effectType, int liquidColorIn) {
		super(effectType, liquidColorIn);
	}

	@Override
	public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
		if (entityLivingBaseIn instanceof Player) {
			StaminaData energyStats = SurviveEntityStats.getEnergyStats(entityLivingBaseIn);
			if (this == SMobEffects.HYPOTHERMIA) {
				if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth() / 3.5F)
					entityLivingBaseIn.hurt(SDamageSource.HYPOTHERMIA, 0.8F);
				if ((float) energyStats.getEnergyLevel() > ((float) energyStats.getEnergyLevel()) * 0.3)
					energyStats.addExhaustion((Player) entityLivingBaseIn, (1.0F * (float) (amplifier + 1)), "Hypothermia effect");
			} else if (this == SMobEffects.HYPERTHERMIA) {
				if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth() / 3.5F)
					entityLivingBaseIn.hurt(SDamageSource.HYPERTHERMIA, 0.8F);
				if ((float) energyStats.getEnergyLevel() > ((float) energyStats.getEnergyLevel()) * 0.3)
					energyStats.addExhaustion((Player) entityLivingBaseIn, (1.0F * (float) (amplifier + 1)), "Hyperthermia effect");
			}
			energyStats.save(entityLivingBaseIn);
		}
		super.applyEffectTick(entityLivingBaseIn, amplifier);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		if (this == SMobEffects.HYPOTHERMIA) {
			int k = 160 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SMobEffects.HYPERTHERMIA) {
			int k = 160 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		return ret;
	}
}
