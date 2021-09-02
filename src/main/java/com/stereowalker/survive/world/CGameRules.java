package com.stereowalker.survive.world;

import net.minecraft.world.GameRules;

public class CGameRules {
	public static GameRules.RuleKey<GameRules.BooleanValue> DO_THIRST_REDUCTION;

	public static void init(){
		CGameRules.DO_THIRST_REDUCTION = GameRules.register("doThirstReduction", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
	}
}
