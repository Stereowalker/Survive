package com.stereowalker.survive.network.client;

import java.util.UUID;
import java.util.function.Supplier;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.WaterStats;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CThirstMovementPacket {
	private float moveF;
	private float moveS;
	private boolean jump;
	private UUID uuid;

	public CThirstMovementPacket(final float moveF, final float moveS, final boolean jump, final UUID uuid) {
		this.uuid = uuid;
		this.moveF = moveF;
		this.moveS = moveS;
		this.jump = jump;
	}

	public static void encode(final CThirstMovementPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeFloat(msg.moveF);
		packetBuffer.writeFloat(msg.moveS);
		packetBuffer.writeBoolean(msg.jump);
		packetBuffer.writeLong(msg.uuid.getMostSignificantBits());
		packetBuffer.writeLong(msg.uuid.getLeastSignificantBits());
	}

	public static CThirstMovementPacket decode(final PacketBuffer packetBuffer) {
		return new CThirstMovementPacket(packetBuffer.readFloat(), packetBuffer.readFloat(), packetBuffer.readBoolean(), new UUID(packetBuffer.readLong(), packetBuffer.readLong()));
	}

	public static void handle(final CThirstMovementPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayerEntity sender = context.getSender();
			if (sender == null) {
				return;
			}
			final float moveF = msg.moveF;
			final float moveS = msg.moveS;
			final boolean jump = msg.jump;
			final UUID uuid = msg.uuid;
			if (uuid.equals(PlayerEntity.getUUID(sender.getGameProfile()))) {
				if (Config.enable_thirst) {
					WaterStats stats = SurviveEntityStats.getWaterStats(sender);
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
