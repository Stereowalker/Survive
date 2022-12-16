package com.stereowalker.survive.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TempControlEnchantment extends Enchantment {
	
	public TempControlEnchantment(Rarity rarityIn, EquipmentSlot[] slots) {
		super(rarityIn, EnchantmentCategory.ARMOR, slots);
	}
	
	@Override
	public int getMinCost(int enchantmentLevel) 
	{
		return 6 * enchantmentLevel + 5;
	}
	
	@Override
	public int getMaxCost(int enchantmentLevel)
	{
		return this.getMinCost(enchantmentLevel) + 20;
	}
	
	@Override
	public int getMaxLevel()
	{
		return 7;
	}
	
	@Override
	protected boolean checkCompatibility(Enchantment ench) 
	{
		if (this == SEnchantments.COOLING) {
			return super.checkCompatibility(ench) && !(ench instanceof AutoTempControlEnchantment) && ench != SEnchantments.WARMING;
		} else {
			return super.checkCompatibility(ench) && !(ench instanceof AutoTempControlEnchantment) && ench != SEnchantments.COOLING;
		}
	}
}
