package com.stereowalker.survive.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class HeatedAndChilledMobEffect extends MobEffect {

	public HeatedAndChilledMobEffect(MobEffectCategory effectType, int liquidColorIn) {
		super(effectType, liquidColorIn);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int k = 60 >> amplifier;
		if (k > 0) {
			return duration % k == 0;
		} else {
			return true;
		}
	}
}
