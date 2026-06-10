package com.stereowalker.survive.world.item;

import java.util.Map;

import com.google.common.collect.Maps;
import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.util.VersionHelper;
import com.stereowalker.unionlib.world.item.equipment.ArmorMatReference;
import com.stereowalker.unionlib.world.item.equipment.ArmorMatReference.ArmorSegment;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;

public class SArmorMaterials
{
	public static final ArmorMatReference WOOL = new ArmorMatReference(VersionHelper.toLoc(Survive.MOD_ID, "wool"), 9, Maps.newEnumMap( 
            Map.of(ArmorSegment.HEAD, 1, ArmorSegment.CHEST, 1, ArmorSegment.LEGS, 1, ArmorSegment.FEET, 1)
        ), 9, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, TagKey.create(Registries.ITEM, null));
	
	public static final ArmorMatReference STIFFENED_HONEY = new ArmorMatReference(VersionHelper.toLoc(Survive.MOD_ID, "stiffened_honey"), 9, Maps.newEnumMap( 
            Map.of(ArmorSegment.HEAD, 1, ArmorSegment.CHEST, 1, ArmorSegment.LEGS, 1, ArmorSegment.FEET, 1)
        ), 9, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, TagKey.create(Registries.ITEM, null));
}
