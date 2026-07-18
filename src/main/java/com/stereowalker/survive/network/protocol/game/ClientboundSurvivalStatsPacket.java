package com.stereowalker.survive.network.protocol.game;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.unionlib.network.protocol.game.ClientboundUnionPacket;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;

public class ClientboundSurvivalStatsPacket extends ClientboundUnionPacket {
	private static final Logger LOGGER = LogUtils.getLogger();
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
		surviveData.put("water", player.waterData().write(true));
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
			try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
				ValueInput tag = TagValueInput.create(reporter, sender.level().registryAccess(), stats);
				if (this.stats.getCompound("temperature").isPresent()) player.temperatureData().read(tag.childOrEmpty("temperature"));
				if (this.stats.getCompound("wellbeing").isPresent()) player.wellbeingData().read(tag.childOrEmpty("wellbeing"));
				if (this.stats.getCompound("nutrition").isPresent()) player.nutritionData().read(tag.childOrEmpty("nutrition"));
				if (this.stats.getCompound("hygiene").isPresent()) player.hygieneData().read(tag.childOrEmpty("hygiene"));
				if (this.stats.getCompound("stamina").isPresent()) player.staminaData().read(tag.childOrEmpty("stamina"));
				if (this.stats.getCompound("sleep").isPresent()) player.sleepData().read(tag.childOrEmpty("sleep"));
				if (this.stats.getCompound("water").isPresent()) player.waterData().read(tag.childOrEmpty("water"));
			}
		}
		return true;
	}

	public static Identifier id = VersionHelper.toLoc(Survive.MOD_ID, "clientbound_survival_stats");
	@Override
	public Identifier id() {
		return id;
	}
}
