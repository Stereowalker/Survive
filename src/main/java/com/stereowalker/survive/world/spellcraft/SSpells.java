package com.stereowalker.survive.world.spellcraft;

import com.stereowalker.combat.api.world.spellcraft.EffectSpell;
import com.stereowalker.combat.api.world.spellcraft.Rank;
import com.stereowalker.combat.api.world.spellcraft.Spell;
import com.stereowalker.combat.api.world.spellcraft.SpellCategory;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.effect.SMobEffects;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class SSpells {
	public static final DeferredRegister<Spell> SPELL_REGISTRY = DeferredRegister.create(Spell.class, Survive.MOD_ID);
	public static final Spell HEAT = register("heat", new EffectSpell(SpellCategory.FIRE, Rank.NOVICE, 0.4f, SMobEffects.HEATED, 0, 20));
	public static final Spell CHILL = register("chill", new EffectSpell(SpellCategory.WATER, Rank.NOVICE, 0.4f, SMobEffects.CHILLED, 0, 20));

	public static Spell register(String name, Spell spell) {
		SPELL_REGISTRY.register(name, () -> spell);
		return spell;
	}

	public static void registerAll(IEventBus eventBus) {
		SPELL_REGISTRY.register(eventBus);
	}
}
