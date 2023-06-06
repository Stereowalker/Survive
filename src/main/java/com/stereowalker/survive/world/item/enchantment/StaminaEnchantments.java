package com.stereowalker.survive.world.item.enchantment;

import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
@RegistryHolder(registry = Enchantment.class)
public class StaminaEnchantments {
	@RegistryObject("featherweight")
	public static final Enchantment FEATHERWEIGHT = new FeatherweightEnchantment(Rarity.UNCOMMON, new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
	@RegistryObject("weightless")
	public static final Enchantment WEIGHTLESS = new WeightlessEnchantment(Rarity.VERY_RARE, new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
}
