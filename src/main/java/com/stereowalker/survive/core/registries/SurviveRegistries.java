package com.stereowalker.survive.core.registries;

import com.stereowalker.survive.world.seasons.Season;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeCondition;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class SurviveRegistries {
	public static final ResourceKey<Registry<TemperatureChangeCondition<?>>> CONDITION = key("survive:temperature_change_condition");
	public static final ResourceKey<Registry<Season>> SEASON = key("survive:season");

	private static <T> ResourceKey<Registry<T>> key(String name)
    {
        return ResourceKey.createRegistryKey(new ResourceLocation(name));
    }
	
	public class ForgeRegistry {
		public static final IForgeRegistry<TemperatureChangeCondition<?>> CONDITION = RegistryManager.ACTIVE.getRegistry(SurviveRegistries.CONDITION);
		public static final IForgeRegistry<Season> SEASON = RegistryManager.ACTIVE.getRegistry(SurviveRegistries.SEASON);
	}
}
