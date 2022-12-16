package com.stereowalker.survive.needs;

import com.google.common.collect.ImmutableList;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.json.BiomeTemperatureJsonHolder;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class TemperatureUtil {
	public static double firstHeat(Player player) {
		return Survive.DEFAULT_TEMP + (player.getAttributeValue(SAttributes.HEAT_RESISTANCE) * 1.0D);
	}

	public static double secondHeat(Player player) {
		return Survive.DEFAULT_TEMP + (player.getAttributeValue(SAttributes.HEAT_RESISTANCE) * 1.0D+(21.0D/63.0D));
	}

	public static double maxHeat(Player player) {
		return Survive.DEFAULT_TEMP + (player.getAttributeValue(SAttributes.HEAT_RESISTANCE) * 1.0D+(28.0D/63.0D));
	}
	
	public static double firstCold(Player player) {
		return Survive.DEFAULT_TEMP - (player.getAttributeValue(SAttributes.COLD_RESISTANCE) * 1.0D);
	}
	
	public static double secondCold(Player player) {
		return Survive.DEFAULT_TEMP - (player.getAttributeValue(SAttributes.COLD_RESISTANCE) * 1.0D+(21.0D/63.0D));
	}

	public static double maxCold(Player player) {
		return Survive.DEFAULT_TEMP - (player.getAttributeValue(SAttributes.COLD_RESISTANCE) * 1.0D+(28.0D/63.0D));
	}



	private static final PerlinSimplexNoise TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(1234L)), ImmutableList.of(0));
	   
	/**
	 * Gets the current temperature at the given location, based off of the default for this biome, the elevation of the
	 * position, and {@linkplain #Biome#TEMPERATURE_NOISE} some random perlin noise. Although this is a heavily modified version to account for altitude
	 */
	public static float getTemperature(Biome biome, BlockPos pos) {
		float f = (float)(TEMPERATURE_NOISE.getValue((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F), false) * 4.0D);
		float modifier = 1.0f;
		float t = biome.getBaseTemperature();
		if (DataMaps.Server.biomeTemperature.containsKey(biome.getRegistryName())) {
			BiomeTemperatureJsonHolder temperatureData = DataMaps.Server.biomeTemperature.get(biome.getRegistryName());
			t = (temperatureData.getTemperature() + 2) / 2;
			if (pos.getY() > 64.0F) {
				modifier = temperatureData.getAltitudeLevelModifier().getFirst();
			} else if (pos.getY() < 64.0F) {
				modifier = temperatureData.getAltitudeLevelModifier().getSecond();
			}
		}
		return t - modifier*((f + (float)pos.getY() - 64.0F) * 0.05F / 3.75F);
	}

}
