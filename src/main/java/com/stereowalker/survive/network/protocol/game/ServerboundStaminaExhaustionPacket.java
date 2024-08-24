package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundStaminaExhaustionPacket extends ServerboundUnionPacket {
	private float exhaustion;
	
	public ServerboundStaminaExhaustionPacket(final float exhaustion) {
		super(Survive.getInstance().channel);
		this.exhaustion = exhaustion;
	}

	public ServerboundStaminaExhaustionPacket(RegistryFriendlyByteBuf packetBuffer) {
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
			((IRealisticEntity)sender).addStaminaExhaustion(exhaustion, "Energy from client");
		}
		return true;
	}
	
	public static ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "serverbound_stamina_exhaustion");
	@Override
	public ResourceLocation id() {
		return id;
	}
}
