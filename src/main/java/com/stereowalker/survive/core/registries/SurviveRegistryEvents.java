package com.stereowalker.survive.core.registries;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.client.particle.HygieneParticle;
import com.stereowalker.survive.core.particles.SParticleTypes;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.survive.world.item.crafting.conditions.ModuleEnabledCondition;
import com.stereowalker.survive.world.level.material.PurifiedWaterFluid;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.survive.world.level.storage.loot.predicates.SLootItemConditions;
import com.stereowalker.survive.world.seasons.Seasons;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeConditions;
import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(bus=EventBusSubscriber.Bus.MOD)
public class SurviveRegistryEvents
{
	//Game Object Registries
	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(SParticleTypes.STINK, HygieneParticle.StinkFactory::new);
		event.registerSpriteSet(SParticleTypes.CLEAN, HygieneParticle.CleanFactory::new);
	}
	
	@SubscribeEvent
	public static void registerParticlesz(final RegisterEvent event) {
		event.register(RegistryHelper.particleTypeKey(), (helper) -> SParticleTypes.registerAll(helper));
		event.register(SurviveRegistries.Keys.CONDITION, (helper) -> TemperatureChangeConditions.registerAll(helper));
		event.register(SurviveRegistries.Keys.SEASON, (helper) -> Seasons.registerAll(helper));
		event.register(NeoForgeRegistries.Keys.FLUID_TYPES, (helper) -> helper.register(VersionHelper.toLoc("survive:purified_water"), PurifiedWaterFluid.TYPE));
//		if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
//        {			
//        }
		event.register(NeoForgeRegistries.Keys.CONDITION_CODECS, (reg) -> {
			reg.register(VersionHelper.toLoc("survive", "module_enabled"), ModuleEnabledCondition.CODEC);
		});
		new SLootItemConditions();
		MobEffects.FIRE_RESISTANCE.value().addAttributeModifier(SAttributes.HEAT_RESISTANCE.holder(), VersionHelper.toLoc("fire_heat_res"), 5.0D, AttributeModifier.Operation.ADD_VALUE);
		Survive.POTION_FLUID_MAP = 
				new ImmutableMap.Builder<Holder<Potion>, List<Fluid>>()
				.put(Potions.WATER, Lists.newArrayList(Fluids.FLOWING_WATER, Fluids.WATER))
				.put(SPotions.PURIFIED_WATER.holder(), Lists.newArrayList(SFluids.FLOWING_PURIFIED_WATER, SFluids.PURIFIED_WATER)).build();
	}
}
