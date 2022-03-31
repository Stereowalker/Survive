package com.stereowalker.survive.core.registries;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.client.particle.HygieneParticle;
import com.stereowalker.survive.core.particles.SParticleTypes;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.TemperatureRegulatorPlateItem;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.survive.world.item.crafting.conditions.ModuleEnabledCondition;
import com.stereowalker.survive.world.item.enchantment.SEnchantments;
import com.stereowalker.survive.world.level.block.PlatedTemperatureRegulatorBlock;
import com.stereowalker.survive.world.level.block.SBlocks;
import com.stereowalker.survive.world.seasons.Season;
import com.stereowalker.survive.world.seasons.Seasons;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeCondition;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeConditions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class SurviveRegistryEvents
{
	private static final int MAX_VARINT = Integer.MAX_VALUE - 1;
	//Game Object Registries
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerItemColors(ColorHandlerEvent.Block event) {
		event.getBlockColors().register((state, displayReader, blockPos, tintIndex) -> {
			return 0x41d3f8;
		}, SBlocks.PURIFIED_WATER, SBlocks.PURIFIED_WATER_CAULDRON);
		event.getBlockColors().register((state, displayReader, blockPos, tintIndex) -> {
			return 0x483c35;
		}, SBlocks.POTASH_CAULDRON);
		event.getBlockColors().register((state, displayReader, blockPos, tintIndex) -> {
			return PlatedTemperatureRegulatorBlock.getColor(state);
		}, SBlocks.PLATED_TEMPERATURE_REGULATOR);
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerItemColors(ColorHandlerEvent.Item event) {
		event.getItemColors().register((stack, tintIndex) -> {
			return TemperatureRegulatorPlateItem.getColor(stack);
		}, SItems.LARGE_HEATING_PLATE, SItems.LARGE_COOLING_PLATE, SItems.MEDIUM_HEATING_PLATE, SItems.MEDIUM_COOLING_PLATE, SItems.SMALL_HEATING_PLATE, SItems.SMALL_COOLING_PLATE);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, SAttributes.COLD_RESISTANCE);
		event.add(EntityType.PLAYER, SAttributes.HEAT_RESISTANCE);
		event.add(EntityType.PLAYER, SAttributes.MAX_STAMINA);
	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		ParticleEngine manager = Minecraft.getInstance().particleEngine;
		manager.register(SParticleTypes.STINK, HygieneParticle.StinkFactory::new);
		manager.register(SParticleTypes.CLEAN, HygieneParticle.CleanFactory::new);
	}

	@SubscribeEvent
	public static void registerParticles(final RegistryEvent.Register<ParticleType<?>> event) {
		SParticleTypes.registerAll(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerEffects(final RegistryEvent.Register<MobEffect> event) {
		MobEffects.FIRE_RESISTANCE.addAttributeModifier(SAttributes.HEAT_RESISTANCE, "795606d6-4ac6-4ae7-8311-63ccdb293eb4", 5.0D, AttributeModifier.Operation.ADDITION);
	}

	@SubscribeEvent
	public static void registerPotions(final RegistryEvent.Register<Potion> event) {
		SPotions.registerAll(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event) {
		SEnchantments.registerAll(event.getRegistry());
	}
	@SubscribeEvent
	public static void registerRecipeSerializers(final RegistryEvent.Register<RecipeSerializer<?>> event) {
		CraftingHelper.register(ModuleEnabledCondition.Serializer.INSTANCE);
	}

	@SubscribeEvent
	public static void registerSurviveRegistries(final NewRegistryEvent event) {
		event.create(new RegistryBuilder<TemperatureChangeCondition<?>>().setName(Survive.getInstance().location("temperature_change_condition")).setType(c(TemperatureChangeCondition.class)).setMaxID(MAX_VARINT));
		event.create(new RegistryBuilder<Season>().setName(Survive.getInstance().location("season")).setType(c(Season.class)).setMaxID(MAX_VARINT));
	}

	//Custom Survive Registries
	@SubscribeEvent
	public static void registerTemperatureChangeConditions(final RegistryEvent.Register<TemperatureChangeCondition<?>> event) {
		TemperatureChangeConditions.registerAll(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerSeasons(final RegistryEvent.Register<Season> event) {
		Seasons.registerAll(event.getRegistry());
	}

	@SuppressWarnings("unchecked") //Ugly hack to let us pass in a typed Class object. Remove when we remove type specific references.
	private static <T> Class<T> c(Class<?> cls) { return (Class<T>)cls; }
}
