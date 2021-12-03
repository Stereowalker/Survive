package com.stereowalker.survive.network.protocol.game;

import java.util.UUID;
import java.util.function.Supplier;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.StaminaData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundEnergyTaxPacket {
	private float exhaustion;
	private UUID uuid;

	public ServerboundEnergyTaxPacket(final float exhaustion, final UUID uuid) {
		this.uuid = uuid;
		this.exhaustion = exhaustion;
	}

	public static void encode(final ServerboundEnergyTaxPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeFloat(msg.exhaustion);
		packetBuffer.writeLong(msg.uuid.getMostSignificantBits());
		packetBuffer.writeLong(msg.uuid.getLeastSignificantBits());
	}

	public static ServerboundEnergyTaxPacket decode(final FriendlyByteBuf packetBuffer) {
		return new ServerboundEnergyTaxPacket(packetBuffer.readFloat(), new UUID(packetBuffer.readLong(), packetBuffer.readLong()));
	}

	public static void handle(final ServerboundEnergyTaxPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayer sender = context.getSender();
			if (sender == null) {
				return;
			}
			final float exhaustion = msg.exhaustion;
			final UUID uuid = msg.uuid;
			if (uuid.equals(Player.createPlayerUUID(sender.getGameProfile()))) {
				if (Survive.CONFIG.enable_stamina) {
					StaminaData stats = SurviveEntityStats.getEnergyStats(sender);
					stats.addExhaustion(sender, exhaustion, "Energy from client");
					SurviveEntityStats.setStaminaStats(sender, stats);
				}
			}
		});
		context.setPacketHandled(true);
	}
}
