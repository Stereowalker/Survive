package com.stereowalker.survive.network.protocol.game;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.json.ArmorJsonHolder;
import com.stereowalker.survive.world.DataMaps;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ClientboundArmorDataTransferPacket {
	private ResourceLocation stat;
	private ArmorJsonHolder settings;
	private boolean clear;

	public ClientboundArmorDataTransferPacket(final ResourceLocation statIn, final ArmorJsonHolder settingsIn, final boolean clear) {
		this.stat = statIn;
		this.settings = settingsIn;
		this.clear = clear;
	}

	public static void encode(final ClientboundArmorDataTransferPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeResourceLocation(msg.stat);
		packetBuffer.writeNbt(msg.settings.serialize());
		packetBuffer.writeBoolean(msg.clear);
	}

	public static ClientboundArmorDataTransferPacket decode(final FriendlyByteBuf packetBuffer) {
		return new ClientboundArmorDataTransferPacket(packetBuffer.readResourceLocation(), new ArmorJsonHolder(packetBuffer.readNbt()), packetBuffer.readBoolean());
	}

	@SuppressWarnings("deprecation")
	public static void handle(final ClientboundArmorDataTransferPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			final ResourceLocation stat = msg.stat;
			final ArmorJsonHolder settings = msg.settings;
			if (msg.clear) {
				clear();
			}
			update(stat, settings);
		}));
		context.setPacketHandled(true);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void clear() {
		Survive.getInstance().getLogger().info("Clearing Client Side Armor Data");
		DataMaps.Client.armor = ImmutableMap.of();
	}

	@OnlyIn(Dist.CLIENT)
	public static void update(final ResourceLocation stat, final ArmorJsonHolder settings) {
		Map<ResourceLocation,ArmorJsonHolder> statMap = new HashMap<>();
		statMap.putAll(DataMaps.Client.armor);
		statMap.put(stat, settings);
		DataMaps.Client.armor = ImmutableMap.copyOf(statMap);
	}
}
