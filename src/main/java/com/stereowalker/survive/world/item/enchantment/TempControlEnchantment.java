package com.stereowalker.survive.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TempControlEnchantment extends Enchantment {
	
	public TempControlEnchantment(Rarity rarityIn, EquipmentSlot[] slots) {
		super(rarityIn, EnchantmentCategory.ARMOR, slots);
	}
	
	@Override
	protected boolean checkCompatibility(Enchantment ench) 
	{
		if (this == TemperatureEnchantments.COOLING) {
			return super.checkCompatibility(ench) && !(ench instanceof AutoTempControlEnchantment) && ench != TemperatureEnchantments.WARMING;
		} else {
			return super.checkCompatibility(ench) && !(ench instanceof AutoTempControlEnchantment) && ench != TemperatureEnchantments.COOLING;
		}
	}
	
	//1.20.1
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
}
