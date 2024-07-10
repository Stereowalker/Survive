package com.stereowalker.survive.world.item.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;

public class FeatherweightEnchantment extends Enchantment {
	
	public FeatherweightEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
		super(pDefinition);
	}
	
	@Override
	protected boolean checkCompatibility(Enchantment ench) 
	{
		return ench instanceof WeightlessEnchantment ? false : super.checkCompatibility(ench);
	}
}
