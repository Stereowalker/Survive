package com.stereowalker.survive.network.client;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.events.SurviveEvents;
import com.stereowalker.survive.util.StaminaStats;
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
		if (Config.enable_stamina) {
			StaminaStats stats = SurviveEntityStats.getEnergyStats(sender);
			float moveMul = (SurviveEvents.getTotalArmorWeight(sender)/Survive.MAX_WEIGHT)*0.1F;

			if (SurviveEvents.getTotalArmorWeight(sender)/Survive.MAX_WEIGHT > 1.0F) moveMul += (SurviveEvents.getTotalArmorWeight(sender)/Survive.MAX_WEIGHT) - 1.0F;

			if (moveMul > 0) stats.addExhaustion(sender, moveMul*2.5f);
			SurviveEntityStats.setStaminaStats(sender, stats);
		}
		return true;
	}
}
