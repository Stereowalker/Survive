package com.stereowalker.survive.events;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.api.insert.InsertSetter;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class StaminaEvents {

	public static void replenishEnergyOnSleep(LevelAccessor level, InsertSetter<Long> newTime) {
		for (Player player : level.players()) {
			StaminaData energyStats = ((IRealisticEntity)player).staminaData();
			int staminaToRecover = Mth.ceil(((float)(newTime.get()-level.getLevelData().getGameTime())/Survive.STAMINA_CONFIG.sleepTime)*(energyStats.getMaxLTS()+6));
			energyStats.relax(staminaToRecover, player.getAttributeValue(SAttributes.MAX_STAMINA.holder()));
		}
	}
}
