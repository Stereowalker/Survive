package com.stereowalker.survive.core;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.HygieneData;
import com.stereowalker.survive.needs.NutritionData;
import com.stereowalker.survive.needs.SleepData;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.survive.needs.TemperatureData;
import com.stereowalker.survive.needs.WaterData;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;

public class SurviveEntityStats {
	public static String waterStatsID = "WaterStats";
	public static String temperatureStatsID = "TemperatureStats";
	public static String energyStatsID = "EnergyStats";
	public static String hygieneStatsID = "HygieneStats";
	public static String nutritionStatsID = "NutritionStats";
	public static String sleepStatsID = "SleepStats";
	//Getters

	public static WaterData getWaterStats(LivingEntity entity) {
		WaterData stats = new WaterData();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(waterStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(waterStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static StaminaData getEnergyStats(LivingEntity entity) {
		StaminaData stats = new StaminaData(entity.getAttributeValue(SAttributes.MAX_STAMINA));
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(energyStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(energyStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static TemperatureData getTemperatureStats(LivingEntity entity) {
		TemperatureData stats = new TemperatureData();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(temperatureStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(temperatureStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static HygieneData getHygieneStats(LivingEntity entity) {
		HygieneData stats = new HygieneData();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(hygieneStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(hygieneStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static NutritionData getNutritionStats(LivingEntity entity) {
		NutritionData stats = new NutritionData();
		if(entity != null) {
			if (getModNBT(entity) != null && getModNBT(entity).contains(nutritionStatsID, 10)) {
				stats.read(getModNBT(entity).getCompound(nutritionStatsID));
				return stats;
			}
		}
		return stats;
	}
	
	public static SleepData getSleepStats(LivingEntity entity) {
		SleepData stats = new SleepData();
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

	public static void setWaterStats(LivingEntity entity, WaterData waterStats) {
		CompoundTag compound2 = new CompoundTag();
		waterStats.write(compound2);
		getModNBT(entity).put(waterStatsID, compound2);
	}
	
	public static void setStaminaStats(LivingEntity entity, StaminaData energyStats) {
		CompoundTag compound2 = new CompoundTag();
		energyStats.write(compound2);
		getModNBT(entity).put(energyStatsID, compound2);
	}
	
	public static void setTemperatureStats(Entity entity, TemperatureData temperatureStats) {
		CompoundTag compound2 = new CompoundTag();
		temperatureStats.write(compound2);
		getModNBT(entity).put(temperatureStatsID, compound2);
	}
	
	public static void setHygieneStats(LivingEntity entity, HygieneData hygieneStats) {
		CompoundTag compound2 = new CompoundTag();
		hygieneStats.write(compound2);
		getModNBT(entity).put(hygieneStatsID, compound2);
	}
	
	public static void setNutritionStats(LivingEntity entity, NutritionData nutritionStats) {
		CompoundTag compound2 = new CompoundTag();
		nutritionStats.write(compound2);
		getModNBT(entity).put(nutritionStatsID, compound2);
	}
	
	public static void setSleepStats(LivingEntity entity, SleepData sleepStats) {
		CompoundTag compound2 = new CompoundTag();
		sleepStats.write(compound2);
		getModNBT(entity).put(sleepStatsID, compound2);
	}

	public static void setWetTime(LivingEntity entity, int wetTime) {
		getModNBT(entity).putInt(append("WetTime"), wetTime);
	}

	public static boolean addWetTime(LivingEntity entity, int wetTime) {
		CompoundTag compound = entity.getPersistentData();
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
	
	public static void addStatsOnSpawn(ItemFrame frame) {
		if (frame != null) {
			CompoundTag compound;
			compound = getOrCreateModNBT(frame);
			if(frame.isAlive()) {
				if (!compound.contains(temperatureStatsID)) {
					setTemperatureStats(frame, new TemperatureData());
				}
			}
		}
	}

	public static void addStatsOnSpawn(Player player) {
		if (player != null) {
			CompoundTag compound;
			compound = getOrCreateModNBT(player);
			String name = player.getScoreboardName();
			if(player.isAlive()) {
				if (!compound.contains(waterStatsID)) {
					setWaterStats(player, new WaterData());
				}
				if (!compound.contains(energyStatsID)) {
					setStaminaStats(player, new StaminaData(player.getAttributeValue(SAttributes.MAX_STAMINA)));
				}
				if (!compound.contains(temperatureStatsID)) {
					setTemperatureStats(player, new TemperatureData());
				}
				if (!compound.contains(hygieneStatsID)) {
					setHygieneStats(player, new HygieneData());
				}
				if (!compound.contains(nutritionStatsID)) {
					setNutritionStats(player, new NutritionData());
				}
				if (!compound.contains(sleepStatsID)) {
					setSleepStats(player, new SleepData());
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

	public static CompoundTag getModNBT(Entity entity) {
		return entity.getPersistentData().getCompound(getModDataString());
	}

	public static CompoundTag getOrCreateModNBT(Entity entity) {
		if (!entity.getPersistentData().contains(getModDataString(), 10)) {
			entity.getPersistentData().put(getModDataString(), new CompoundTag());
		}
		return entity.getPersistentData().getCompound(getModDataString());
	}

	public static void setModNBT(CompoundTag nbt, Entity entity) {
		entity.getPersistentData().put(getModDataString(), nbt);
	}
}
