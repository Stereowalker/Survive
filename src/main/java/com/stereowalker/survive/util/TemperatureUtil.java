package com.stereowalker.survive.util;

import com.google.common.collect.ImmutableList;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.entity.ai.SAttributes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.PerlinNoiseGenerator;

public class TemperatureUtil {
	public static double firstHeat(PlayerEntity player) {
		return Survive.DEFAULT_TEMP + (player.getAttributeValue(SAttributes.HEAT_RESISTANCE) * 1.0D);
	}

	public static double secondHeat(PlayerEntity player) {
		return Survive.DEFAULT_TEMP + (player.getAttributeValue(SAttributes.HEAT_RESISTANCE) * 1.0D+(21.0D/63.0D));
	}

	public static double maxHeat(PlayerEntity player) {
		return Survive.DEFAULT_TEMP + (player.getAttributeValue(SAttributes.HEAT_RESISTANCE) * 1.0D+(28.0D/63.0D));
	}
	
	public static double firstCold(PlayerEntity player) {
		return Survive.DEFAULT_TEMP - (player.getAttributeValue(SAttributes.COLD_RESISTANCE) * 1.0D);
	}
	
	public static double secondCold(PlayerEntity player) {
		return Survive.DEFAULT_TEMP - (player.getAttributeValue(SAttributes.COLD_RESISTANCE) * 1.0D+(21.0D/63.0D));
	}

	public static double maxCold(PlayerEntity player) {
		return Survive.DEFAULT_TEMP - (player.getAttributeValue(SAttributes.COLD_RESISTANCE) * 1.0D+(28.0D/63.0D));
	}



	protected static final PerlinNoiseGenerator TEMPERATURE_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(1234L), ImmutableList.of(0));

	/**
	 * Gets the current temperature at the given location, based off of the default for this biome, the elevation of the
	 * position, and {@linkplain #Biome#TEMPERATURE_NOISE} some random perlin noise. Although this is a heavily modified version to account for altitude
	 */
	public static float getTemperature(Biome biome, BlockPos pos) {
		float f = (float)(TEMPERATURE_NOISE.noiseAt((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F), false) * 4.0D);
		float modifier = 1.0f;
		if (Survive.biomeTemperatureMap.containsKey(biome.getRegistryName())) {
			if (pos.getY() > 64.0F) {
				modifier = Survive.biomeTemperatureMap.get(biome.getRegistryName()).getAltitudeLevelModifier().getFirst();
			} else if (pos.getY() < 64.0F) {
				modifier = Survive.biomeTemperatureMap.get(biome.getRegistryName()).getAltitudeLevelModifier().getSecond();
			}
		}
		return biome.getTemperature() - modifier*((f + (float)pos.getY() - 64.0F) * 0.05F / 3.75F);
	}

}
