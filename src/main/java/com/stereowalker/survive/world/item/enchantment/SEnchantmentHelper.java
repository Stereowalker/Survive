package com.stereowalker.survive.world.item.enchantment;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class SEnchantmentHelper extends EnchantmentHelper {

	public static int getCoolingModifier(ItemStack stack) {
		return getItemEnchantmentLevel(SEnchantments.COOLING, stack);
	}

	public static int getWarmingModifier(ItemStack stack) {
		return getItemEnchantmentLevel(SEnchantments.WARMING, stack);
	}

	public static int getFeatherweightModifier(ItemStack stack) {
		return getItemEnchantmentLevel(SEnchantments.FEATHERWEIGHT, stack);
	}
	
	public static boolean hasAdjustedCooling(ItemStack stack) {
		return getItemEnchantmentLevel(SEnchantments.ADJUSTED_COOLING, stack) > 0;
	}

	public static boolean hasAdjustedWarming(ItemStack stack) {
		return getItemEnchantmentLevel(SEnchantments.ADJUSTED_WARMING, stack) > 0;
	}

	public static boolean hasWeightless(ItemStack stack) {
		return getItemEnchantmentLevel(SEnchantments.WEIGHTLESS, stack) > 0;
	}
}
