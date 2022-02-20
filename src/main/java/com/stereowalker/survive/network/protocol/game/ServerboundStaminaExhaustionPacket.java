package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundStaminaExhaustionPacket extends ServerboundUnionPacket{
	private float exhaustion;
	
	public ServerboundStaminaExhaustionPacket(final float exhaustion) {
		super(Survive.getInstance().channel);
		this.exhaustion = exhaustion;
	}

	public ServerboundStaminaExhaustionPacket(FriendlyByteBuf packetBuffer) {
		super(packetBuffer, Survive.getInstance().channel);
		this.exhaustion = packetBuffer.readFloat();
	}

	@Override
	public void encode(final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeFloat(this.exhaustion);
	}

	@Override
	public boolean handleOnServer(ServerPlayer sender) {
		if (Survive.STAMINA_CONFIG.enabled) {
			StaminaData stats = SurviveEntityStats.getEnergyStats(sender);
			stats.addExhaustion(sender, exhaustion, "Energy from client");
			SurviveEntityStats.setStaminaStats(sender, stats);
		}
		return true;
	}
}
