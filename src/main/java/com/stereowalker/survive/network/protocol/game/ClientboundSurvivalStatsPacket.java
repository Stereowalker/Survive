package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.unionlib.network.protocol.game.ClientboundUnionPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ClientboundSurvivalStatsPacket extends ClientboundUnionPacket {
	private CompoundTag stats;

	public ClientboundSurvivalStatsPacket(final CompoundTag statsIn) {
		super(Survive.getInstance().channel);
		this.stats = statsIn;
	}
	
	public ClientboundSurvivalStatsPacket(final ServerPlayer player){
		this(SurviveEntityStats.getModNBT(player));
	}

	public ClientboundSurvivalStatsPacket(FriendlyByteBuf byteBuf) {
		super(byteBuf, Survive.getInstance().channel);
		this.stats = byteBuf.readNbt();
	}

	@Override
	public void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeNbt(this.stats);
	}

	@Override
	public boolean handleOnClient(LocalPlayer sender) {
		SurviveEntityStats.setModNBT(this.stats, Minecraft.getInstance().player);
		return true;
	}
}
