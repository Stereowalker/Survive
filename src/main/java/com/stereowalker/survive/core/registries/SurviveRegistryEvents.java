package com.stereowalker.survive.core.registries;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.client.particle.HygieneParticle;
import com.stereowalker.survive.core.particles.SParticleTypes;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.survive.world.item.crafting.conditions.ModuleEnabledCondition;
import com.stereowalker.survive.world.level.material.PurifiedWaterFluid;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.survive.world.level.storage.loot.predicates.SLootItemConditions;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class SurviveRegistryEvents
{
	
	@SubscribeEvent
	public static void registerParticlesz(final RegisterEvent event) {
		event.register(ForgeRegistries.Keys.FLUID_TYPES, (helper) -> helper.register(VersionHelper.toLoc("survive:purified_water"), PurifiedWaterFluid.TYPE));
//		if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
//        {			
//        }
		event.register(ForgeRegistries.Keys.CONDITION_SERIALIZERS, (reg) -> {
			reg.register(VersionHelper.toLoc("survive", "module_enabled"), ModuleEnabledCondition.CODEC);
		});
		new SLootItemConditions();
		Survive.POTION_FLUID_MAP = 
				new ImmutableMap.Builder<Holder<Potion>, List<Fluid>>()
				.put(Potions.WATER, Lists.newArrayList(Fluids.FLOWING_WATER, Fluids.WATER))
				.put(SPotions.PURIFIED_WATER.holder(), Lists.newArrayList(SFluids.FLOWING_PURIFIED_WATER, SFluids.PURIFIED_WATER)).build();
	}
}
