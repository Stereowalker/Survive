package com.stereowalker.survive.core.registries;

import com.stereowalker.survive.world.item.crafting.conditions.ModuleEnabledCondition;
import com.stereowalker.survive.world.level.storage.loot.predicates.SLootItemConditions;
import com.stereowalker.unionlib.util.VersionHelper;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(bus=EventBusSubscriber.Bus.MOD)
public class SurviveRegistryEvents
{
	
	@SubscribeEvent
	public static void registerParticlesz(final RegisterEvent event) {
		event.register(NeoForgeRegistries.Keys.CONDITION_CODECS, (reg) -> {
			reg.register(VersionHelper.toLoc("survive", "module_enabled"), ModuleEnabledCondition.CODEC);
		});
		new SLootItemConditions();
	}
}
