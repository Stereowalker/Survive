package com.stereowalker.survive.network.client;

import java.util.Random;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.events.SurviveEvents;
import com.stereowalker.survive.util.EnergyStats;
import com.stereowalker.unionlib.network.client.CUnionPacket;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

public class CArmorStaminaPacket extends CUnionPacket {

	public CArmorStaminaPacket() {
		super(Survive.getInstance().channel);
	}

	public CArmorStaminaPacket(PacketBuffer packetBuffer) {
		super(packetBuffer, Survive.getInstance().channel);
	}

	@Override
	public void encode(final PacketBuffer packetBuffer) {
	}

	@Override
	public boolean handleOnServer(ServerPlayerEntity sender) {
		Random rand = new Random();
		if (Config.enable_stamina) {
			EnergyStats stats = SurviveEntityStats.getEnergyStats(sender);
			float moveMul = 0;
			int rgn = rand.nextInt(1);

			float maxWeight = 21.0F;

			if (SurviveEvents.getTotalArmorWeight(sender)/maxWeight > 1.0F) moveMul += SurviveEvents.getTotalArmorWeight(sender)/maxWeight - 1.0F;

			moveMul *= SurviveEvents.getTotalArmorWeight(sender)/maxWeight;

			if (moveMul > 0) stats.addExhaustion(sender, moveMul);
			else if (rgn == 0 && SurviveEvents.getTotalArmorWeight(sender)/maxWeight < 1.0F) stats.addStats(1);
			SurviveEntityStats.setEnergyStats(sender, stats);
		}
		return true;
	}
}
