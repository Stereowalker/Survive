package com.stereowalker.survive.compat;

import com.stereowalker.survive.util.TemperatureUtil;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;

public class SereneSeasonsCompat {

	public static boolean snowsHere(World world, BlockPos pos) {
		if (world.func_242406_i(pos).isPresent() && !sereneseasons.config.BiomeConfig.usesTropicalSeasons(world.func_242406_i(pos).get())) {
			return SeasonHelper.getSeasonState(world).getSeason() == Season.WINTER;
		}
		return false;
	}

	public static float modifyTemperatureBySeason(World world, BlockPos pos) {
		if (world.func_242406_i(pos) != null &&
				world.func_242406_i(pos).isPresent() && 
				sereneseasons.config.BiomeConfig.usesTropicalSeasons(world.func_242406_i(pos).get())) {
			switch (SeasonHelper.getSeasonState(world).getTropicalSeason()) {
			case EARLY_DRY: return 0.1F;
			case MID_DRY: return 0.2F;
			case LATE_DRY: return 0.1F;

			case EARLY_WET: return -0.1F;
			case MID_WET: return -0.2F;
			case LATE_WET: return -0.1F;

			default: return 0.0F;
			}
		}
		else {
			switch (SeasonHelper.getSeasonState(world).getSubSeason()) {
			case EARLY_WINTER: return -0.6F;
			case MID_WINTER: return -0.9F;
			case LATE_WINTER: return -0.6F;

			case EARLY_SPRING: return -0.3F;
			case MID_SPRING: return 0.0F;
			case LATE_SPRING: return 0.3F;

			case EARLY_SUMMER: return 0.6F;
			case MID_SUMMER: return 0.9F;
			case LATE_SUMMER: return 0.6F;

			case EARLY_AUTUMN: return 0.3F;
			case MID_AUTUMN: return 0.0F;
			case LATE_AUTUMN: return -0.3F;

			default: return 0.0F;
			}
		}
	}
	
	
    /**
     * From Serene Seasons, Altered the temp
     * @param subSeason
     * @param biome
     * @param key
     * @param pos
     * @return
     */
    public static float getBiomeTemperatureInSeason(Season.SubSeason subSeason, Biome biome, RegistryKey<Biome> key, BlockPos pos)
    {
        boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(key);
        float biomeTemp = TemperatureUtil.getTemperature(biome, pos);
        if (!tropicalBiome && biome.getTemperature() <= 0.8F && BiomeConfig.enablesSeasonalEffects(key))
        {
            switch (subSeason)
            {
                default:
                    break;

                case LATE_SPRING: case EARLY_AUTUMN:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.1F, -0.5F, 2.0F);
                break;

                case MID_SPRING: case MID_AUTUMN:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.2F, -0.5F, 2.0F);
                break;

                case EARLY_SPRING: case LATE_AUTUMN:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.4F, -0.5F, 2.0F);
                break;

                case EARLY_WINTER: case MID_WINTER: case LATE_WINTER:
                biomeTemp = MathHelper.clamp(biomeTemp - 0.8F, -0.5F, 2.0F);
                break;
            }
        }
        return biomeTemp;
    }
}
