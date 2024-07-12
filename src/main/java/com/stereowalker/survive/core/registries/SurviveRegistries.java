package com.stereowalker.survive.core.registries;

import com.stereowalker.survive.world.seasons.Season;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeCondition;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class SurviveRegistries {
	public static final ResourceKey<Registry<TemperatureChangeCondition<?>>> CONDITION = key("survive:temperature_change_condition");
	public static final ResourceKey<Registry<Season>> SEASON = key("survive:season");

	private static <T> ResourceKey<Registry<T>> key(String name)
    {
        return ResourceKey.createRegistryKey(VersionHelper.toLoc(name));
    }
	
	public class ForgeRegistry {
		public static final Registry<TemperatureChangeCondition<?>> CONDITION = new RegistryBuilder<>(SurviveRegistries.CONDITION).sync(true).maxId(Integer.MAX_VALUE - 1).create();
		public static final Registry<Season> SEASON = new RegistryBuilder<>(SurviveRegistries.SEASON).sync(true).maxId(Integer.MAX_VALUE - 1).create();
	}
}
