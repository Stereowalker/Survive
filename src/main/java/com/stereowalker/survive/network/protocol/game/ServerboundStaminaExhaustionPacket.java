package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundStaminaExhaustionPacket extends ServerboundUnionPacket {
	private float exhaustion;
	
	public ServerboundStaminaExhaustionPacket(final float exhaustion) {
		super(null);
		this.exhaustion = exhaustion;
	}

	public ServerboundStaminaExhaustionPacket(RegistryFriendlyByteBuf packetBuffer) {
		super(packetBuffer);
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
	
	public static ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "serverbound_stamina_exhaustion");
	@Override
	public ResourceLocation id() {
		return id;
	}
}
