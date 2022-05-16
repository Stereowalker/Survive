package com.stereowalker.survive.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.stereowalker.survive.config.ServerConfig;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * A LootItemCondition that checks whether it currently raining or trhundering.
 * Both checks are optional.
 */
public class AnimalFatLoot implements LootItemCondition {

	AnimalFatLoot() {
	}

	public LootItemConditionType getType() {
		return SLootItemConditions.ANIMAL_FAT_CONFIG;
	}

	public boolean test(LootContext p_82066_) {
		ServerLevel serverlevel = p_82066_.getLevel();
		if (serverlevel != null) {
			return ServerConfig.animalFatDrops;
		} else return false;
	}

	public static AnimalFatLoot.Builder animalFatConfig() {
		return new AnimalFatLoot.Builder();
	}

	public static class Builder implements LootItemCondition.Builder {
		public AnimalFatLoot build() {
			return new AnimalFatLoot();
		}
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<AnimalFatLoot> {
		/**
		 * Serialize the value by putting its data into the JsonObject.
		 */
		public void serialize(JsonObject p_82079_, AnimalFatLoot p_82080_, JsonSerializationContext p_82081_) {
		}

		/**
		 * Deserialize a value by reading it from the JsonObject.
		 */
		public AnimalFatLoot deserialize(JsonObject p_82087_, JsonDeserializationContext p_82088_) {
			return new AnimalFatLoot();
		}
	}
}