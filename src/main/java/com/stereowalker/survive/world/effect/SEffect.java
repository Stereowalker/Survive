package com.stereowalker.survive.world.effect;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.SDamageSource;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.survive.needs.TemperatureData;
import com.stereowalker.survive.needs.WaterData;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SEffect extends MobEffect {

	public SEffect(MobEffectCategory effectType, int liquidColorIn) {
		super(effectType, liquidColorIn);
	}

	@Override
	public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
		if (this == SEffects.THIRST && entityLivingBaseIn instanceof ServerPlayer) {
			WaterData waterStats = SurviveEntityStats.getWaterStats(entityLivingBaseIn);
			waterStats.addExhaustion((Player) entityLivingBaseIn, (0.005F * (float)(amplifier + 1)));
			waterStats.save(entityLivingBaseIn);
		} else if (this == SEffects.DEPRECIATED_HYPOTHERMIA && entityLivingBaseIn instanceof Player) {
			boolean flag = !entityLivingBaseIn.level.getDifficulty().equals(Difficulty.HARD) && entityLivingBaseIn.getHealth() > 1.0F;
			if (entityLivingBaseIn.level.getDifficulty().equals(Difficulty.HARD)) flag = true;
			if ((!((Player)entityLivingBaseIn).isSleeping() || !Survive.TEMPERATURE_CONFIG.hyp_allow_sleep) && flag) {
				entityLivingBaseIn.hurt(SDamageSource.HYPOTHERMIA, 0.4F);
			}
		} else if (this == SEffects.DEPRECIATED_HYPERTHERMIA && entityLivingBaseIn instanceof Player) {
			boolean flag = !entityLivingBaseIn.level.getDifficulty().equals(Difficulty.HARD) && entityLivingBaseIn.getHealth() > 1.0F;
			if (entityLivingBaseIn.level.getDifficulty().equals(Difficulty.HARD)) flag = true;
			if ((!((Player)entityLivingBaseIn).isSleeping() || !Survive.TEMPERATURE_CONFIG.hyp_allow_sleep) && flag) {
				entityLivingBaseIn.hurt(SDamageSource.HYPERTHERMIA, 0.4F);
			}
		} else if (this == SEffects.CHILLED && entityLivingBaseIn instanceof ServerPlayer) {
			TemperatureData stats = SurviveEntityStats.getTemperatureStats((ServerPlayer) entityLivingBaseIn);
			TemperatureData.setTemperatureModifier(entityLivingBaseIn, "survive:chilled_effect", -(0.05F * (float)(amplifier + 1)));
			SurviveEntityStats.setTemperatureStats((ServerPlayer) entityLivingBaseIn, stats);
		} else if (this == SEffects.HEATED && entityLivingBaseIn instanceof ServerPlayer) {
			TemperatureData stats = SurviveEntityStats.getTemperatureStats((ServerPlayer) entityLivingBaseIn);
			TemperatureData.setTemperatureModifier(entityLivingBaseIn, "survive:heated_effect", +(0.05F * (float)(amplifier + 1)));
			SurviveEntityStats.setTemperatureStats((ServerPlayer) entityLivingBaseIn, stats);
		} else if (this == SEffects.ENERGIZED && entityLivingBaseIn instanceof ServerPlayer) {
			StaminaData energyStats = SurviveEntityStats.getEnergyStats(entityLivingBaseIn);
			energyStats.addStats(1);
			energyStats.save(entityLivingBaseIn);
			if (entityLivingBaseIn.hasEffect(SEffects.TIREDNESS)) {
				entityLivingBaseIn.removeEffect(SEffects.TIREDNESS);
			}
		} else if (this == SEffects.TIREDNESS && entityLivingBaseIn instanceof ServerPlayer) {
			StaminaData energyStats = SurviveEntityStats.getEnergyStats(entityLivingBaseIn);
			energyStats.addExhaustion((Player) entityLivingBaseIn, (0.0125F * (float)(amplifier + 1)), "Tiredness effect");
			SurviveEntityStats.setStaminaStats(entityLivingBaseIn, energyStats);
		}
		super.applyEffectTick(entityLivingBaseIn, amplifier);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		if (this == SEffects.DEPRECIATED_HYPOTHERMIA) {
			int k = 40 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SEffects.DEPRECIATED_HYPERTHERMIA) {
			int k = 40 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SEffects.CHILLED) {
			int k = 60 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SEffects.HEATED) {
			int k = 60 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SEffects.TIREDNESS) {
			int k = 80 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SEffects.ENERGIZED) {
			int k = 60 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else {
			return this == SEffects.THIRST;
		}
	}
}
