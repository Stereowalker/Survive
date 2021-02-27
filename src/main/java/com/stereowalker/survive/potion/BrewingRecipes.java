package com.stereowalker.survive.potion;

import com.stereowalker.survive.item.SItems;

import net.minecraft.item.Items;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.Potions;

public class BrewingRecipes {
	public static void addBrewingRecipes() {
    	PotionBrewing.addMix(Potions.AWKWARD, SItems.ICE_CUBE, SPotions.COLD_RESISTANCE);
    	PotionBrewing.addMix(SPotions.COLD_RESISTANCE, Items.REDSTONE, SPotions.LONG_COLD_RESISTANCE);
    	PotionBrewing.addMix(SPotions.COLD_RESISTANCE, Items.GLOWSTONE_DUST, SPotions.STRONG_COLD_RESISTANCE);
    	
    	PotionBrewing.addMix(Potions.AWKWARD, SItems.MAGMA_PASTE, SPotions.HEAT_RESISTANCE);
    	PotionBrewing.addMix(SPotions.HEAT_RESISTANCE, Items.REDSTONE, SPotions.LONG_HEAT_RESISTANCE);
    	PotionBrewing.addMix(SPotions.HEAT_RESISTANCE, Items.GLOWSTONE_DUST, SPotions.STRONG_HEAT_RESISTANCE);
	}
}
