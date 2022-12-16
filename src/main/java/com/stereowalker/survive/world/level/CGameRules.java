package com.stereowalker.survive.world.level;

import net.minecraft.world.level.GameRules;

public class CGameRules extends GameRules {
	public static GameRules.Key<GameRules.BooleanValue> RULE_DO_THIRST_REDUCTION;

	public static void init(){
		CGameRules.RULE_DO_THIRST_REDUCTION = GameRules.register("doThirstReduction", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
	}
}
