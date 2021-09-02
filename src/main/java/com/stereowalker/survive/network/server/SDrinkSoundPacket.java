package com.stereowalker.survive.network.server;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class SDrinkSoundPacket {
	private BlockPos pos;
	private UUID uuid;
	
	public SDrinkSoundPacket(
			final BlockPos pos,
			final UUID uuid) {
		this.uuid = uuid;
		this.pos = pos;
	}
	
	public static void encode(final SDrinkSoundPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeBlockPos(msg.pos);
		packetBuffer.writeLong(msg.uuid.getMostSignificantBits());
        packetBuffer.writeLong(msg.uuid.getLeastSignificantBits());
	}
	
	public static SDrinkSoundPacket decode(final PacketBuffer packetBuffer) {
		return new SDrinkSoundPacket(packetBuffer.readBlockPos(), new UUID(packetBuffer.readLong(), packetBuffer.readLong()));
	}
	
	@SuppressWarnings("deprecation")
	public static void handle(final SDrinkSoundPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			final UUID uuid = msg.uuid;
			final BlockPos pos = msg.pos;
			update(pos, uuid);
		}));
		context.setPacketHandled(true);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void update(final BlockPos pos, final UUID uuid) {
		if (uuid.equals(PlayerEntity.getUUID(Minecraft.getInstance().player.getGameProfile()))) {
			Minecraft.getInstance().player.world.playSound(pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.POTION).getDrinkSound(), SoundCategory.PLAYERS, 0.5F, Minecraft.getInstance().player.world.rand.nextFloat() * 0.1F + 0.9F, false);
			Minecraft.getInstance().player.swingArm(Hand.MAIN_HAND);
		}
	}
}
