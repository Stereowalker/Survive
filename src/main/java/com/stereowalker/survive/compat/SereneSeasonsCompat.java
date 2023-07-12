package com.stereowalker.survive.compat;

import com.stereowalker.survive.world.seasons.Season;
import com.stereowalker.survive.world.seasons.Seasons;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.SeasonHelper;

public class SereneSeasonsCompat {

	public static boolean snowsHere(Level world, BlockPos pos) {
		if (world.getBiome(pos).isBound() && !SeasonHelper.usesTropicalSeasons(world.getBiome(pos))) {
			return SeasonHelper.getSeasonState(world).getSeason() == sereneseasons.api.season.Season.WINTER;
		}
		return false;
	}

	public static Season modifyTemperatureBySeason(Level world, BlockPos pos) {
		if (world.getBiome(pos) != null &&
				world.getBiome(pos).isBound()) {
			if (SeasonHelper.usesTropicalSeasons(world.getBiome(pos))) {
				switch (SeasonHelper.getSeasonState(world).getTropicalSeason()) {
				case EARLY_DRY: return Seasons.DRY_BEGIN;
				case MID_DRY: return Seasons.DRY_MIDST;
				case LATE_DRY: return Seasons.DRY_CLOSE;

				case EARLY_WET: return Seasons.WET_BEGIN;
				case MID_WET: return Seasons.WET_MIDST;
				case LATE_WET: return Seasons.WET_CLOSE;
				}
			}
			else {
				switch (SeasonHelper.getSeasonState(world).getSubSeason()) {
				case EARLY_WINTER: return Seasons.WINTER_BEGIN;
				case MID_WINTER: return Seasons.WINTER_MIDST;
				case LATE_WINTER: return Seasons.WINTER_CLOSE;

				case EARLY_SPRING: return Seasons.SPRING_BEGIN;
				case MID_SPRING: return Seasons.SPRING_MIDST;
				case LATE_SPRING: return Seasons.SPRING_CLOSE;

				case EARLY_SUMMER: return Seasons.SUMMER_BEGIN;
				case MID_SUMMER: return Seasons.SUMMER_MIDST;
				case LATE_SUMMER: return Seasons.SUMMER_CLOSE;

				case EARLY_AUTUMN: return Seasons.AUTUMN_BEGIN;
				case MID_AUTUMN: return Seasons.AUTUMN_MIDST;
				case LATE_AUTUMN: return Seasons.AUTUMN_CLOSE;
				}
			}
		}
		return Seasons.NONE;
	}
}
