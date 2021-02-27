package com.stereowalker.survive.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class TempControlEnchantment extends Enchantment {
	
	public TempControlEnchantment(Rarity rarityIn, EquipmentSlotType[] slots) {
		super(rarityIn, EnchantmentType.ARMOR, slots);
	}
	
	@Override
	public int getMinEnchantability(int enchantmentLevel) 
	{
		return 6 * enchantmentLevel + 5;
	}
	
	@Override
	public int getMaxEnchantability(int enchantmentLevel)
	{
		return this.getMinEnchantability(enchantmentLevel) + 20;
	}
	
	@Override
	public int getMaxLevel()
	{
		return 7;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench) 
	{
		if (this == SEnchantments.COOLING) {
			return super.canApplyTogether(ench) && !(ench instanceof AutoTempControlEnchantment) && ench != SEnchantments.WARMING;
		} else {
			return super.canApplyTogether(ench) && !(ench instanceof AutoTempControlEnchantment) && ench != SEnchantments.COOLING;
		}
	}
}
