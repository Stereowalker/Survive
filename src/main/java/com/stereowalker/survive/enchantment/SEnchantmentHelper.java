package com.stereowalker.survive.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class SEnchantmentHelper extends EnchantmentHelper {

	public static int getCoolingModifier(ItemStack stack) {
		return getEnchantmentLevel(SEnchantments.COOLING, stack);
	}

	public static int getWarmingModifier(ItemStack stack) {
		return getEnchantmentLevel(SEnchantments.WARMING, stack);
	}
	
	public static boolean hasAdjustedCooling(ItemStack stack) {
		return getEnchantmentLevel(SEnchantments.ADJUSTED_COOLING, stack) > 0;
	}

	public static boolean hasAdjustedWarming(ItemStack stack) {
		return getEnchantmentLevel(SEnchantments.ADJUSTED_WARMING, stack) > 0;
	}
}
