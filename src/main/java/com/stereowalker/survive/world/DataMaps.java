package com.stereowalker.survive.world;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.stereowalker.survive.json.ArmorJsonHolder;
import com.stereowalker.survive.json.BiomeJsonHolder;
import com.stereowalker.survive.json.BlockTemperatureJsonHolder;
import com.stereowalker.survive.json.EntityTemperatureJsonHolder;
import com.stereowalker.survive.json.FluidJsonHolder;
import com.stereowalker.survive.json.FoodJsonHolder;
import com.stereowalker.survive.json.PotionJsonHolder;

import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;

public class DataMaps {
	public static class Server {
		public static final Map<UUID, Boolean> syncedClients = Maps.newHashMap();
		public static final Map<Identifier, FoodProperties> defaultFood = Maps.newHashMap();
		public static final Map<Identifier, FoodJsonHolder> consummableItem = Maps.newHashMap();
		public static final Map<Identifier, PotionJsonHolder> potionDrink = Maps.newHashMap();
		public static final Map<Identifier, ArmorJsonHolder> armor = Maps.newHashMap();
		public static final Map<Identifier, FluidJsonHolder> fluid = Maps.newHashMap();
		public static final Map<Identifier, BlockTemperatureJsonHolder> blockTemperature = Maps.newHashMap();
		public static final Map<Identifier, EntityTemperatureJsonHolder> entityTemperature = Maps.newHashMap();
		public static final Map<Identifier, BiomeJsonHolder> biome = Maps.newHashMap();
	}
	public static class Client {
		public static ImmutableMap<Identifier, FoodProperties> defaultFood = ImmutableMap.of();
		public static ImmutableMap<Identifier, FoodJsonHolder> consummableItem = ImmutableMap.of();
		public static ImmutableMap<Identifier, PotionJsonHolder> potionDrink = ImmutableMap.of();
		public static ImmutableMap<Identifier, ArmorJsonHolder> armor = ImmutableMap.of();
		public static ImmutableMap<Identifier, FluidJsonHolder> fluid = ImmutableMap.of();
		public static ImmutableMap<Identifier, BlockTemperatureJsonHolder> blockTemperature = ImmutableMap.of();
		public static ImmutableMap<Identifier, EntityTemperatureJsonHolder> entityTemperature = ImmutableMap.of();
		public static ImmutableMap<Identifier, BiomeJsonHolder> biome = ImmutableMap.of();
	}
}
