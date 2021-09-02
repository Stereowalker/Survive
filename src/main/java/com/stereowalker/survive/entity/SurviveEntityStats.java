package com.stereowalker.survive.entity;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.util.HygieneStats;
import com.stereowalker.survive.util.NutritionStats;
import com.stereowalker.survive.util.SleepStats;
import com.stereowalker.survive.util.StaminaStats;
import com.stereowalker.survive.util.TemperatureStats;
import com.stereowalker.survive.util.WaterStats;
import com.stereowalker.survive.util.WellbeingStats;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class SurviveEntityStats {
	public static String waterStatsID = "WaterStats";
	public static String temperatureStatsID = "TemperatureStats";
	public static String energyStatsID = "EnergyStats";
	public static String hygieneStatsID = "HygieneStats";
	public static String nutritionStatsID = "NutritionStats";
	public static String wellbeingStatsID = "WellbeingStats";
	public static String sleepStatsID = "SleepStats";
	//Getters

	public static WaterStats getWaterStats(LivingEntity entity) {
		WaterStats stats = new WaterStats();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(waterStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(waterStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static StaminaStats getEnergyStats(LivingEntity entity) {
		StaminaStats stats = new StaminaStats();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(energyStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(energyStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static TemperatureStats getTemperatureStats(LivingEntity entity) {
		TemperatureStats stats = new TemperatureStats();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(temperatureStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(temperatureStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static HygieneStats getHygieneStats(LivingEntity entity) {
		HygieneStats stats = new HygieneStats();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(hygieneStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(hygieneStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static NutritionStats getNutritionStats(LivingEntity entity) {
		NutritionStats stats = new NutritionStats();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(nutritionStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(nutritionStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static WellbeingStats getWellbeingStats(LivingEntity entity) {
		WellbeingStats stats = new WellbeingStats();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(wellbeingStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(wellbeingStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static SleepStats getSleepStats(LivingEntity entity) {
		SleepStats stats = new SleepStats();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(sleepStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(sleepStatsID));
				return stats;
			}
		}
		return stats;
	}

	public static int getWetTime(LivingEntity entity) {
		if (getModNBT(entity) != null && getModNBT(entity).contains(append("WetTime"))) {
			return getModNBT(entity).getInt(append("WetTime"));
		}
		return 0;
	}

	//Setters

	public static void setWaterStats(LivingEntity entity, WaterStats waterStats) {
		CompoundNBT compound2 = new CompoundNBT();
		waterStats.write(compound2);
		getModNBT(entity).put(waterStatsID, compound2);
	}
	
	public static void setStaminaStats(LivingEntity entity, StaminaStats energyStats) {
		CompoundNBT compound2 = new CompoundNBT();
		energyStats.write(compound2);
		getModNBT(entity).put(energyStatsID, compound2);
	}
	
	public static void setTemperatureStats(LivingEntity entity, TemperatureStats temperatureStats) {
		CompoundNBT compound2 = new CompoundNBT();
		temperatureStats.write(compound2);
		getModNBT(entity).put(temperatureStatsID, compound2);
	}
	
	public static void setHygieneStats(LivingEntity entity, HygieneStats hygieneStats) {
		CompoundNBT compound2 = new CompoundNBT();
		hygieneStats.write(compound2);
		getModNBT(entity).put(hygieneStatsID, compound2);
	}
	
	public static void setNutritionStats(LivingEntity entity, NutritionStats nutritionStats) {
		CompoundNBT compound2 = new CompoundNBT();
		nutritionStats.write(compound2);
		getModNBT(entity).put(nutritionStatsID, compound2);
	}
	
	public static void setWellbeingStats(LivingEntity entity, WellbeingStats wellbeingStats) {
		CompoundNBT compound2 = new CompoundNBT();
		wellbeingStats.write(compound2);
		getModNBT(entity).put(nutritionStatsID, compound2);
	}
	
	public static void setSleepStats(LivingEntity entity, SleepStats sleepStats) {
		CompoundNBT compound2 = new CompoundNBT();
		sleepStats.write(compound2);
		getModNBT(entity).put(sleepStatsID, compound2);
	}

	public static void setWetTime(LivingEntity entity, int wetTime) {
		getModNBT(entity).putInt(append("WetTime"), wetTime);
	}

	public static boolean addWetTime(LivingEntity entity, int wetTime) {
		CompoundNBT compound = entity.getPersistentData();
		if (compound != null) {
			setWetTime(entity, getWetTime(entity)+wetTime);
			if (getWetTime(entity) < 0) {
				setWetTime(entity, 0);
			}
			if (getWetTime(entity) > 3600) {
				setWetTime(entity, 3600);
			}
			return true;
		}
		return false;
	}

	public static void addStatsOnSpawn(PlayerEntity player) {
		if (player != null) {
			CompoundNBT compound;
			compound = getOrCreateModNBT(player);
			String name = player.getScoreboardName();
			if(player.isAlive()) {
				if (!compound.contains(waterStatsID)) {
					setWaterStats(player, new WaterStats());
				}
				if (!compound.contains(energyStatsID)) {
					setStaminaStats(player, new StaminaStats());
				}
				if (!compound.contains(temperatureStatsID)) {
					setTemperatureStats(player, new TemperatureStats());
				}
				if (!compound.contains(hygieneStatsID)) {
					setHygieneStats(player, new HygieneStats());
				}
				if (!compound.contains(nutritionStatsID)) {
					setNutritionStats(player, new NutritionStats());
				}
				if (!compound.contains(wellbeingStatsID)) {
					setWellbeingStats(player, new WellbeingStats());
				}
				if (!compound.contains(sleepStatsID)) {
					setSleepStats(player, new SleepStats());
				}
				if (!compound.contains(append("WetTime"))) {
					setWetTime(player, 0);
					Survive.getInstance().debug("Set " + name + "'s wet time to " + getWetTime(player));
				}
			}
		}
	}

	private static String append(String string) {
		return Survive.MOD_ID+":"+string;
	}

	public static String getModDataString() {
		return Survive.MOD_ID+":PlayerData";
	}

	public static CompoundNBT getModNBT(Entity entity) {
		return entity.getPersistentData().getCompound(getModDataString());
	}

	public static CompoundNBT getOrCreateModNBT(Entity entity) {
		if (!entity.getPersistentData().contains(getModDataString(), 10)) {
			entity.getPersistentData().put(getModDataString(), new CompoundNBT());
		}
		return entity.getPersistentData().getCompound(getModDataString());
	}

	public static void setModNBT(CompoundNBT nbt, Entity entity) {
		entity.getPersistentData().put(getModDataString(), nbt);
	}
}
