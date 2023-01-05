package com.stereowalker.survive.world.item.alchemy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.effect.SMobEffects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;

public class SPotions {
	public static final Map<ResourceLocation, Potion> POTIONS = new HashMap<ResourceLocation, Potion>();

	public static final Potion PURIFIED_WATER = register("purified_water", new Potion());
	public static final Potion HEAT_RESISTANCE = register("heat_resistance", new Potion( new MobEffectInstance(SMobEffects.HEAT_RESISTANCE, 3600)));
	public static final Potion LONG_HEAT_RESISTANCE = register("long_heat_resistance", new Potion("heat_resistance", new MobEffectInstance(SMobEffects.HEAT_RESISTANCE, 9600)));
	public static final Potion STRONG_HEAT_RESISTANCE = register("strong_heat_resistance", new Potion("heat_resistance", new MobEffectInstance(SMobEffects.HEAT_RESISTANCE, 1800, 1)));
	public static final Potion COLD_RESISTANCE = register("cold_resistance", new Potion( new MobEffectInstance(SMobEffects.COLD_RESISTANCE, 3600)));
	public static final Potion LONG_COLD_RESISTANCE = register("long_cold_resistance", new Potion("cold_resistance", new MobEffectInstance(SMobEffects.COLD_RESISTANCE, 9600)));
	public static final Potion STRONG_COLD_RESISTANCE = register("strong_cold_resistance", new Potion("cold_resistance", new MobEffectInstance(SMobEffects.COLD_RESISTANCE, 1800, 1)));

	public static Potion register(String name, Potion potion) {
		POTIONS.put(Survive.getInstance().location(name), potion);
		return potion;
	}

	public static void registerAll(RegisterHelper<Potion> registry) {
		for(Entry<ResourceLocation, Potion> potion : POTIONS.entrySet()) {
			registry.register(potion.getKey(), potion.getValue());
			Survive.getInstance().debug("Potion: \""+potion.getValue().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Potions Registered");
	}

}
