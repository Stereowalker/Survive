package com.stereowalker.survive.client.gui;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRoastedEntity;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public enum SurviveHeartType {
	CONTAINER(
			VersionHelper.toLoc("hud/heart/container"),
			VersionHelper.toLoc("hud/heart/container_blinking"),
			VersionHelper.toLoc("hud/heart/container"),
			VersionHelper.toLoc("hud/heart/container_blinking"),
			VersionHelper.toLoc("hud/heart/container_hardcore"),
			VersionHelper.toLoc("hud/heart/container_hardcore_blinking"),
			VersionHelper.toLoc("hud/heart/container_hardcore"),
			VersionHelper.toLoc("hud/heart/container_hardcore_blinking")
			),
	NORMAL(
			VersionHelper.toLoc("hud/heart/full"),
			VersionHelper.toLoc("hud/heart/full_blinking"),
			VersionHelper.toLoc("hud/heart/half"),
			VersionHelper.toLoc("hud/heart/half_blinking"),
			VersionHelper.toLoc("hud/heart/hardcore_full"),
			VersionHelper.toLoc("hud/heart/hardcore_full_blinking"),
			VersionHelper.toLoc("hud/heart/hardcore_half"),
			VersionHelper.toLoc("hud/heart/hardcore_half_blinking")
			),
	POISIONED(
			VersionHelper.toLoc("hud/heart/poisoned_full"),
			VersionHelper.toLoc("hud/heart/poisoned_full_blinking"),
			VersionHelper.toLoc("hud/heart/poisoned_half"),
			VersionHelper.toLoc("hud/heart/poisoned_half_blinking"),
			VersionHelper.toLoc("hud/heart/poisoned_hardcore_full"),
			VersionHelper.toLoc("hud/heart/poisoned_hardcore_full_blinking"),
			VersionHelper.toLoc("hud/heart/poisoned_hardcore_half"),
			VersionHelper.toLoc("hud/heart/poisoned_hardcore_half_blinking")
			),
	WITHERED(
			VersionHelper.toLoc("hud/heart/withered_full"),
			VersionHelper.toLoc("hud/heart/withered_full_blinking"),
			VersionHelper.toLoc("hud/heart/withered_half"),
			VersionHelper.toLoc("hud/heart/withered_half_blinking"),
			VersionHelper.toLoc("hud/heart/withered_hardcore_full"),
			VersionHelper.toLoc("hud/heart/withered_hardcore_full_blinking"),
			VersionHelper.toLoc("hud/heart/withered_hardcore_half"),
			VersionHelper.toLoc("hud/heart/withered_hardcore_half_blinking")
			),
	ABSORBING(
			VersionHelper.toLoc("hud/heart/absorbing_full"),
			VersionHelper.toLoc("hud/heart/absorbing_full_blinking"),
			VersionHelper.toLoc("hud/heart/absorbing_half"),
			VersionHelper.toLoc("hud/heart/absorbing_half_blinking"),
			VersionHelper.toLoc("hud/heart/absorbing_hardcore_full"),
			VersionHelper.toLoc("hud/heart/absorbing_hardcore_full_blinking"),
			VersionHelper.toLoc("hud/heart/absorbing_hardcore_half"),
			VersionHelper.toLoc("hud/heart/absorbing_hardcore_half_blinking")
			),
	FROZEN(
			VersionHelper.toLoc("hud/heart/frozen_full"),
			VersionHelper.toLoc("hud/heart/frozen_full_blinking"),
			VersionHelper.toLoc("hud/heart/frozen_half"),
			VersionHelper.toLoc("hud/heart/frozen_half_blinking"),
			VersionHelper.toLoc("hud/heart/frozen_hardcore_full"),
			VersionHelper.toLoc("hud/heart/frozen_hardcore_full_blinking"),
			VersionHelper.toLoc("hud/heart/frozen_hardcore_half"),
			VersionHelper.toLoc("hud/heart/frozen_hardcore_half_blinking")
			),
	ROASTED(
			VersionHelper.toLoc(Survive.MOD_ID, "hud/heart/roasted_full"),
			VersionHelper.toLoc(Survive.MOD_ID, "hud/heart/roasted_full_blinking"),
			VersionHelper.toLoc(Survive.MOD_ID, "hud/heart/roasted_half"),
			VersionHelper.toLoc(Survive.MOD_ID, "hud/heart/roasted_half_blinking"),
			VersionHelper.toLoc(Survive.MOD_ID, "hud/heart/roasted_hardcore_full"),
			VersionHelper.toLoc(Survive.MOD_ID, "hud/heart/roasted_hardcore_full_blinking"),
			VersionHelper.toLoc(Survive.MOD_ID, "hud/heart/roasted_hardcore_half"),
			VersionHelper.toLoc(Survive.MOD_ID, "hud/heart/roasted_hardcore_half_blinking")
			);

	private final Identifier full;
	private final Identifier fullBlinking;
	private final Identifier half;
	private final Identifier halfBlinking;
	private final Identifier hardcoreFull;
	private final Identifier hardcoreFullBlinking;
	private final Identifier hardcoreHalf;
	private final Identifier hardcoreHalfBlinking;

	private SurviveHeartType(
			final Identifier pFull,
			final Identifier pFullBlinking,
			final Identifier pHalf,
			final Identifier pHalfBlinking,
			final Identifier pHardcoreFull,
			final Identifier pHardcoreBlinking,
			final Identifier pHardcoreHalf,
			final Identifier pHardcoreHalfBlinking
			) {
		this.full = pFull;
		this.fullBlinking = pFullBlinking;
		this.half = pHalf;
		this.halfBlinking = pHalfBlinking;
		this.hardcoreFull = pHardcoreFull;
		this.hardcoreFullBlinking = pHardcoreBlinking;
		this.hardcoreHalf = pHardcoreHalf;
		this.hardcoreHalfBlinking = pHardcoreHalfBlinking;
	}

	public Identifier getSprite(boolean pHardcore, boolean pHalfHeart, boolean pBlinking) {
		if (!pHardcore) {
			if (pHalfHeart) {
				return pBlinking ? this.halfBlinking : this.half;
			} else {
				return pBlinking ? this.fullBlinking : this.full;
			}
		} else if (pHalfHeart) {
			return pBlinking ? this.hardcoreHalfBlinking : this.hardcoreHalf;
		} else {
			return pBlinking ? this.hardcoreFullBlinking : this.hardcoreFull;
		}
	}

	public static SurviveHeartType forPlayer(Player p_168733_) {
		SurviveHeartType gui$hearttype;
		if (p_168733_.hasEffect(MobEffects.POISON)) {
			gui$hearttype = POISIONED;
		} else if (p_168733_.hasEffect(MobEffects.WITHER)) {
			gui$hearttype = WITHERED;
		} else if (p_168733_.isFullyFrozen()) {
			gui$hearttype = FROZEN;
		} else if (((IRoastedEntity)p_168733_).isFullyRoasted()) {
			gui$hearttype = ROASTED;
		} else {
			gui$hearttype = NORMAL;
		}

		return gui$hearttype;
	}
}
