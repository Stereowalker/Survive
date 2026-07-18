package com.stereowalker.survive.core;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.WaterData;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SurviveEntityStats {
//	public static String waterStatsID = "WaterStats";
	//Getters

//	public static WaterData getWaterStats(LivingEntity entity) {
//		WaterData stats = new WaterData();
//		if(entity != null) {
//			if (getModNBT(entity) != null && getModNBT(entity).contains(waterStatsID, 10)) {
//				stats.read(getModNBT(entity).getCompound(waterStatsID));
//				return stats;
//			}
//		}
//		return stats;
//	}
	
	public static int getWetTime(LivingEntity entity) {
		if (getModNBT(entity) != null && getModNBT(entity).contains(append("WetTime"))) {
			return getModNBT(entity).getIntOr(append("WetTime"), 0);
		}
		return 0;
	}

	//Setters

//	public static void setWaterStats(LivingEntity entity, WaterData waterStats) {
//		CompoundTag compound2 = new CompoundTag();
//		waterStats.write(compound2, false);
//		getModNBT(entity).put(waterStatsID, compound2);
//	}
	
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
	
//	public static void addStatsOnSpawn(ItemFrame frame) {
//		if (frame != null) {
//			CompoundTag compound;
//			compound = getOrCreateModNBT(frame);
//			if(frame.isAlive()) {
//				if (!compound.contains(temperatureStatsID)) {
//					setTemperatureStats(frame, new TemperatureData());
//				}
//			}
//		}
//	}

	public static void addStatsOnSpawn(Player player) {
		if (player != null) {
			CompoundTag compound;
			compound = getOrCreateModNBT(player);
			String name = player.getScoreboardName();
			if(player.isAlive()) {
//				if (!compound.contains(waterStatsID)) {
//					setWaterStats(player, new WaterData());
//				}
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
		return entity.getPersistentData().getCompoundOrEmpty(getModDataString());
	}

	public static CompoundTag getOrCreateModNBT(Entity entity) {
		if (!entity.getPersistentData().contains(getModDataString())) {
			entity.getPersistentData().put(getModDataString(), new CompoundTag());
		}
		return entity.getPersistentData().getCompoundOrEmpty(getModDataString());
	}

	public static void setModNBT(CompoundTag nbt, Entity entity) {
		entity.getPersistentData().put(getModDataString(), nbt);
	}
}
