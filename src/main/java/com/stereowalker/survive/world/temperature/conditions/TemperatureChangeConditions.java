package com.stereowalker.survive.world.temperature.conditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.stereowalker.survive.Survive;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;

public class TemperatureChangeConditions {
	public static final Map<ResourceLocation, TemperatureChangeCondition<?>> CONDITION_LIST = new HashMap<ResourceLocation, TemperatureChangeCondition<?>>();

	public static final TemperatureChangeCondition<?> DEFAULT = register("default", new DefaultCondition());
	public static final TemperatureChangeCondition<?> UNDERWATER = register("underwater", new UnderwaterCondition());
	public static final TemperatureChangeCondition<?> ON_FIRE = register("on_fire", new OnFireCondition());
	public static final TemperatureChangeCondition<?> BIOME = register("biome", new BiomeCondition());
	public static final TemperatureChangeCondition<?> NOT_BIOME = register("not_biome", new BiomeNotCondition());
	
	public static TemperatureChangeCondition<?> register(String name, TemperatureChangeCondition<?> condition) {
		CONDITION_LIST.put(Survive.getInstance().location(name), condition);
		return condition;
	}
	
	public static void registerAll(RegisterHelper<TemperatureChangeCondition<?>> registry) {
		for(Entry<ResourceLocation, TemperatureChangeCondition<?>> condition : CONDITION_LIST.entrySet()) {
			registry.register(condition.getKey(), condition.getValue());
			Survive.getInstance().debug("Condition: \""+condition.getKey().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Conditions Registered");
	}
}
