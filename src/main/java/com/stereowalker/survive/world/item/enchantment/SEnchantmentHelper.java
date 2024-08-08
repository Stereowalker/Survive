package com.stereowalker.survive.world.item.enchantment;

import org.apache.commons.lang3.mutable.MutableInt;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class SEnchantmentHelper extends EnchantmentHelper {

	public static int getCoolingModifier(ItemStack stack) {
		MutableInt mutablefloat = new MutableInt();
		ItemEnchantments itemenchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

		has(stack, null);
        for (Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
        	if (entry.getKey().value().effects().has(SEnchantmentEffectComponents.COOLING))
        		mutablefloat.add(entry.getIntValue());
        }
        return mutablefloat.intValue();
	}

	public static int getWarmingModifier(ItemStack stack) {
		MutableInt mutablefloat = new MutableInt();
		ItemEnchantments itemenchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

		has(stack, null);
        for (Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
        	if (entry.getKey().value().effects().has(SEnchantmentEffectComponents.WARMING))
        		mutablefloat.add(entry.getIntValue());
        }
        return mutablefloat.intValue();
	}

	public static int getFeatherweightModifier(ItemStack stack) {
		MutableInt mutablefloat = new MutableInt();
		ItemEnchantments itemenchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

		has(stack, null);
        for (Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
        	if (entry.getKey().value().effects().has(SEnchantmentEffectComponents.FEATHERS))
        		mutablefloat.add(entry.getIntValue());
        }
        return mutablefloat.intValue();
	}
	
	public static boolean hasAdjustedCooling(ItemStack stack) {
		return has(stack, SEnchantmentEffectComponents.AUTO_COOLING);
	}

	public static boolean hasAdjustedWarming(ItemStack stack) {
		return has(stack, SEnchantmentEffectComponents.AUTO_WARMING);
	}

	public static boolean hasWeightless(ItemStack stack) {
		return has(stack, SEnchantmentEffectComponents.WEIGHTLESS);
	}
}
