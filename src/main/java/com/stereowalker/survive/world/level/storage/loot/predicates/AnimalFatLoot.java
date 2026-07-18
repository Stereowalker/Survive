package com.stereowalker.survive.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stereowalker.survive.config.ServerConfig;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * A LootItemCondition that checks whether it currently raining or trhundering.
 * Both checks are optional.
 */
public class AnimalFatLoot implements LootItemCondition {
	
	public static final MapCodec<AnimalFatLoot> CODEC = RecordCodecBuilder.mapCodec(
	        p_297208_ -> p_297208_.group(
	        			Codec.INT.fieldOf("value").forGetter((a)->0)
	                )
	                .apply(p_297208_, AnimalFatLoot::new)
	    );

	AnimalFatLoot(int x) {
	}

    @Override
    public MapCodec<AnimalFatLoot> codec() {
        return CODEC;
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
			return new AnimalFatLoot(0);
		}
	}
}