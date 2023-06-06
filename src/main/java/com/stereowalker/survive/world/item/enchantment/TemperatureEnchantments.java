package com.stereowalker.survive.world.item.enchantment;

import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;

@RegistryHolder(registry = Enchantment.class)
public class TemperatureEnchantments {
	@RegistryObject("warming")
	public static final Enchantment WARMING = new TempControlEnchantment(Rarity.UNCOMMON, new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
	@RegistryObject("cooling")
	public static final Enchantment COOLING = new TempControlEnchantment(Rarity.UNCOMMON, new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
	@RegistryObject("adjusted_warming")
	public static final Enchantment ADJUSTED_WARMING = new AutoTempControlEnchantment(Rarity.VERY_RARE, new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
	@RegistryObject("adjusted_cooling")
	public static final Enchantment ADJUSTED_COOLING = new AutoTempControlEnchantment(Rarity.VERY_RARE, new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
}
