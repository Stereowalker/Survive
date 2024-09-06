package com.stereowalker.survive.world.item;

import java.util.EnumMap;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.core.registries.Housing;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.Util;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SArmorMaterials
{
	@RegistryObject("wool")
	public static final Housing<ArmorMaterial> WOOL = Housing.create(() -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), p_327096_ -> {
        p_327096_.put(ArmorItem.Type.BOOTS, 1);
        p_327096_.put(ArmorItem.Type.LEGGINGS, 1);
        p_327096_.put(ArmorItem.Type.CHESTPLATE, 1);
        p_327096_.put(ArmorItem.Type.HELMET, 1);
        p_327096_.put(ArmorItem.Type.BODY, 1);
    }), 9, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(Items.WHITE_WOOL), 
			List.of(new ArmorMaterial.Layer(VersionHelper.toLoc(Survive.MOD_ID, "wool"))), 0, 0));
	
	@RegistryObject("stiffened_honey")
	public static final Housing<ArmorMaterial> STIFFENED_HONEY = Housing.create(() -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), p_327096_ -> {
        p_327096_.put(ArmorItem.Type.BOOTS, 1);
        p_327096_.put(ArmorItem.Type.LEGGINGS, 1);
        p_327096_.put(ArmorItem.Type.CHESTPLATE, 1);
        p_327096_.put(ArmorItem.Type.HELMET, 1);
        p_327096_.put(ArmorItem.Type.BODY, 1);
    }), 9, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(SItems.STIFFENED_HONEY), 
			List.of(new ArmorMaterial.Layer(VersionHelper.toLoc(Survive.MOD_ID, "stiffened_honey"))), 0, 0));
}
