package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.WeightHandler;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundArmorStaminaPacket extends ServerboundUnionPacket {

	public ServerboundArmorStaminaPacket() {
		super(Survive.getInstance().channel);
	}

	public ServerboundArmorStaminaPacket(FriendlyByteBuf packetBuffer) {
		super(packetBuffer, Survive.getInstance().channel);
	}

	@Override
	public void encode(final FriendlyByteBuf packetBuffer) {
	}

	@Override
	public boolean handleOnServer(ServerPlayer sender) {
		if (Survive.STAMINA_CONFIG.enabled) {
			StaminaData stats = SurviveEntityStats.getEnergyStats(sender);
			float moveMul = (WeightHandler.getTotalArmorWeight(sender)/Survive.STAMINA_CONFIG.max_weight)*0.1F;

			if (WeightHandler.getTotalArmorWeight(sender)/Survive.STAMINA_CONFIG.max_weight > 1.0F) moveMul += (WeightHandler.getTotalArmorWeight(sender)/Survive.STAMINA_CONFIG.max_weight) - 1.0F;

			if (moveMul > 0) stats.addExhaustion(sender, moveMul*2.0f, "Armor weight");
			SurviveEntityStats.setStaminaStats(sender, stats);
		}
		return true;
	}
}
