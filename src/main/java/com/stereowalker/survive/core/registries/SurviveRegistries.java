package com.stereowalker.survive.core.registries;

import com.stereowalker.survive.world.seasons.Season;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeCondition;
import com.stereowalker.unionlib.api.registries.RegistryWrapper;
import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class SurviveRegistries {
	public static final RegistryWrapper<TemperatureChangeCondition<?>> CONDITION = RegistryHelper.createWrapper();
	public static final RegistryWrapper<Season> SEASON = RegistryHelper.createWrapper();
	
	public class Keys {
		public static final ResourceKey<Registry<TemperatureChangeCondition<?>>> CONDITION = key("survive:temperature_change_condition");
		public static final ResourceKey<Registry<Season>> SEASON = key("survive:season");
		private static <T> ResourceKey<Registry<T>> key(String name)
		{
			return ResourceKey.createRegistryKey(VersionHelper.toLoc(name));
		}
	}

}
