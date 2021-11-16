package com.stereowalker.survive.client.gui;

import com.stereowalker.survive.world.effect.SEffects;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum SurviveHeartType {
	CONTAINER(0, false, true),
	NORMAL(2, true, true),
	POISIONED(4, true, true),
	WITHERED(6, true, true),
	ABSORBING(8, false, true),
	FROZEN(9, false, true),
	HYPO(0, false, false),
	HYPER(2, false, false);

	private final int index;
	private final boolean canBlink;
	private final boolean useVanilla;

	private SurviveHeartType(int index, boolean p_168730_, boolean useVanilla) {
		this.index = index;
		this.canBlink = p_168730_;
		this.useVanilla = useVanilla;
	}

	public int getX(boolean p_168735_, boolean p_168736_) {
		int i;
		if (this == CONTAINER) {
			i = p_168736_ ? 1 : 0;
		} else {
			int j = p_168735_ ? 1 : 0;
			int k = this.canBlink && p_168736_ ? 2 : 0;
			i = j + k;
		}

		return 16 + (this.index * 2 + i) * 9;
	}

	public boolean usesVanilla() {
		return useVanilla;
	}

	public static SurviveHeartType forPlayer(Player p_168733_) {
		SurviveHeartType gui$hearttype;
		if (p_168733_.hasEffect(MobEffects.POISON)) {
			gui$hearttype = POISIONED;
		} else if (p_168733_.hasEffect(MobEffects.WITHER)) {
			gui$hearttype = WITHERED;
		} else if (p_168733_.isFullyFrozen()) {
			gui$hearttype = FROZEN;
		} else if (p_168733_.hasEffect(SEffects.DEPRECIATED_HYPERTHERMIA)) {
			gui$hearttype = HYPER;
		} else if (p_168733_.hasEffect(SEffects.DEPRECIATED_HYPOTHERMIA)) {
			gui$hearttype = HYPO;
		} else {
			gui$hearttype = NORMAL;
		}

		return gui$hearttype;
	}
}
