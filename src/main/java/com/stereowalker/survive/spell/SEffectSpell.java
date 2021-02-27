package com.stereowalker.survive.spell;

import com.stereowalker.combat.api.spell.EffectSpell;
import com.stereowalker.combat.api.spell.Rank;
import com.stereowalker.combat.api.spell.SpellCategory;

import net.minecraft.potion.Effect;

public class SEffectSpell extends EffectSpell {

	public SEffectSpell(SpellCategory category, Rank tier, float cost, Effect effect, int amplifierIn, int castTime) {
		super(category, tier, cost, effect, amplifierIn, castTime);
	}

}
