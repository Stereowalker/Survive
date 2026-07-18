package com.stereowalker.survive.world.item.alchemy;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.unionlib.core.registries.Housing;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SPotions {
	@RegistryObject("purified_water")
	public static final Housing<Potion> PURIFIED_WATER = Housing.create(() -> new Potion("purified_water"));
	@RegistryObject("heat_resistance")
	public static final Housing<Potion> HEAT_RESISTANCE = Housing.create(() -> new Potion("heat_resistance", new MobEffectInstance(SMobEffects.HEAT_RESISTANCE.holder(), 3600)));
	@RegistryObject("long_heat_resistance")
	public static final Housing<Potion> LONG_HEAT_RESISTANCE = Housing.create(() -> new Potion("heat_resistance", new MobEffectInstance(SMobEffects.HEAT_RESISTANCE.holder(), 9600)));
	@RegistryObject("strong_heat_resistance")
	public static final Housing<Potion> STRONG_HEAT_RESISTANCE = Housing.create(() -> new Potion("heat_resistance", new MobEffectInstance(SMobEffects.HEAT_RESISTANCE.holder(), 1800, 1)));
	@RegistryObject("cold_resistance")
	public static final Housing<Potion> COLD_RESISTANCE = Housing.create(() -> new Potion("cold_resistance", new MobEffectInstance(SMobEffects.COLD_RESISTANCE.holder(), 3600)));
	@RegistryObject("long_cold_resistance")
	public static final Housing<Potion> LONG_COLD_RESISTANCE = Housing.create(() -> new Potion("cold_resistance", new MobEffectInstance(SMobEffects.COLD_RESISTANCE.holder(), 9600)));
	@RegistryObject("strong_cold_resistance")
	public static final Housing<Potion> STRONG_COLD_RESISTANCE = Housing.create(() -> new Potion("cold_resistance", new MobEffectInstance(SMobEffects.COLD_RESISTANCE.holder(), 1800, 1)));
}
