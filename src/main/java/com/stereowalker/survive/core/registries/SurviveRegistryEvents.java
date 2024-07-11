package com.stereowalker.survive.core.registries;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.client.particle.HygieneParticle;
import com.stereowalker.survive.core.particles.SParticleTypes;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.TemperatureRegulatorPlateItem;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.survive.world.item.crafting.conditions.ModuleEnabledCondition;
import com.stereowalker.survive.world.level.block.PlatedTemperatureRegulatorBlock;
import com.stereowalker.survive.world.level.block.SBlocks;
import com.stereowalker.survive.world.level.material.PurifiedWaterFluid;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.survive.world.level.storage.loot.predicates.SLootItemConditions;
import com.stereowalker.survive.world.seasons.Season;
import com.stereowalker.survive.world.seasons.Seasons;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeCondition;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeConditions;
import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class SurviveRegistryEvents
{
	private static final int MAX_VARINT = Integer.MAX_VALUE - 1;
	//Game Object Registries
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerItemColors(RegisterColorHandlersEvent.Block event) {
		event.register((state, displayReader, blockPos, tintIndex) -> {
			return Survive.PURIFIED_WATER_COLOR;
		}, SBlocks.PURIFIED_WATER, SBlocks.PURIFIED_WATER_CAULDRON);
		event.register((state, displayReader, blockPos, tintIndex) -> {
			return 0x483c35;
		}, SBlocks.POTASH_CAULDRON);
		event.register((state, displayReader, blockPos, tintIndex) -> {
			return PlatedTemperatureRegulatorBlock.getColor(state);
		}, SBlocks.PLATED_TEMPERATURE_REGULATOR);
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> {
			PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
			return tintIndex > 0 ? -1 : contents.potion().get().value() == SPotions.PURIFIED_WATER.holder() ? Survive.PURIFIED_WATER_COLOR : FastColor.ARGB32.opaque(contents.getColor());
	      }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
		event.register((stack, tintIndex) -> {
			return TemperatureRegulatorPlateItem.getColor(stack);
		}, SItems.LARGE_HEATING_PLATE, SItems.LARGE_COOLING_PLATE, SItems.MEDIUM_HEATING_PLATE, SItems.MEDIUM_COOLING_PLATE, SItems.SMALL_HEATING_PLATE, SItems.SMALL_COOLING_PLATE);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(SParticleTypes.STINK, HygieneParticle.StinkFactory::new);
		event.registerSpriteSet(SParticleTypes.CLEAN, HygieneParticle.CleanFactory::new);
	}
	
	@SubscribeEvent
	public static void registerParticlesz(final RegisterEvent event) {
		event.register(RegistryHelper.particleTypeKey(), (helper) -> SParticleTypes.registerAll(helper));
		event.register(SurviveRegistries.CONDITION, (helper) -> TemperatureChangeConditions.registerAll(helper));
		event.register(SurviveRegistries.SEASON, (helper) -> Seasons.registerAll(helper));
		event.register(ForgeRegistries.Keys.FLUID_TYPES, (helper) -> helper.register(VersionHelper.toLoc("survive:purified_water"), PurifiedWaterFluid.TYPE));
//		if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
//        {			
//        }
		event.register(ForgeRegistries.Keys.CONDITION_SERIALIZERS, (reg) -> {
			reg.register(VersionHelper.toLoc("survive", "module_enabled"), ModuleEnabledCondition.CODEC);
		});
		new SLootItemConditions();
		MobEffects.FIRE_RESISTANCE.value().addAttributeModifier(SAttributes.HEAT_RESISTANCE.holder(), VersionHelper.toLoc("fire_heat_res"), 5.0D, AttributeModifier.Operation.ADD_VALUE);
		Survive.POTION_FLUID_MAP = 
				new ImmutableMap.Builder<Holder<Potion>, List<Fluid>>()
				.put(Potions.WATER, Lists.newArrayList(Fluids.FLOWING_WATER, Fluids.WATER))
				.put(SPotions.PURIFIED_WATER.holder(), Lists.newArrayList(SFluids.FLOWING_PURIFIED_WATER, SFluids.PURIFIED_WATER)).build();
	}
	
	@SubscribeEvent
	public static void registerSurviveRegistries(final NewRegistryEvent event) {
		event.create(new RegistryBuilder<TemperatureChangeCondition<?>>().setName(SurviveRegistries.CONDITION.location()).setMaxID(MAX_VARINT));
		event.create(new RegistryBuilder<Season>().setName(Survive.getInstance().location("season")).setMaxID(MAX_VARINT));
	}
}
