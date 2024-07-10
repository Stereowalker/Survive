package com.stereowalker.survive.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.stereowalker.survive.Survive;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class SLootItemConditions {
	public static final LootItemConditionType ANIMAL_FAT_CONFIG = register("animal_fat_config", AnimalFatLoot.CODEC);

	private static LootItemConditionType register(String pRegistryName, MapCodec<? extends LootItemCondition> pSerializer) {
		return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation(Survive.MOD_ID, pRegistryName), new LootItemConditionType(pSerializer));
	}
}
