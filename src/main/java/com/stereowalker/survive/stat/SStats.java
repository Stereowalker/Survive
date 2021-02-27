package com.stereowalker.survive.stat;

import com.stereowalker.rankup.api.stat.Stat;
import com.stereowalker.survive.Survive;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class SStats {
	public static final DeferredRegister<Stat> STAT_REGISTRY = DeferredRegister.create(Stat.class, Survive.MOD_ID);
	public static final Stat COLD_RESISTANCE = register("cold_resistance", new ColdResistanceStat());
	public static final Stat HEAT_RESISTANCE = register("heat_resistance", new HeatResistanceStat());


	
	public static Stat register(String name, Stat Stat) {
		STAT_REGISTRY.register(name, () -> Stat);
		return Stat;
	}
	
	public static void registerAll(IEventBus eventBus) {
		STAT_REGISTRY.register(eventBus);
	}
}
