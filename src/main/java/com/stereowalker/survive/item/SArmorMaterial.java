package com.stereowalker.survive.item;

import com.stereowalker.survive.Survive;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public enum SArmorMaterial implements IArmorMaterial
{
	WOOL("wool", 1, new int[]{1, 1, 1, 1}, 1, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0, 0.0F, Items.WHITE_WOOL),
	STIFFENED_HONEY("stiffened_honey", 1, new int[]{1, 1, 1, 1}, 1, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0, 0.0F, SItems.STIFFENED_HONEY);
	
	private static final int[] max_damage_array = new int[] {13, 15, 16, 11};
	private String name;
	private SoundEvent equipSound;
	private int durability, enchantability;
	private Item repairItem;
	private int[] damageReductionAmounts;
	private float toughness;
	
	private SArmorMaterial(String name, int durability, int[] damageReductionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Item repairItem) 
	{
		this.damageReductionAmounts = damageReductionAmounts;
		this.durability = durability;
		this.enchantability = enchantability;
		this.equipSound = equipSound;
		this.name = name;
		this.repairItem = repairItem;
		this.toughness = toughness;
	}

	@Override
	public int getDamageReductionAmount(EquipmentSlotType slot) 
	{
		return this.damageReductionAmounts[slot.getIndex()];
	}

	@Override
	public int getDurability(EquipmentSlotType slot) 
	{
		return max_damage_array[slot.getIndex()] * this.durability;
	}

	@Override
	public int getEnchantability() 
	{
		return this.enchantability;
	}

	@Override
	public String getName() 
	{
		return Survive.MOD_ID + ":" + this.name;
	}

	@Override
	public Ingredient getRepairMaterial() 
	{
		return Ingredient.fromItems(this.repairItem);
	}

	@Override
	public SoundEvent getSoundEvent() 
	{
		return this.equipSound;
	}

	@Override
	public float getToughness() 
	{
		return this.toughness;
	}

}
