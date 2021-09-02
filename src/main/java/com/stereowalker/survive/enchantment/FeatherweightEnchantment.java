package com.stereowalker.survive.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class FeatherweightEnchantment extends Enchantment {
	
	public FeatherweightEnchantment(Rarity rarityIn, EquipmentSlotType[] slots) {
		super(rarityIn, EnchantmentType.ARMOR, slots);
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel) 
	{
		return 5 * enchantmentLevel + 6;
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return this.getMinEnchantability(enchantmentLevel) + 20;
	}
	
	@Override
	public int getMaxLevel()
	{
		return 5;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench) 
	{
		return ench instanceof WeightlessEnchantment ? false : super.canApplyTogether(ench);
	}
}
