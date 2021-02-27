package com.stereowalker.survive.registries;

import com.stereowalker.survive.blocks.SBlocks;
import com.stereowalker.survive.enchantment.SEnchantments;
import com.stereowalker.survive.entity.ai.SAttributes;
import com.stereowalker.survive.fluid.SFluids;
import com.stereowalker.survive.item.SItems;
import com.stereowalker.survive.item.crafting.SRecipeSerializer;
import com.stereowalker.survive.potion.SEffects;
import com.stereowalker.survive.potion.SPotions;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class SurviveRegistryEvents
{
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
}
