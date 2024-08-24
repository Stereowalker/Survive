package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundRelaxPacket extends ServerboundUnionPacket {
	int amount;

	public ServerboundRelaxPacket(int amount) {
		super(Survive.getInstance().channel);
		this.amount = amount;
	}

	public ServerboundRelaxPacket(RegistryFriendlyByteBuf packetBuffer) {
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
			((IRealisticEntity)sender).staminaData().relax(this.amount, sender.getAttributeValue(SAttributes.MAX_STAMINA.holder()));
		}
		return true;
	}
	
	public static ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "serverbound_relax");
	@Override
	public ResourceLocation id() {
		return id;
	}
}
