package com.stereowalker.survive.spell;

import net.minecraftforge.eventbus.api.IEventBus;

//
//import com.stereowalker.combat.api.spell.Rank;
//import com.stereowalker.combat.api.spell.Spell;
//import com.stereowalker.combat.api.spell.SpellCategory;
//import com.stereowalker.survive.Survive;
//import com.stereowalker.survive.potion.SEffects;
//
//import net.minecraftforge.eventbus.api.IEventBus;
//import net.minecraftforge.registries.DeferredRegister;
//
public class SSpells {
////	public static List<Spell> SPELLS = new ArrayList<Spell>();
//	public static final DeferredRegister<Spell> SPELL_REGISTRY = DeferredRegister.create(Spell.class, Survive.MOD_ID);
//	////----------------------------------------[Fire]----------------------------------------\\
//	//	//Basic
//	//	//Novice
//		public static final Spell HEAT = register("heat", new SEffectSpell(SpellCategory.FIRE, Rank.NOVICE, 0.4f, SEffects.HEATED, 0, 20));
//	//	//Apprentice
//	//	//God
//	////----------------------------------------[Water]----------------------------------------\\
//	//	//Novice
//		public static final Spell CHILL = register("chill", new SEffectSpell(SpellCategory.WATER, Rank.NOVICE, 0.4f, SEffects.CHILLED, 0, 20));
//	//	//Apprentice
//	//	//God
//	////----------------------------------------[Lightning]----------------------------------------\\
//	//	//Novice
//	//	//Apprentice
//	//	//Advanced
//	//	//Master
//	//	//God
//	////----------------------------------------[Restoration]----------------------------------------\\
//	//	//Novice
//	//	//Apprentice
//	//	//Advanced
//	////----------------------------------------[Conjuration]----------------------------------------\\
//	//	//Basic
//	//	//Novice
//	//	//Apprentice
//	//	//Advanced
//	////----------------------------------------[Earth]----------------------------------------\\
//	//	//Basic
//	//	//Novice
//	//	//Apprentice
//	//	//Advanced
//	//	//Master
//	//	//God
//	////----------------------------------------[Wind]----------------------------------------\\
//	//	//Basic
//	//	//Novice
//	////----------------------------------------[Mind]----------------------------------------\\
//	//	//Mind
//	////----------------------------------------[Nature]----------------------------------------\\
//	//	//Novice
//	//	//Apprentice
//	//	//Master
//	////----------------------------------------[Space]----------------------------------------\\
//	//	//Novice
//	//	//Advanced
//	////----------------------------------------[Enhancement]----------------------------------------\\
//	//	//Basic
//	//	//Novice
//	//	//Apprentice
//	//	//God
//	//	//----------------------------------------[Blood]----------------------------------------\\
//	//	
////	public static Spell register(String name, Spell spell) {
////		spell.setRegistryName(Survive.location(name));
////		SPELLS.add(spell);
////		return spell;
////	}
////
////	public static void registerAll(IForgeRegistry<Spell> registry) {
////		if (Survive.isCombatLoaded) {
////			for(Spell spell : SPELLS) {
////				registry.register(spell);
////				Survive.debug("Spell: \""+spell.getRegistryName().toString()+"\" registered");
////			}
////			Survive.debug("All Spells Registered");
////		}
////	}
//	
//	public static Spell register(String name, Spell spell) {
//		SPELL_REGISTRY.register(name, () -> spell);
//		return spell;
//	}
//	
	public static void registerAll(IEventBus eventBus) {
//		SPELL_REGISTRY.register(eventBus);
	}
}
