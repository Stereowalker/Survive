package com.stereowalker.survive.client.gui;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRoastedEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum SurviveHeartType {
	CONTAINER(
			new ResourceLocation("hud/heart/container"),
			new ResourceLocation("hud/heart/container_blinking"),
			new ResourceLocation("hud/heart/container"),
			new ResourceLocation("hud/heart/container_blinking"),
			new ResourceLocation("hud/heart/container_hardcore"),
			new ResourceLocation("hud/heart/container_hardcore_blinking"),
			new ResourceLocation("hud/heart/container_hardcore"),
			new ResourceLocation("hud/heart/container_hardcore_blinking")
			),
	NORMAL(
			new ResourceLocation("hud/heart/full"),
			new ResourceLocation("hud/heart/full_blinking"),
			new ResourceLocation("hud/heart/half"),
			new ResourceLocation("hud/heart/half_blinking"),
			new ResourceLocation("hud/heart/hardcore_full"),
			new ResourceLocation("hud/heart/hardcore_full_blinking"),
			new ResourceLocation("hud/heart/hardcore_half"),
			new ResourceLocation("hud/heart/hardcore_half_blinking")
			),
	POISIONED(
			new ResourceLocation("hud/heart/poisoned_full"),
			new ResourceLocation("hud/heart/poisoned_full_blinking"),
			new ResourceLocation("hud/heart/poisoned_half"),
			new ResourceLocation("hud/heart/poisoned_half_blinking"),
			new ResourceLocation("hud/heart/poisoned_hardcore_full"),
			new ResourceLocation("hud/heart/poisoned_hardcore_full_blinking"),
			new ResourceLocation("hud/heart/poisoned_hardcore_half"),
			new ResourceLocation("hud/heart/poisoned_hardcore_half_blinking")
			),
	WITHERED(
			new ResourceLocation("hud/heart/withered_full"),
			new ResourceLocation("hud/heart/withered_full_blinking"),
			new ResourceLocation("hud/heart/withered_half"),
			new ResourceLocation("hud/heart/withered_half_blinking"),
			new ResourceLocation("hud/heart/withered_hardcore_full"),
			new ResourceLocation("hud/heart/withered_hardcore_full_blinking"),
			new ResourceLocation("hud/heart/withered_hardcore_half"),
			new ResourceLocation("hud/heart/withered_hardcore_half_blinking")
			),
	ABSORBING(
			new ResourceLocation("hud/heart/absorbing_full"),
			new ResourceLocation("hud/heart/absorbing_full_blinking"),
			new ResourceLocation("hud/heart/absorbing_half"),
			new ResourceLocation("hud/heart/absorbing_half_blinking"),
			new ResourceLocation("hud/heart/absorbing_hardcore_full"),
			new ResourceLocation("hud/heart/absorbing_hardcore_full_blinking"),
			new ResourceLocation("hud/heart/absorbing_hardcore_half"),
			new ResourceLocation("hud/heart/absorbing_hardcore_half_blinking")
			),
	FROZEN(
			new ResourceLocation("hud/heart/frozen_full"),
			new ResourceLocation("hud/heart/frozen_full_blinking"),
			new ResourceLocation("hud/heart/frozen_half"),
			new ResourceLocation("hud/heart/frozen_half_blinking"),
			new ResourceLocation("hud/heart/frozen_hardcore_full"),
			new ResourceLocation("hud/heart/frozen_hardcore_full_blinking"),
			new ResourceLocation("hud/heart/frozen_hardcore_half"),
			new ResourceLocation("hud/heart/frozen_hardcore_half_blinking")
			),
	ROASTED(
			new ResourceLocation(Survive.MOD_ID, "hud/heart/roasted_full"),
			new ResourceLocation(Survive.MOD_ID, "hud/heart/roasted_full_blinking"),
			new ResourceLocation(Survive.MOD_ID, "hud/heart/roasted_half"),
			new ResourceLocation(Survive.MOD_ID, "hud/heart/roasted_half_blinking"),
			new ResourceLocation(Survive.MOD_ID, "hud/heart/roasted_hardcore_full"),
			new ResourceLocation(Survive.MOD_ID, "hud/heart/roasted_hardcore_full_blinking"),
			new ResourceLocation(Survive.MOD_ID, "hud/heart/roasted_hardcore_half"),
			new ResourceLocation(Survive.MOD_ID, "hud/heart/roasted_hardcore_half_blinking")
			);

	private final ResourceLocation full;
	private final ResourceLocation fullBlinking;
	private final ResourceLocation half;
	private final ResourceLocation halfBlinking;
	private final ResourceLocation hardcoreFull;
	private final ResourceLocation hardcoreFullBlinking;
	private final ResourceLocation hardcoreHalf;
	private final ResourceLocation hardcoreHalfBlinking;

	private SurviveHeartType(
			final ResourceLocation pFull,
			final ResourceLocation pFullBlinking,
			final ResourceLocation pHalf,
			final ResourceLocation pHalfBlinking,
			final ResourceLocation pHardcoreFull,
			final ResourceLocation pHardcoreBlinking,
			final ResourceLocation pHardcoreHalf,
			final ResourceLocation pHardcoreHalfBlinking
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

	public ResourceLocation getSprite(boolean pHardcore, boolean pHalfHeart, boolean pBlinking) {
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
