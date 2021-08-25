package com.stereowalker.survive.potion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class UnwellEffect extends Effect{

	public UnwellEffect(EffectType effectType, int liquidColorIn) {
		super(effectType, liquidColorIn);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		return ret;
	}
}
