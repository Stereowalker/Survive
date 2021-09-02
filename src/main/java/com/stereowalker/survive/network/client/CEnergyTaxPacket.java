package com.stereowalker.survive.network.client;

import java.util.UUID;
import java.util.function.Supplier;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.StaminaStats;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CEnergyTaxPacket {
	private float exhaustion;
	private UUID uuid;

	public CEnergyTaxPacket(final float exhaustion, final UUID uuid) {
		this.uuid = uuid;
		this.exhaustion = exhaustion;
	}

	public static void encode(final CEnergyTaxPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeFloat(msg.exhaustion);
		packetBuffer.writeLong(msg.uuid.getMostSignificantBits());
		packetBuffer.writeLong(msg.uuid.getLeastSignificantBits());
	}

	public static CEnergyTaxPacket decode(final PacketBuffer packetBuffer) {
		return new CEnergyTaxPacket(packetBuffer.readFloat(), new UUID(packetBuffer.readLong(), packetBuffer.readLong()));
	}

	public static void handle(final CEnergyTaxPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayerEntity sender = context.getSender();
			if (sender == null) {
				return;
			}
			final float exhaustion = msg.exhaustion;
			final UUID uuid = msg.uuid;
			if (uuid.equals(PlayerEntity.getUUID(sender.getGameProfile()))) {
				if (Config.enable_stamina) {
					StaminaStats stats = SurviveEntityStats.getEnergyStats(sender);
					stats.addExhaustion(sender, exhaustion);
					SurviveEntityStats.setStaminaStats(sender, stats);
				}
			}
		});
		context.setPacketHandled(true);
	}
}
