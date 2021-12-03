package com.stereowalker.survive.world.effect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.effect.AttackDamageMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

public class UnwellAttackDamageMobEffect extends AttackDamageMobEffect {

	protected UnwellAttackDamageMobEffect(MobEffectCategory p_19426_, int p_19427_, double p_19428_) {
		super(p_19426_, p_19427_, p_19428_);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		return ret;
	}

}
