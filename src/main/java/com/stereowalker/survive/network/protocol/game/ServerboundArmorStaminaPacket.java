package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.WeightHandler;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.unionlib.network.protocol.game.ServerboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundArmorStaminaPacket extends ServerboundUnionPacket {

	public ServerboundArmorStaminaPacket() {
		super(Survive.getInstance().channel);
	}

	public ServerboundArmorStaminaPacket(RegistryFriendlyByteBuf packetBuffer) {
		super(packetBuffer, Survive.getInstance().channel);
	}

	@Override
	public void encode(final FriendlyByteBuf packetBuffer) {
	}

	@Override
	public boolean handleOnServer(ServerPlayer sender) {
		if (Survive.STAMINA_CONFIG.enabled) {
			float moveMul = (WeightHandler.getTotalArmorWeight(sender)/Survive.STAMINA_CONFIG.max_weight)*0.1F;

			if (WeightHandler.getTotalArmorWeight(sender)/Survive.STAMINA_CONFIG.max_weight > 1.0F) moveMul += (WeightHandler.getTotalArmorWeight(sender)/Survive.STAMINA_CONFIG.max_weight) - 1.0F;

			if (moveMul > 0) ((IRealisticEntity)sender).addStaminaExhaustion(moveMul*2.0f, "Armor weight", false);
		}
		return true;
	}
	
	public static ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "serverbound_interact_with_water");
	@Override
	public ResourceLocation id() {
		return id;
	}
}
