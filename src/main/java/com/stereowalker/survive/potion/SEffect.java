package com.stereowalker.survive.potion;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.EnergyStats;
import com.stereowalker.survive.util.SDamageSource;
import com.stereowalker.survive.util.TemperatureStats;
import com.stereowalker.survive.util.WaterStats;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.world.Difficulty;

public class SEffect extends Effect{

	public SEffect(EffectType effectType, int liquidColorIn) {
		super(effectType, liquidColorIn);
	}

	@Override
	public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
		if (this == SEffects.THIRST && entityLivingBaseIn instanceof ServerPlayerEntity) {
			WaterStats waterStats = SurviveEntityStats.getWaterStats(entityLivingBaseIn);
			waterStats.addExhaustion((PlayerEntity) entityLivingBaseIn, (0.005F * (float)(amplifier + 1)));
			SurviveEntityStats.setWaterStats(entityLivingBaseIn, waterStats);
		} else if (this == SEffects.HYPOTHERMIA && entityLivingBaseIn instanceof PlayerEntity) {
			boolean flag = !entityLivingBaseIn.world.getDifficulty().equals(Difficulty.HARD) && entityLivingBaseIn.getHealth() > 1.0F;
			if (entityLivingBaseIn.world.getDifficulty().equals(Difficulty.HARD)) flag = true;
			if ((!((PlayerEntity)entityLivingBaseIn).isSleeping() || !Config.hyp_allow_sleep) && flag) {
				entityLivingBaseIn.attackEntityFrom(SDamageSource.HYPOTHERMIA, 0.4F);
			}
		} else if (this == SEffects.HYPERTHERMIA && entityLivingBaseIn instanceof PlayerEntity) {
			boolean flag = !entityLivingBaseIn.world.getDifficulty().equals(Difficulty.HARD) && entityLivingBaseIn.getHealth() > 1.0F;
			if (entityLivingBaseIn.world.getDifficulty().equals(Difficulty.HARD)) flag = true;
			if ((!((PlayerEntity)entityLivingBaseIn).isSleeping() || !Config.hyp_allow_sleep) && flag) {
				entityLivingBaseIn.attackEntityFrom(SDamageSource.HYPERTHERMIA, 0.4F);
			}
		} else if (this == SEffects.CHILLED && entityLivingBaseIn instanceof ServerPlayerEntity) {
			TemperatureStats stats = SurviveEntityStats.getTemperatureStats((ServerPlayerEntity) entityLivingBaseIn);
			//			stats.getOrCreateModifier(new ResourceLocation("survive:chilled_effect")).setMod(-(0.05F * (float)(amplifier + 1)));
			TemperatureStats.setTemperatureModifier(entityLivingBaseIn, "survive:chilled_effect", -(0.05F * (float)(amplifier + 1)));
			SurviveEntityStats.setTemperatureStats((ServerPlayerEntity) entityLivingBaseIn, stats);
		} else if (this == SEffects.HEATED && entityLivingBaseIn instanceof ServerPlayerEntity) {
			TemperatureStats stats = SurviveEntityStats.getTemperatureStats((ServerPlayerEntity) entityLivingBaseIn);
			//			stats.getOrCreateModifier(new ResourceLocation("survive:heated_effect")).setMod(+(0.05F * (float)(amplifier + 1)));
			TemperatureStats.setTemperatureModifier(entityLivingBaseIn, "survive:heated_effect", +(0.05F * (float)(amplifier + 1)));
			SurviveEntityStats.setTemperatureStats((ServerPlayerEntity) entityLivingBaseIn, stats);
		} else if (this == SEffects.ENERGIZED && entityLivingBaseIn instanceof ServerPlayerEntity) {
			EnergyStats energyStats = SurviveEntityStats.getEnergyStats(entityLivingBaseIn);
			if (entityLivingBaseIn.ticksExisted % 20 == 0) {
				energyStats.addStats(1);
				SurviveEntityStats.setEnergyStats(entityLivingBaseIn, energyStats);
			}
			if (entityLivingBaseIn.isPotionActive(SEffects.TIREDNESS)) {
				entityLivingBaseIn.removePotionEffect(SEffects.TIREDNESS);
			}
		} else if (this == SEffects.TIREDNESS && entityLivingBaseIn instanceof ServerPlayerEntity) {
			EnergyStats energyStats = SurviveEntityStats.getEnergyStats(entityLivingBaseIn);
			energyStats.addExhaustion((PlayerEntity) entityLivingBaseIn, (0.005F * (float)(amplifier + 1)));
			SurviveEntityStats.setEnergyStats(entityLivingBaseIn, energyStats);
		}
		super.performEffect(entityLivingBaseIn, amplifier);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		if (this == SEffects.HYPOTHERMIA) {
			int k = 40 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SEffects.HYPERTHERMIA) {
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
			int k = 80 >> amplifier;
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
