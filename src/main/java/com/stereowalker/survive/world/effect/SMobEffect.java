package com.stereowalker.survive.world.effect;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.needs.SDamageSource;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SMobEffect extends MobEffect {

	public SMobEffect(MobEffectCategory effectType, int liquidColorIn) {
		super(effectType, liquidColorIn);
	}

	@Override
	public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
		if (entityLivingBaseIn instanceof IRealisticEntity) {
			IRealisticEntity realisticEntity = (IRealisticEntity)entityLivingBaseIn;
			if (!entityLivingBaseIn.level.isClientSide) {
				if (this == SMobEffects.THIRST) {
					realisticEntity.getWaterData().addExhaustion((Player) entityLivingBaseIn, (0.005F * (float)(amplifier + 1)));
					realisticEntity.getWaterData().save(entityLivingBaseIn);
				}
			}
			if (this == SMobEffects.DEPRECIATED_HYPOTHERMIA && entityLivingBaseIn instanceof Player) {
				boolean flag = !entityLivingBaseIn.level.getDifficulty().equals(Difficulty.HARD) && entityLivingBaseIn.getHealth() > 1.0F;
				if (entityLivingBaseIn.level.getDifficulty().equals(Difficulty.HARD)) flag = true;
				if ((!((Player)entityLivingBaseIn).isSleeping() || !Survive.TEMPERATURE_CONFIG.hyp_allow_sleep) && flag) {
					entityLivingBaseIn.hurt(SDamageSource.HYPOTHERMIA, 0.4F);
				}
			} else if (this == SMobEffects.DEPRECIATED_HYPERTHERMIA && entityLivingBaseIn instanceof Player) {
				boolean flag = !entityLivingBaseIn.level.getDifficulty().equals(Difficulty.HARD) && entityLivingBaseIn.getHealth() > 1.0F;
				if (entityLivingBaseIn.level.getDifficulty().equals(Difficulty.HARD)) flag = true;
				if ((!((Player)entityLivingBaseIn).isSleeping() || !Survive.TEMPERATURE_CONFIG.hyp_allow_sleep) && flag) {
					entityLivingBaseIn.hurt(SDamageSource.HYPERTHERMIA, 0.4F);
				}
			} else if (this == SMobEffects.ENERGIZED && entityLivingBaseIn instanceof ServerPlayer) {
				StaminaData energyStats = SurviveEntityStats.getEnergyStats(entityLivingBaseIn);
				energyStats.relax(1, entityLivingBaseIn.getAttributeValue(SAttributes.MAX_STAMINA));
				energyStats.save(entityLivingBaseIn);
				if (entityLivingBaseIn.hasEffect(SMobEffects.TIREDNESS)) {
					entityLivingBaseIn.removeEffect(SMobEffects.TIREDNESS);
				}
			} else if (this == SMobEffects.TIREDNESS && entityLivingBaseIn instanceof ServerPlayer) {
				StaminaData energyStats = SurviveEntityStats.getEnergyStats(entityLivingBaseIn);
				energyStats.addExhaustion((Player) entityLivingBaseIn, (0.0125F * (float)(amplifier + 1)), "Tiredness effect");
				SurviveEntityStats.setStaminaStats(entityLivingBaseIn, energyStats);
			}
		}
		super.applyEffectTick(entityLivingBaseIn, amplifier);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		if (this == SMobEffects.DEPRECIATED_HYPOTHERMIA) {
			int k = 40 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SMobEffects.DEPRECIATED_HYPERTHERMIA) {
			int k = 40 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SMobEffects.CHILLED) {
			int k = 60 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SMobEffects.HEATED) {
			int k = 60 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SMobEffects.TIREDNESS) {
			int k = 80 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SMobEffects.ENERGIZED) {
			int k = 60 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else {
			return this == SMobEffects.THIRST;
		}
	}
}
