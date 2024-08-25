package com.stereowalker.survive.world.effect;

import com.stereowalker.survive.damagesource.SDamageSources;
import com.stereowalker.survive.damagesource.SDamageTypes;
import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HyperthermiaMobEffect extends UnwellMobEffect {

	public HyperthermiaMobEffect(MobEffectCategory effectType, int liquidColorIn) {
		super(effectType, liquidColorIn);
	}

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
    	if (living instanceof Player) {
    		if (living.getHealth() > living.getMaxHealth()/3.5F)
    			living.hurt(SDamageSources.source(living.level().registryAccess(), SDamageTypes.HYPERTHERMIA), 0.8F);
    		if ((float)((IRealisticEntity)living).staminaData().getLTS() > ((float)((IRealisticEntity)living).staminaData().getLTS())*0.3)
    			((IRealisticEntity)living).addStaminaExhaustion((1.0F * (float)(amplifier + 1)), "Hyperthermia effect", false);
    	}
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    	int k = 160 >> amplifier;
		if (k > 0) {
			return duration % k == 0;
		} else {
			return true;
		}
    }

}
