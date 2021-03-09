package com.stereowalker.survive.enchantment;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.registries.IForgeRegistry;

public class SEnchantments {
	public static List<Enchantment> ENCHANTMENTS = new ArrayList<Enchantment>();
	
	public static final Enchantment WARMING = registerTemp("warming", new TempControlEnchantment(Rarity.UNCOMMON, new EquipmentSlotType[] {EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET}));
	public static final Enchantment COOLING = registerTemp("cooling", new TempControlEnchantment(Rarity.UNCOMMON, new EquipmentSlotType[] {EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET}));
	public static final Enchantment ADJUSTED_WARMING = registerTemp("adjusted_warming", new AutoTempControlEnchantment(Rarity.VERY_RARE, new EquipmentSlotType[] {EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET}));
	public static final Enchantment ADJUSTED_COOLING = registerTemp("adjusted_cooling", new AutoTempControlEnchantment(Rarity.VERY_RARE, new EquipmentSlotType[] {EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET}));
	
	public static Enchantment registerTemp(String name, Enchantment enchantment) {
		if (Config.enable_temperature) {
			return register(name, enchantment);
		} else {
			return enchantment;
		}
	}
	
	public static Enchantment register(String name, Enchantment enchantment) {
		enchantment.setRegistryName(Survive.getInstance().location(name));
		ENCHANTMENTS.add(enchantment);
		return enchantment;
	}
	
	public static void registerAll(IForgeRegistry<Enchantment> registry) {
		for(Enchantment enchantment : ENCHANTMENTS) {
			registry.register(enchantment);
			Survive.getInstance().debug("Enchantment: \""+enchantment.getRegistryName().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Enchantments Registered");
	}
}
