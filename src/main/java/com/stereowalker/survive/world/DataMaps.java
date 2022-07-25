package com.stereowalker.survive.world;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.stereowalker.survive.json.ArmorJsonHolder;
import com.stereowalker.survive.json.BiomeTemperatureJsonHolder;
import com.stereowalker.survive.json.BlockTemperatureJsonHolder;
import com.stereowalker.survive.json.EntityTemperatureJsonHolder;
import com.stereowalker.survive.json.FluidJsonHolder;
import com.stereowalker.survive.json.FoodJsonHolder;
import com.stereowalker.survive.json.PotionJsonHolder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DataMaps {
	public static class Server {
		public static boolean syncedToClient = false;
		public static final Map<ResourceLocation, FoodProperties> defaultFood = Maps.newHashMap();
		public static final Map<ResourceLocation, FoodJsonHolder> consummableItem = Maps.newHashMap();
		public static final Map<ResourceLocation, PotionJsonHolder> potionDrink = Maps.newHashMap();
		public static final Map<ResourceLocation, ArmorJsonHolder> armor = Maps.newHashMap();
		public static final Map<ResourceLocation, FluidJsonHolder> fluid = Maps.newHashMap();
		public static final Map<ResourceLocation, BlockTemperatureJsonHolder> blockTemperature = Maps.newHashMap();
		public static final Map<ResourceLocation, EntityTemperatureJsonHolder> entityTemperature = Maps.newHashMap();
		public static final Map<ResourceLocation, BiomeTemperatureJsonHolder> biomeTemperature = Maps.newHashMap();
	}
	@OnlyIn(Dist.CLIENT)
	public static class Client {
		public static ImmutableMap<ResourceLocation, FoodProperties> defaultFood = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, FoodJsonHolder> consummableItem = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, PotionJsonHolder> potionDrink = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, ArmorJsonHolder> armor = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, FluidJsonHolder> fluid = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, BlockTemperatureJsonHolder> blockTemperature = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, EntityTemperatureJsonHolder> entityTemperature = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, BiomeTemperatureJsonHolder> biomeTemperature = ImmutableMap.of();
	}
}
