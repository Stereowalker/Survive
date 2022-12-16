package com.stereowalker.survive.world.level.storage.loot.predicates;

import com.stereowalker.survive.Survive;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class SLootItemConditions {
	public static final LootItemConditionType ANIMAL_FAT_CONFIG = register("animal_fat_config", new AnimalFatLoot.Serializer());

	private static LootItemConditionType register(String pRegistryName, Serializer<? extends LootItemCondition> pSerializer) {
		return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(Survive.MOD_ID, pRegistryName), new LootItemConditionType(pSerializer));
	}
}
