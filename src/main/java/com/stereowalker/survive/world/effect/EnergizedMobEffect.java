package com.stereowalker.survive.world.effect;

import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class EnergizedMobEffect extends MobEffect {

	protected EnergizedMobEffect(MobEffectCategory pCategory, int pColor) {
		super(pCategory, pColor);
	}

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (living instanceof Player player) {
        	IRealisticEntity realisticEntity = (IRealisticEntity)player;
        	StaminaData energyStats = SurviveEntityStats.getEnergyStats(player);
			energyStats.relax(1, player.getAttributeValue(SAttributes.MAX_STAMINA.holder()));
			energyStats.save(player);
			if (player.hasEffect(SMobEffects.TIREDNESS.holder())) {
				player.removeEffect(SMobEffects.TIREDNESS.holder());
			}
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    	int k = 60 >> amplifier;
		if (k > 0) {
			return duration % k == 0;
		} else {
			return true;
		}
    }

}
