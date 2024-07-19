package com.stereowalker.survive.events;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.SleepData;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.api.insert.InsertSetter;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class SleepEvents {
	public static void allowSleep(Player player, BlockPos pos, BedSleepingProblem vanillaProblem, InsertSetter<BedSleepingProblem> problem) {
		if (player instanceof ServerPlayer splayer && vanillaProblem == BedSleepingProblem.NOT_POSSIBLE_NOW) {
			if (Survive.CONFIG.enable_sleep && SurviveEntityStats.getSleepStats(splayer).getAwakeTimer() > time(0) - 5000 && Survive.CONFIG.canSleepDuringDay) {
				problem.set(null);
			}
			else if (SurviveEntityStats.getEnergyStats(splayer).getEnergyLevel() < splayer.getAttributeValue(SAttributes.MAX_STAMINA.holder())/2) {
				problem.set(null);
			}
		}
	}

	public static void allowSleep(Player player, BlockPos sleepingPos, InsertSetter<Boolean> mayContinueSleeping) {
		if (player instanceof ServerPlayer splayer) {
			if (Survive.CONFIG.enable_sleep && SurviveEntityStats.getSleepStats(splayer).getAwakeTimer() > time(0) - 5000 && Survive.CONFIG.canSleepDuringDay) {
				mayContinueSleeping.set(true);
			}
			else if (SurviveEntityStats.getEnergyStats(splayer).getEnergyLevel() < splayer.getAttributeValue(SAttributes.MAX_STAMINA.holder())/2) {
				mayContinueSleeping.set(true);
			}
		}
	}
	
	public static void replenishEnergy(LevelAccessor level) {
		for (Player player : level.players()) {
			SleepData stats = SurviveEntityStats.getSleepStats(player);
			stats.setAwakeTimer(0);
			stats.save(player);
		}
	}
	
	public static int time(int i) {
		return Survive.CONFIG.initialTiredTime+(Survive.CONFIG.tiredTimeStep*i);
	}
}
