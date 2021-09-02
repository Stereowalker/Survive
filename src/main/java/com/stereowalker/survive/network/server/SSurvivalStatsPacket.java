package com.stereowalker.survive.network.server;

import java.util.UUID;
import java.util.function.Supplier;

import com.stereowalker.survive.entity.SurviveEntityStats;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class SSurvivalStatsPacket {
	private CompoundNBT stats;
	private UUID uuid;
	
	public SSurvivalStatsPacket(final CompoundNBT statsIn, final UUID uuid) {
		this.stats = statsIn;
		this.uuid = uuid;
	}
	
	public SSurvivalStatsPacket(final ServerPlayerEntity player){
		this(SurviveEntityStats.getModNBT(player), player.getUniqueID());
	}
	
	public static void encode(final SSurvivalStatsPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeCompoundTag(msg.stats);
		packetBuffer.writeLong(msg.uuid.getMostSignificantBits());
        packetBuffer.writeLong(msg.uuid.getLeastSignificantBits());
	}
	
	public static SSurvivalStatsPacket decode(final PacketBuffer packetBuffer) {
		return new SSurvivalStatsPacket(packetBuffer.readCompoundTag(), new UUID(packetBuffer.readLong(), packetBuffer.readLong()));
	}
	
	@SuppressWarnings("deprecation")
	public static void handle(final SSurvivalStatsPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			final CompoundNBT stats = msg.stats;
			final UUID uuid = msg.uuid;
			update(stats, uuid);
		}));
		context.setPacketHandled(true);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void update(final CompoundNBT stats, final UUID uuid) {
		if (uuid.equals(PlayerEntity.getUUID(Minecraft.getInstance().player.getGameProfile()))) {
			SurviveEntityStats.setModNBT(stats, Minecraft.getInstance().player);
		}
	}
}
