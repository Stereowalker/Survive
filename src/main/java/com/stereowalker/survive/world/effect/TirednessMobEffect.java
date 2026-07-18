package com.stereowalker.survive.world.effect;

import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class TirednessMobEffect extends MobEffect {

	protected TirednessMobEffect(MobEffectCategory pCategory, int pColor) {
		super(pCategory, pColor);
	}

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity living, int amplifier) {
        if (living instanceof Player player) {
        	IRealisticEntity realisticEntity = (IRealisticEntity)player;
			realisticEntity.addStaminaExhaustion((0.0125F * (float)(amplifier + 1)), "Tiredness effect", false);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    	int k = 80 >> amplifier;
		if (k > 0) {
			return duration % k == 0;
		} else {
			return true;
		}
    }

}
