package com.stereowalker.survive.world.item.alchemy;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.effect.SMobEffects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.IForgeRegistry;

public class SPotions {
	public static List<Potion> POTIONS = new ArrayList<Potion>();
	
	public static final Potion HEAT_RESISTANCE = register("heat_resistance", new Potion( new MobEffectInstance(SMobEffects.HEAT_RESISTANCE, 3600)));
	public static final Potion LONG_HEAT_RESISTANCE = register("long_heat_resistance", new Potion("heat_resistance", new MobEffectInstance(SMobEffects.HEAT_RESISTANCE, 9600)));
	public static final Potion STRONG_HEAT_RESISTANCE = register("strong_heat_resistance", new Potion("heat_resistance", new MobEffectInstance(SMobEffects.HEAT_RESISTANCE, 1800, 1)));
	public static final Potion COLD_RESISTANCE = register("cold_resistance", new Potion( new MobEffectInstance(SMobEffects.COLD_RESISTANCE, 3600)));
	public static final Potion LONG_COLD_RESISTANCE = register("long_cold_resistance", new Potion("cold_resistance", new MobEffectInstance(SMobEffects.COLD_RESISTANCE, 9600)));
	public static final Potion STRONG_COLD_RESISTANCE = register("strong_cold_resistance", new Potion("cold_resistance", new MobEffectInstance(SMobEffects.COLD_RESISTANCE, 1800, 1)));
	
	public static Potion register(String name, Potion potion) {
		potion.setRegistryName(Survive.getInstance().location(name));
		POTIONS.add(potion);
		return potion;
	}
	
	public static void registerAll(IForgeRegistry<Potion> registry) {
		for(Potion potion : POTIONS) {
			registry.register(potion);
			Survive.getInstance().debug("Potion: \""+potion.getRegistryName().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Potions Registered");
	}

}
