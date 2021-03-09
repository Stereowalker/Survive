package com.stereowalker.survive.entity;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.util.EnergyStats;
import com.stereowalker.survive.util.HygieneStats;
import com.stereowalker.survive.util.TemperatureStats;
import com.stereowalker.survive.util.WaterStats;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class SurviveEntityStats {
	public static String waterStatsID = "WaterStats";
	public static String temperatureStatsID = "TemperatureStats";
	public static String energyStatsID = "EnergyStats";
	public static String hygieneStatsID = "HygieneStats";
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
	
	public static EnergyStats getEnergyStats(LivingEntity entity) {
		EnergyStats stats = new EnergyStats();
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

	public static int getAwakeTime(LivingEntity entity) {
		if (getModNBT(entity) != null && getModNBT(entity).contains(append("AwakeTime"))) {
			return getModNBT(entity).getInt(append("AwakeTime"));
		}
		return 0;
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
	
	public static void setEnergyStats(LivingEntity entity, EnergyStats energyStats) {
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

	public static void setAwakeTime(LivingEntity entity, int awakeTime) {
		getModNBT(entity).putInt(append("AwakeTime"), awakeTime);
	}

	public static void setWetTime(LivingEntity entity, int wetTime) {
		getModNBT(entity).putInt(append("WetTime"), wetTime);
	}

	public static boolean addAwakeTime(ServerPlayerEntity player, int awakeTime) {
		if (Config.enable_sleep) {
			if (getModNBT(player) != null && getModNBT(player).contains(append("AwakeTime")) && player.interactionManager.survivalOrAdventure()) {
				setAwakeTime(player, getAwakeTime(player)+awakeTime);
				if (getAwakeTime(player) < 0 ) {
					setAwakeTime(player, 0);
				}
				return true;
			}
		}
		return false;
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
					setEnergyStats(player, new EnergyStats());
				}
				if (!compound.contains(temperatureStatsID)) {
					setTemperatureStats(player, new TemperatureStats());
				}
				if (!compound.contains(append("AwakeTime"))) {
					setAwakeTime(player, 0);
					Survive.getInstance().debug("Set " + name + "'s awake time to " + getAwakeTime(player));
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
