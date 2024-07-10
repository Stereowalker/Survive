package com.stereowalker.survive.world.item.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;

public class TempControlEnchantment extends Enchantment {
	
	public TempControlEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
		super(pDefinition);
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
}
