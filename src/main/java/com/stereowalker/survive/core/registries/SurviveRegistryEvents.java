package com.stereowalker.survive.core.registries;

import com.stereowalker.survive.world.item.crafting.conditions.ModuleEnabledCondition;
import com.stereowalker.survive.world.level.storage.loot.predicates.SLootItemConditions;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class SurviveRegistryEvents
{
	
	@SubscribeEvent
	public static void registerParticlesz(final RegisterEvent event) {
//		if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
//        {			
//        }
		if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
        {			
			CraftingHelper.register(ModuleEnabledCondition.Serializer.INSTANCE);
        }
		new SLootItemConditions();
	}
}
