package com.stereowalker.survive.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SLootItemConditions {
	public static final MapCodec<? extends LootItemCondition> ANIMAL_FAT_CONFIG = register("animal_fat_config", AnimalFatLoot.CODEC);

	private static MapCodec<? extends LootItemCondition> register(String pRegistryName, MapCodec<? extends LootItemCondition> pSerializer) {
		return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, VersionHelper.toLoc(Survive.MOD_ID, pRegistryName), pSerializer);
	}
}
