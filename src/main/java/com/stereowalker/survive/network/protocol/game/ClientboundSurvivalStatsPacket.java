package com.stereowalker.survive.network.protocol.game;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.unionlib.network.protocol.game.ClientboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ClientboundSurvivalStatsPacket extends ClientboundUnionPacket {
	private CompoundTag stats;
	private boolean legacyStats;

	public ClientboundSurvivalStatsPacket(final CompoundTag statsIn, final boolean legacyStats) {
		super(null);
		this.stats = statsIn;
		this.legacyStats = legacyStats;
	}
	
	public ClientboundSurvivalStatsPacket(final ServerPlayer player, boolean legacyStats){
		this(legacyStats ? SurviveEntityStats.getModNBT(player) : tag((IRealisticEntity)player), legacyStats);
	}
	
	public static CompoundTag tag(IRealisticEntity player) {
		CompoundTag surviveData = new CompoundTag();
		surviveData.put("temperature", player.temperatureData().write(true));
		surviveData.put("wellbeing", player.wellbeingData().write(true));
		surviveData.put("nutrition", player.nutritionData().write(true));
		surviveData.put("hygiene", player.hygieneData().write(true));
		surviveData.put("stamina", player.staminaData().write(true));
		surviveData.put("sleep", player.sleepData().write(true));
		return surviveData;
	}

	public ClientboundSurvivalStatsPacket(RegistryFriendlyByteBuf byteBuf) {
		super(byteBuf);
		this.stats = byteBuf.readNbt();
		this.legacyStats = byteBuf.readBoolean();
	}

	@Override
	public void encode(final FriendlyByteBuf byteBuf) {
		byteBuf.writeNbt(this.stats);
		byteBuf.writeBoolean(this.legacyStats);
	}

	@Override
	public boolean runOnClient(Player sender) {
		if (this.legacyStats) {
			SurviveEntityStats.setModNBT(this.stats, sender);
		} else {
			IRealisticEntity player = (IRealisticEntity)sender;
			if (this.stats.contains("temperature", 10)) player.temperatureData().read(this.stats.getCompound("temperature"));
			if (this.stats.contains("wellbeing", 10)) player.wellbeingData().read(this.stats.getCompound("wellbeing"));
			if (this.stats.contains("nutrition", 10)) player.nutritionData().read(this.stats.getCompound("nutrition"));
			if (this.stats.contains("hygiene", 10)) player.hygieneData().read(this.stats.getCompound("hygiene"));
			if (this.stats.contains("stamina", 10)) player.staminaData().read(this.stats.getCompound("stamina"));
			if (this.stats.contains("sleep", 10)) player.sleepData().read(this.stats.getCompound("sleep"));
		}
		return true;
	}

	public static ResourceLocation id = VersionHelper.toLoc(Survive.MOD_ID, "clientbound_survival_stats");
	@Override
	public ResourceLocation id() {
		return id;
	}
}
