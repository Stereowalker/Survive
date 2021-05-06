package com.stereowalker.survive.temperature;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;

import net.minecraftforge.registries.IForgeRegistry;

public class TemperatureChangeConditions {
	public static final List<TemperatureChangeCondition<?>> CONDITION_LIST = new ArrayList<TemperatureChangeCondition<?>>();

	public static final TemperatureChangeCondition<?> DEFAULT = register("default", new DefaultCondition());
	
	public static TemperatureChangeCondition<?> register(String name, TemperatureChangeCondition<?> condition) {
		condition.setRegistryName(Survive.getInstance().location(name));
		CONDITION_LIST.add(condition);
		return condition;
	}
	
	public static void registerAll(IForgeRegistry<TemperatureChangeCondition<?>> registry) {
		for(TemperatureChangeCondition<?> condition : CONDITION_LIST) {
			registry.register(condition);
			Survive.getInstance().debug("Condition: \""+condition.getRegistryName().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Conditions Registered");
	}
}
