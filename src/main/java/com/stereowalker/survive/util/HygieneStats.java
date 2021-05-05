package com.stereowalker.survive.util;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class HygieneStats {
	private int uncleanLevel = 10;
	private int hygieneTimer;

	public HygieneStats() {
		this.uncleanLevel = 20;
	}
	
	/**
	 * Attempts to clean the player
	 * @param cleanLevelIn - Caps at zero
	 */
	public void clean(int cleanLevelIn) {
		this.uncleanLevel = Math.max(cleanLevelIn - this.uncleanLevel, 0);
	}
	
	/**
	 * Attempts to dirty the player
	 * @param cleanLevelIn - Caps at zero
	 */
	public void dirty(int dirtyLevelIn) {
		this.uncleanLevel = Math.max(dirtyLevelIn + this.uncleanLevel, 0);
	}

	/**
	 * Handles the water game logic.
	 */
	public void tick(PlayerEntity player) {
//		Difficulty difficulty = player.world.getDifficulty();
//		this.prevWaterLevel = this.waterLevel;
//		if (this.waterExhaustionLevel > 4.0F) {
//			this.waterExhaustionLevel -= 4.0F;
//			if (this.waterHydrationLevel > 0.0F) {
//				this.waterHydrationLevel = Math.max(this.waterHydrationLevel - 1.0F, 0.0F);
//			} else if (difficulty != Difficulty.PEACEFUL) {
//				this.waterLevel = Math.max(this.waterLevel - 1, 0);
//			}
//		}

		boolean flag = /*player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)*/ Config.enable_Hygiene;
		if (flag/*
				 * && this.waterHydrationLevel > 0.0F && player.shouldHeal() && this.waterLevel
				 * >= 20
				 */) {
			++this.hygieneTimer;
			if (this.hygieneTimer >= 400) {
//				float f = Math.min(this.waterHydrationLevel, 6.0F);
//				player.heal(f / 12.0F);
//				this.addExhaustion(f);
				this.uncleanLevel+=1.0F;
				this.hygieneTimer = 0;
			}
//		} else if (flag && this.waterLevel >= 18 && player.shouldHeal()) {
//			++this.waterTimer;
//			if (this.waterTimer >= 80) {
//				player.heal(0.5F);
//				this.addExhaustion(6.0F);
//				this.waterTimer = 0;
//			}
//		} else if (this.waterLevel <= 0) {
//			++this.waterTimer;
//			if (this.waterTimer >= 80) {
//				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
//					player.attackEntityFrom(SDamageSource.DEHYDRATE, 1.0F);
//				}
//
//				this.waterTimer = 0;
//			}
		} else {
			this.hygieneTimer = 0;
		}

	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundNBT compound) {
		if (compound.contains("uncleanLevel", 99)) {
			this.uncleanLevel = compound.getInt("uncleanLevel");
			this.hygieneTimer = compound.getInt("hygieneTimer");
		}

	}

	/**
	 * Writes the water data for the player.
	 */
	public void write(CompoundNBT compound) {
		compound.putFloat("uncleanLevel", this.uncleanLevel);
		compound.putInt("hygieneTimer", this.hygieneTimer);
	}

	/**
	 * Get the player's water level.
	 */
	public int getUncleanLevel() {
		return this.uncleanLevel;
	}

	/**
	 * Get whether the player should take a shower.
	 */
	public boolean needsABath() {
		return this.uncleanLevel > 25;
	}

	/////-----------EVENTS-----------/////

	@SubscribeEvent
	public static void regulateHygiene(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
			if (Config.enable_Hygiene) {
				HygieneStats stats = SurviveEntityStats.getHygieneStats(player);
				if (player.isWet() && player.ticksExisted % 20 == 0) {
					stats.clean(1);
				}
				stats.tick(player);
				SurviveEntityStats.setHygieneStats(player, stats);
			}
		}
		if (event.getEntityLiving() != null && event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ClientPlayerEntity) {
			ClientPlayerEntity player = (ClientPlayerEntity)event.getEntityLiving();
		}
	}
}
