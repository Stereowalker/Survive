package com.stereowalker.survive.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class AutoTempControlEnchantment extends Enchantment {

	public AutoTempControlEnchantment(Rarity rarityIn, EquipmentSlot[] slots) {
		super(rarityIn, EnchantmentCategory.ARMOR, slots);
	}
	
	/**
	 * Returns the minimal value of enchantability needed on the enchantment level passed.
	 */
	@Override
	public int getMinCost(int enchantmentLevel) {
		return enchantmentLevel * 25;
	}

	@Override
	public int getMaxCost(int enchantmentLevel) {
		return this.getMinCost(enchantmentLevel) + 50;
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	/**
	 * Returns the maximum level that the enchantment can have.
	 */
	@Override
	public int getMaxLevel() {
		return 1;
	}
	
	@Override
	protected boolean checkCompatibility(Enchantment ench) 
	{
		if (this == SEnchantments.ADJUSTED_COOLING) {
			return super.checkCompatibility(ench) && ench != SEnchantments.WARMING && ench != SEnchantments.ADJUSTED_WARMING && ench != SEnchantments.COOLING;
		} else {
			return super.checkCompatibility(ench) && ench != SEnchantments.WARMING && ench != SEnchantments.ADJUSTED_COOLING && ench != SEnchantments.COOLING;
		}
	}
}
