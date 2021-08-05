package com.stereowalker.survive.network.server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.stereowalker.survive.DataMaps;
import com.stereowalker.survive.util.data.ArmorData;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class SArmorDataTransferPacket {
	private ResourceLocation stat;
	private ArmorData settings;
	private boolean clear;

	public SArmorDataTransferPacket(final ResourceLocation statIn, final ArmorData settingsIn, final boolean clear) {
		this.stat = statIn;
		this.settings = settingsIn;
		this.clear = clear;
	}

	public static void encode(final SArmorDataTransferPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeResourceLocation(msg.stat);
		packetBuffer.writeCompoundTag(msg.settings.serialize());
		packetBuffer.writeBoolean(msg.clear);
	}

	public static SArmorDataTransferPacket decode(final PacketBuffer packetBuffer) {
		return new SArmorDataTransferPacket(packetBuffer.readResourceLocation(), new ArmorData(packetBuffer.readCompoundTag()), packetBuffer.readBoolean());
	}

	@SuppressWarnings("deprecation")
	public static void handle(final SArmorDataTransferPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			final ResourceLocation stat = msg.stat;
			final ArmorData settings = msg.settings;
			if (msg.clear) {
				clear();
			}
			update(stat, settings);
		}));
		context.setPacketHandled(true);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void clear() {
		DataMaps.Client.armor = ImmutableMap.of();
	}

	@OnlyIn(Dist.CLIENT)
	public static void update(final ResourceLocation stat, final ArmorData settings) {
		Map<ResourceLocation,ArmorData> statMap = new HashMap<>();
		statMap.putAll(DataMaps.Client.armor);
		statMap.put(stat, settings);
		DataMaps.Client.armor = ImmutableMap.copyOf(statMap);
	}
}
