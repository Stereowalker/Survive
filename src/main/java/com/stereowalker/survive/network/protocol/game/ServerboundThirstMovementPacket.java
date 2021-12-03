package com.stereowalker.survive.network.protocol.game;

import java.util.UUID;
import java.util.function.Supplier;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.WaterData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundThirstMovementPacket {
	private float moveF;
	private float moveS;
	private boolean jump;
	private UUID uuid;

	public ServerboundThirstMovementPacket(final float moveF, final float moveS, final boolean jump, final UUID uuid) {
		this.uuid = uuid;
		this.moveF = moveF;
		this.moveS = moveS;
		this.jump = jump;
	}

	public static void encode(final ServerboundThirstMovementPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeFloat(msg.moveF);
		packetBuffer.writeFloat(msg.moveS);
		packetBuffer.writeBoolean(msg.jump);
		packetBuffer.writeLong(msg.uuid.getMostSignificantBits());
		packetBuffer.writeLong(msg.uuid.getLeastSignificantBits());
	}

	public static ServerboundThirstMovementPacket decode(final FriendlyByteBuf packetBuffer) {
		return new ServerboundThirstMovementPacket(packetBuffer.readFloat(), packetBuffer.readFloat(), packetBuffer.readBoolean(), new UUID(packetBuffer.readLong(), packetBuffer.readLong()));
	}

	public static void handle(final ServerboundThirstMovementPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayer sender = context.getSender();
			if (sender == null) {
				return;
			}
			final float moveF = msg.moveF;
			final float moveS = msg.moveS;
			final boolean jump = msg.jump;
			final UUID uuid = msg.uuid;
			if (uuid.equals(Player.createPlayerUUID(sender.getGameProfile()))) {
				if (Survive.CONFIG.enable_thirst) {
					WaterData stats = SurviveEntityStats.getWaterStats(sender);
					int movM = (int) ((moveS+moveF)*10);
					float moveMul;
					if (movM > 0) {
						moveMul = 1.0F;
					}
					else {
						moveMul = 0.5F;
					}

					if (sender.isSprinting()) {
						moveMul+=2.0F;
					}
					if (sender.isCrouching()) {
						moveMul+=0.5F;
					}
					if (jump) {
						moveMul+=1.5F;
					}
					stats.addExhaustion(sender, 0.1F*moveMul);
					stats.save(sender);
				}
			}
		});
		context.setPacketHandled(true);
	}
}
