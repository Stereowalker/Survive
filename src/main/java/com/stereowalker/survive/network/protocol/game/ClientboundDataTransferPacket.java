package com.stereowalker.survive.network.protocol.game;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.json.JsonHolder;
import com.stereowalker.survive.json.ArmorJsonHolder;
import com.stereowalker.survive.json.FluidJsonHolder;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.unionlib.network.protocol.game.ClientboundUnionPacket;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ClientboundDataTransferPacket extends ClientboundUnionPacket {
	private ResourceLocation stat;
	private JsonHolder settings;
	private boolean clear;

	public ClientboundDataTransferPacket(final ResourceLocation statIn, final JsonHolder settingsIn, final boolean clear) {
		super(Survive.getInstance().channel);
		this.stat = statIn;
		this.settings = settingsIn;
		this.clear = clear;
	}

	public ClientboundDataTransferPacket(FriendlyByteBuf byteBuf) {
		super(byteBuf, Survive.getInstance().channel);
		this.stat = byteBuf.readResourceLocation();
		String cl = byteBuf.readUtf();
		this.settings = JsonHolder.deserialize(byteBuf.readNbt(), JsonHolder.HOLD.get(cl));
		this.clear = byteBuf.readBoolean();
	}

	@Override
	public void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeResourceLocation(this.stat);
		byteBuf.writeUtf(this.settings.getClass().descriptorString());
		byteBuf.writeNbt(this.settings.serialize());
		byteBuf.writeBoolean(this.clear);
	}

	@Override
	public boolean handleOnClient(LocalPlayer sender) {
		if (settings instanceof ArmorJsonHolder) {
			if (this.clear) {
				Survive.getInstance().getLogger().info("Clearing Client Side Armor Data");
				DataMaps.Client.armor = ImmutableMap.of();
			}
			Map<ResourceLocation,ArmorJsonHolder> statMap = new HashMap<>();
			statMap.putAll(DataMaps.Client.armor);
			statMap.put(stat, (ArmorJsonHolder) settings);
			DataMaps.Client.armor = ImmutableMap.copyOf(statMap);
		}
		if (settings instanceof FluidJsonHolder) {
			if (this.clear) {
				Survive.getInstance().getLogger().info("Clearing Client Side Fluid Data");
				DataMaps.Client.fluid = ImmutableMap.of();
			}
			Map<ResourceLocation,FluidJsonHolder> statMap = new HashMap<>();
			statMap.putAll(DataMaps.Client.fluid);
			statMap.put(stat, (FluidJsonHolder) settings);
			DataMaps.Client.fluid = ImmutableMap.copyOf(statMap);
		}
		return true;
	}
}
