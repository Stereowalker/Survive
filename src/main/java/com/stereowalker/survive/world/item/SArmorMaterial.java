package com.stereowalker.survive.world.item;

import com.stereowalker.survive.Survive;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public enum SArmorMaterial implements ArmorMaterial
{
	WOOL("wool", 1, new int[]{1, 1, 1, 1}, 1, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0.0F, Items.WHITE_WOOL),
	STIFFENED_HONEY("stiffened_honey", 1, new int[]{1, 1, 1, 1}, 1, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0.0F, SItems.STIFFENED_HONEY);
	
	private static final int[] max_damage_array = new int[] {13, 15, 16, 11};
	private String name;
	private SoundEvent equipSound;
	private int durability, enchantability;
	private Item repairItem;
	private int[] damageReductionAmounts;
	private float toughness;
	private float knockbackResistance;
	
	private SArmorMaterial(String name, int durability, int[] damageReductionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Item repairItem) 
	{
		this.damageReductionAmounts = damageReductionAmounts;
		this.durability = durability;
		this.enchantability = enchantability;
		this.equipSound = equipSound;
		this.name = name;
		this.repairItem = repairItem;
		this.knockbackResistance = knockbackResistance;
		this.toughness = toughness;
	}

	@Override
	public int getDefenseForSlot(EquipmentSlot slot) 
	{
		return this.damageReductionAmounts[slot.getIndex()];
	}

	@Override
	public int getDurabilityForSlot(EquipmentSlot slot) 
	{
		return max_damage_array[slot.getIndex()] * this.durability;
	}

	@Override
	public int getEnchantmentValue() 
	{
		return this.enchantability;
	}

	@Override
	public String getName() 
	{
		return Survive.MOD_ID + ":" + this.name;
	}

	@Override
	public Ingredient getRepairIngredient() 
	{
		return Ingredient.of(this.repairItem);
	}

	@Override
	public SoundEvent getEquipSound() 
	{
		return this.equipSound;
	}

	@Override
	public float getToughness() 
	{
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return knockbackResistance;
	}

}
