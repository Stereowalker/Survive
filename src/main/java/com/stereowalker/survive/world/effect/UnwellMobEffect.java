package com.stereowalker.survive.world.effect;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.needs.SDamageSource;

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
		if (this == SMobEffects.HYPOTHERMIA && entityLivingBaseIn instanceof Player) {
			if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth()/4.0F) {
				entityLivingBaseIn.hurt(SDamageSource.HYPOTHERMIA, 1.0F);
	         }
		} else if (this == SMobEffects.HYPERTHERMIA && entityLivingBaseIn instanceof Player) {
			if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth()/4.0F) {
				entityLivingBaseIn.hurt(SDamageSource.HYPERTHERMIA, 1.0F);
	         }
		}
		super.applyEffectTick(entityLivingBaseIn, amplifier);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		if (this == SMobEffects.HYPOTHERMIA) {
			int k = 120 >> amplifier;
			if (k > 0) {
				return duration % k == 0;
			} else {
				return true;
			}
		} else if (this == SMobEffects.HYPERTHERMIA) {
			int k = 120 >> amplifier;
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
