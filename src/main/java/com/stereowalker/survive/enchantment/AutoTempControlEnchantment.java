package com.stereowalker.survive.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class AutoTempControlEnchantment extends Enchantment {

	public AutoTempControlEnchantment(Rarity rarityIn, EquipmentSlotType[] slots) {
		super(rarityIn, EnchantmentType.ARMOR, slots);
	}
	
	/**
	 * Returns the minimal value of enchantability needed on the enchantment level passed.
	 */
	public int getMinEnchantability(int enchantmentLevel) {
		return enchantmentLevel * 25;
	}

	public int getMaxEnchantability(int enchantmentLevel) {
		return this.getMinEnchantability(enchantmentLevel) + 50;
	}

	public boolean isTreasureEnchantment() {
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
	protected boolean canApplyTogether(Enchantment ench) 
	{
		if (this == SEnchantments.ADJUSTED_COOLING) {
			return super.canApplyTogether(ench) && ench != SEnchantments.WARMING && ench != SEnchantments.ADJUSTED_WARMING && ench != SEnchantments.COOLING;
		} else {
			return super.canApplyTogether(ench) && ench != SEnchantments.WARMING && ench != SEnchantments.ADJUSTED_COOLING && ench != SEnchantments.COOLING;
		}
	}
}
