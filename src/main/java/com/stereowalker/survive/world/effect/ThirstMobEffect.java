package com.stereowalker.survive.world.effect;

import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ThirstMobEffect extends MobEffect {

	protected ThirstMobEffect(MobEffectCategory pCategory, int pColor) {
		super(pCategory, pColor);
	}

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        if (living instanceof Player player) {
        	IRealisticEntity realisticEntity = (IRealisticEntity)player;
        	realisticEntity.waterData().addExhaustion(player, (0.005F * (float)(amplifier + 1)));
        }

    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

}
