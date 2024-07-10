package com.stereowalker.survive.world.item.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;

public class WeightlessEnchantment extends Enchantment {
	
	public WeightlessEnchantment(Enchantment.EnchantmentDefinition pDefinition) {
		super(pDefinition);
	}
	
	@Override
	public boolean isTreasureOnly() {
		return true;
	}
	
	@Override
	protected boolean checkCompatibility(Enchantment ench) 
	{
		return ench instanceof FeatherweightEnchantment ? false : super.checkCompatibility(ench);
	}
}
