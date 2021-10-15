package com.stereowalker.survive.compat;

import com.stereowalker.survive.needs.TemperatureUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;

public class SereneSeasonsCompat {

	public static boolean snowsHere(Level world, BlockPos pos) {
		if (world.getBiomeName(pos).isPresent() && !sereneseasons.config.BiomeConfig.usesTropicalSeasons(world.getBiomeName(pos).get())) {
			return SeasonHelper.getSeasonState(world).getSeason() == Season.WINTER;
		}
		return false;
	}

	public static float modifyTemperatureBySeason(Level world, BlockPos pos) {
		if (world.getBiomeName(pos) != null &&
				world.getBiomeName(pos).isPresent() && 
				sereneseasons.config.BiomeConfig.usesTropicalSeasons(world.getBiomeName(pos).get())) {
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
    public static float getBiomeTemperatureInSeason(Season.SubSeason subSeason, Biome biome, ResourceKey<Biome> key, BlockPos pos)
    {
        boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(key);
        float biomeTemp = TemperatureUtil.getTemperature(biome, pos);
        if (!tropicalBiome && biome.getBaseTemperature() <= 0.8F && BiomeConfig.enablesSeasonalEffects(key))
        {
            switch (subSeason)
            {
                default:
                    break;

                case LATE_SPRING: case EARLY_AUTUMN:
                biomeTemp = Mth.clamp(biomeTemp - 0.1F, -0.5F, 2.0F);
                break;

                case MID_SPRING: case MID_AUTUMN:
                biomeTemp = Mth.clamp(biomeTemp - 0.2F, -0.5F, 2.0F);
                break;

                case EARLY_SPRING: case LATE_AUTUMN:
                biomeTemp = Mth.clamp(biomeTemp - 0.4F, -0.5F, 2.0F);
                break;

                case EARLY_WINTER: case MID_WINTER: case LATE_WINTER:
                biomeTemp = Mth.clamp(biomeTemp - 0.8F, -0.5F, 2.0F);
                break;
            }
        }
        return biomeTemp;
    }
}
