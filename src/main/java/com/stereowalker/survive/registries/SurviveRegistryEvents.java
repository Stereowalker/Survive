package com.stereowalker.survive.registries;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.blocks.SBlocks;
import com.stereowalker.survive.client.particle.StinkParticle;
import com.stereowalker.survive.enchantment.SEnchantments;
import com.stereowalker.survive.entity.ai.SAttributes;
import com.stereowalker.survive.fluid.SFluids;
import com.stereowalker.survive.item.SItems;
import com.stereowalker.survive.item.crafting.SRecipeSerializer;
import com.stereowalker.survive.particles.SParticleTypes;
import com.stereowalker.survive.potion.SEffects;
import com.stereowalker.survive.potion.SPotions;
import com.stereowalker.survive.temperature.TemperatureChangeCondition;
import com.stereowalker.survive.temperature.TemperatureChangeConditions;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class SurviveRegistryEvents
{
	private static final int MAX_VARINT = Integer.MAX_VALUE - 1;
	//Game Object Registries
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) 
	{
		SBlocks.registerAll(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerFluids(final RegistryEvent.Register<Fluid> event) {
		SFluids.registerAll(event.getRegistry());
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerItemColors(ColorHandlerEvent.Block event) {
		event.getBlockColors().register((state, displayReader, blockPos, tintIndex) -> {
			return displayReader != null && blockPos != null ? BiomeColors.getWaterColor(displayReader, blockPos) : -1;
		}, SBlocks.PURIFIED_WATER);
	}

	@SubscribeEvent
	public static void registerAttributes(final RegistryEvent.Register<Attribute> event) {
		SAttributes.registerAll(event.getRegistry());
	}
	
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, SAttributes.COLD_RESISTANCE);
		event.add(EntityType.PLAYER, SAttributes.HEAT_RESISTANCE);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		ParticleManager manager = Minecraft.getInstance().particles;
		manager.registerFactory(SParticleTypes.STINK, StinkParticle.Factory::new);
	}

	@SubscribeEvent
	public static void registerParticles(final RegistryEvent.Register<ParticleType<?>> event) {
		SParticleTypes.registerAll(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		SItems.registerAll(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerEffects(final RegistryEvent.Register<Effect> event) {
		SEffects.registerAll(event.getRegistry());
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
	public static void registerRecipeSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
		SRecipeSerializer.registerAll(event.getRegistry());
	}
	
	@SubscribeEvent
	public static void registerCombatRegistries(final RegistryEvent.NewRegistry event) {
		new RegistryBuilder<TemperatureChangeCondition<?>>().setName(Survive.getInstance().location("temperature_change_condition")).setType(c(TemperatureChangeCondition.class)).setMaxID(MAX_VARINT).create();
	}

	//Custom Survive Registries
	@SubscribeEvent
	public static void registerSpells(final RegistryEvent.Register<TemperatureChangeCondition<?>> event) {
		TemperatureChangeConditions.registerAll(event.getRegistry());
	}
	
	@SuppressWarnings("unchecked") //Ugly hack to let us pass in a typed Class object. Remove when we remove type specific references.
    private static <T> Class<T> c(Class<?> cls) { return (Class<T>)cls; }
}
