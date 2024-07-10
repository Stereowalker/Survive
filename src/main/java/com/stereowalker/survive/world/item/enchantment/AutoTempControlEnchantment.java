package com.stereowalker.survive.world.item.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;

public class AutoTempControlEnchantment extends Enchantment {

	public AutoTempControlEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
		super(pDefinition);
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}
	
	@Override
	protected boolean checkCompatibility(Enchantment ench) 
	{
		if (this == TemperatureEnchantments.ADJUSTED_COOLING) {
			return super.checkCompatibility(ench) && ench != TemperatureEnchantments.WARMING && ench != TemperatureEnchantments.ADJUSTED_WARMING && ench != TemperatureEnchantments.COOLING;
		} else {
			return super.checkCompatibility(ench) && ench != TemperatureEnchantments.WARMING && ench != TemperatureEnchantments.ADJUSTED_COOLING && ench != TemperatureEnchantments.COOLING;
		}
	}
}
