package com.stereowalker.survive;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.stereowalker.survive.util.data.ArmorData;
import com.stereowalker.survive.util.data.BiomeTemperatureData;
import com.stereowalker.survive.util.data.BlockTemperatureData;
import com.stereowalker.survive.util.data.EntityTemperatureData;
import com.stereowalker.survive.util.data.FoodData;
import com.stereowalker.survive.util.data.PotionData;

import net.minecraft.item.Food;
import net.minecraft.util.ResourceLocation;

public class DataMaps {
	public static class Server {
		public static final Map<ResourceLocation, Food> defaultFood = Maps.newHashMap();
		public static final Map<ResourceLocation, FoodData> consummableItem = Maps.newHashMap();
		public static final Map<ResourceLocation, PotionData> potionDrink = Maps.newHashMap();
		public static final Map<ResourceLocation, ArmorData> armor = Maps.newHashMap();
		public static final Map<ResourceLocation, BlockTemperatureData> blockTemperature = Maps.newHashMap();
		public static final Map<ResourceLocation, EntityTemperatureData> entityTemperature = Maps.newHashMap();
		public static final Map<ResourceLocation, BiomeTemperatureData> biomeTemperature = Maps.newHashMap();
	}
	public static class Client {
		public static ImmutableMap<ResourceLocation, Food> defaultFood = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, FoodData> consummableItem = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, PotionData> potionDrink = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, ArmorData> armor = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, BlockTemperatureData> blockTemperature = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, EntityTemperatureData> entityTemperature = ImmutableMap.of();
		public static ImmutableMap<ResourceLocation, BiomeTemperatureData> biomeTemperature = ImmutableMap.of();
	}
}
