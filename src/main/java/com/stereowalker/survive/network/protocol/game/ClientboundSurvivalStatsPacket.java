package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.unionlib.network.protocol.game.ClientboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ClientboundSurvivalStatsPacket extends ClientboundUnionPacket {
	private CompoundTag stats;

	public ClientboundSurvivalStatsPacket(final CompoundTag statsIn) {
		super(null);
		this.stats = statsIn;
	}
	
	public ClientboundSurvivalStatsPacket(final ServerPlayer player){
		this(SurviveEntityStats.getModNBT(player));
	}

	public ClientboundSurvivalStatsPacket(RegistryFriendlyByteBuf byteBuf) {
		super(byteBuf);
		this.stats = byteBuf.readNbt();
	}

	@Override
	public void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeNbt(this.stats);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean handleOnClient(LocalPlayer sender) {
		SurviveEntityStats.setModNBT(this.stats, Minecraft.getInstance().player);
		return true;
	}

	public static ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "clientbound_survival_stats");
	@Override
	public ResourceLocation id() {
		return id;
	}
}
