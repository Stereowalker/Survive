package com.stereowalker.survive.potion;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.StaminaStats;
import com.stereowalker.survive.util.SDamageSource;
import com.stereowalker.survive.util.TemperatureStats;
import com.stereowalker.survive.util.WaterStats;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.world.Difficulty;

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
