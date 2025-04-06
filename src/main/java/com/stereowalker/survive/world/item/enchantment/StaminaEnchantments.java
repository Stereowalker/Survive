package com.stereowalker.survive.world.item.enchantment;

import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
@RegistryHolder(registry = Enchantment.class)
public class StaminaEnchantments {
	@RegistryObject("featherweight")
	public static final Enchantment FEATHERWEIGHT = new FeatherweightEnchantment(Enchantment.definition(ItemTags.ARMOR_ENCHANTABLE, 5, 5, Enchantment.dynamicCost(6, 5), Enchantment.dynamicCost(26, 5), 1, new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));
	@RegistryObject("weightless")
	public static final Enchantment WEIGHTLESS = new WeightlessEnchantment(Enchantment.definition(ItemTags.ARMOR_ENCHANTABLE, 5, 1, Enchantment.dynamicCost(6, 5), Enchantment.dynamicCost(26, 5), 1, new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));
}
