package com.stereowalker.survive.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FeatherweightEnchantment extends Enchantment {
	
	public FeatherweightEnchantment(Rarity rarityIn, EquipmentSlot[] slots) {
		super(rarityIn, EnchantmentCategory.ARMOR, slots);
	}
	
	@Override
	public int getMinCost(int enchantmentLevel) 
	{
		return 5 * enchantmentLevel + 6;
	}
	
	@Override
	public int getMaxCost(int enchantmentLevel)
	{
		return this.getMinCost(enchantmentLevel) + 20;
	}
	
	@Override
	public int getMaxLevel()
	{
		return 5;
	}
	
	@Override
	protected boolean checkCompatibility(Enchantment ench) 
	{
		return ench instanceof WeightlessEnchantment ? false : super.checkCompatibility(ench);
	}
}
