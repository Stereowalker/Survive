package com.stereowalker.survive.world.effect;

import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.damagesource.SDamageSources;
import com.stereowalker.survive.damagesource.SDamageTypes;
import com.stereowalker.survive.needs.StaminaData;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HypothermiaMobEffect extends UnwellMobEffect {

	public HypothermiaMobEffect(MobEffectCategory effectType, int liquidColorIn) {
		super(effectType, liquidColorIn);
	}

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
		StaminaData energyStats = SurviveEntityStats.getEnergyStats(living);
    	if (living.getHealth() > living.getMaxHealth()/3.5F)
    		living.hurt(SDamageSources.source(living.level().registryAccess(), SDamageTypes.HYPOTHERMIA), 0.8F);
		if ((float)energyStats.getEnergyLevel() > ((float)energyStats.getEnergyLevel())*0.3)
			energyStats.addExhaustion((Player) living, (1.0F * (float)(amplifier + 1)), "Hypothermia effect");
		energyStats.save(living);
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
