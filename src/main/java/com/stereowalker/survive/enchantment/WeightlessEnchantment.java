package com.stereowalker.survive.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class WeightlessEnchantment extends Enchantment {
	
	public WeightlessEnchantment(Rarity rarityIn, EquipmentSlotType[] slots) {
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
	public boolean isTreasureEnchantment() {
		return true;
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench) 
	{
		return ench instanceof FeatherweightEnchantment ? false : super.canApplyTogether(ench);
	}
}
