package com.stereowalker.survive.network.protocol.game;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ClientboundDrinkSoundPacket {
	private BlockPos pos;
	private UUID uuid;
	
	public ClientboundDrinkSoundPacket(
			final BlockPos pos,
			final UUID uuid) {
		this.uuid = uuid;
		this.pos = pos;
	}
	
	public static void encode(final ClientboundDrinkSoundPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeBlockPos(msg.pos);
		packetBuffer.writeLong(msg.uuid.getMostSignificantBits());
        packetBuffer.writeLong(msg.uuid.getLeastSignificantBits());
	}
	
	public static ClientboundDrinkSoundPacket decode(final FriendlyByteBuf packetBuffer) {
		return new ClientboundDrinkSoundPacket(packetBuffer.readBlockPos(), new UUID(packetBuffer.readLong(), packetBuffer.readLong()));
	}
	
	@SuppressWarnings("deprecation")
	public static void handle(final ClientboundDrinkSoundPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
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
		if (uuid.equals(Player.createPlayerUUID(Minecraft.getInstance().player.getGameProfile()))) {
			Minecraft.getInstance().player.level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.POTION).getDrinkingSound(), SoundSource.PLAYERS, 0.5F, Minecraft.getInstance().player.level.random.nextFloat() * 0.1F + 0.9F, false);
			Minecraft.getInstance().player.swing(InteractionHand.MAIN_HAND);
		}
	}
}
