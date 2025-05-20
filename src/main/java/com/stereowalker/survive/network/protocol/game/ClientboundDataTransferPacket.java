package com.stereowalker.survive.network.protocol.game;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.json.JsonHolder;
import com.stereowalker.survive.json.ArmorJsonHolder;
import com.stereowalker.survive.json.BiomeJsonHolder;
import com.stereowalker.survive.json.FluidJsonHolder;
import com.stereowalker.survive.json.FoodJsonHolder;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.unionlib.network.protocol.game.ClientboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

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
	public boolean runOnClient(Player sender) {
		if (settings instanceof ArmorJsonHolder armor) {
			if (this.clear) {
				Survive.getInstance().getLogger().info("Clearing Client Side Armor Data");
				DataMaps.Client.armor = ImmutableMap.of();
			}
			Map<ResourceLocation,ArmorJsonHolder> statMap = new HashMap<>();
			statMap.putAll(DataMaps.Client.armor);
			statMap.put(stat, armor);
			DataMaps.Client.armor = ImmutableMap.copyOf(statMap);
		}
		if (settings instanceof FluidJsonHolder fluid) {
			if (this.clear) {
				Survive.getInstance().getLogger().info("Clearing Client Side Fluid Data");
				DataMaps.Client.fluid = ImmutableMap.of();
			}
			Map<ResourceLocation,FluidJsonHolder> statMap = new HashMap<>();
			statMap.putAll(DataMaps.Client.fluid);
			statMap.put(stat, fluid);
			DataMaps.Client.fluid = ImmutableMap.copyOf(statMap);
		}
		if (settings instanceof BiomeJsonHolder biome) {
			if (this.clear) {
				Survive.getInstance().getLogger().info("Clearing Client Side Biome Data");
				DataMaps.Client.biome = ImmutableMap.of();
			}
			Map<ResourceLocation,BiomeJsonHolder> statMap = new HashMap<>();
			statMap.putAll(DataMaps.Client.biome);
			statMap.put(stat, biome);
			DataMaps.Client.biome = ImmutableMap.copyOf(statMap);
		}
		if (settings instanceof FoodJsonHolder consummable) {
			if (this.clear) {
				Survive.getInstance().getLogger().info("Clearing Client Side Consummable Data");
				DataMaps.Client.consummableItem = ImmutableMap.of();
			}
			Map<ResourceLocation,FoodJsonHolder> statMap = new HashMap<>();
			statMap.putAll(DataMaps.Client.consummableItem);
			statMap.put(stat, consummable);
			DataMaps.Client.consummableItem = ImmutableMap.copyOf(statMap);
		}
		return true;
	}

	public static ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "clientbound_data_transfer");
	@Override
	public ResourceLocation id() {
		return id;
	}
}
