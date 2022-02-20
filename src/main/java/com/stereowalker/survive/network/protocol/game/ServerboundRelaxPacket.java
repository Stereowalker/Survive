package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundRelaxPacket extends ServerboundUnionPacket {
	int amount;

	public ServerboundRelaxPacket(int amount) {
		super(Survive.getInstance().channel);
		this.amount = amount;
	}

	public ServerboundRelaxPacket(FriendlyByteBuf packetBuffer) {
		super(packetBuffer, Survive.getInstance().channel);
		this.amount = packetBuffer.readVarInt();
	}

	@Override
	public void encode(final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeVarInt(this.amount);
	}

	@Override
	public boolean handleOnServer(ServerPlayer sender) {
		if (Survive.STAMINA_CONFIG.enabled) {
			StaminaData stats = SurviveEntityStats.getEnergyStats(sender);
			stats.relax(this.amount, sender.getAttributeValue(SAttributes.MAX_STAMINA));
			stats.save(sender);
		}
		return true;
	}
}
