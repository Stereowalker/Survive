package com.stereowalker.survive.world.item;

import java.util.EnumMap;
import java.util.function.Supplier;

import com.stereowalker.survive.Survive;

import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public enum SArmorMaterial implements ArmorMaterial
{
	WOOL("wool", 1, Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
		p_266652_.put(ArmorItem.Type.BOOTS, 1);
		p_266652_.put(ArmorItem.Type.LEGGINGS, 1);
		p_266652_.put(ArmorItem.Type.CHESTPLATE, 1);
		p_266652_.put(ArmorItem.Type.HELMET, 1);
	}), 1, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
		return Ingredient.of(Items.WHITE_WOOL);
	}),
	STIFFENED_HONEY("stiffened_honey", 1, Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266652_) -> {
		p_266652_.put(ArmorItem.Type.BOOTS, 1);
		p_266652_.put(ArmorItem.Type.LEGGINGS, 1);
		p_266652_.put(ArmorItem.Type.CHESTPLATE, 1);
		p_266652_.put(ArmorItem.Type.HELMET, 1);
	}), 1, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
		return Ingredient.of(Items.WHITE_WOOL);
	});


	public static final StringRepresentable.EnumCodec<ArmorMaterials> f_265935_ = StringRepresentable.fromEnum(ArmorMaterials::values);
	private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266653_) -> {
		p_266653_.put(ArmorItem.Type.BOOTS, 13);
		p_266653_.put(ArmorItem.Type.LEGGINGS, 15);
		p_266653_.put(ArmorItem.Type.CHESTPLATE, 16);
		p_266653_.put(ArmorItem.Type.HELMET, 11);
	});
	private final String name;
	private final int durabilityMultiplier;
	private final EnumMap<ArmorItem.Type, Integer> f_265966_;
	private final int enchantmentValue;
	private final SoundEvent sound;
	private final float toughness;
	private final float knockbackResistance;
	private final LazyLoadedValue<Ingredient> repairIngredient;

	private SArmorMaterial(String p_268171_, int p_268303_, EnumMap<ArmorItem.Type, Integer> p_267941_, int p_268086_, SoundEvent p_268145_, float p_268058_, float p_268180_, Supplier<Ingredient> p_268256_) {
		this.name = p_268171_;
		this.durabilityMultiplier = p_268303_;
		this.f_265966_ = p_267941_;
		this.enchantmentValue = p_268086_;
		this.sound = p_268145_;
		this.toughness = p_268058_;
		this.knockbackResistance = p_268180_;
		this.repairIngredient = new LazyLoadedValue<>(p_268256_);
	}

	@Override
	public int getDurabilityForType(ArmorItem.Type p_266745_) {
		return HEALTH_FUNCTION_FOR_TYPE.get(p_266745_) * this.durabilityMultiplier;
	}

	@Override
	public int getDefenseForType(ArmorItem.Type p_266752_) {
		return this.f_265966_.get(p_266752_);
	}

	@Override
	public int getEnchantmentValue() 
	{
		return this.enchantmentValue;
	}

	@Override
	public String getName() 
	{
		return Survive.MOD_ID + ":" + this.name;
	}

	@Override
	public Ingredient getRepairIngredient() 
	{
		return this.repairIngredient.get();
	}

	@Override
	public SoundEvent getEquipSound() 
	{
		return this.sound;
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
