package com.stereowalker.survive.tags;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class FluidSTags {
	public static final TagKey<Fluid> PURIFIED_WATER = create("purified_water");

	public FluidSTags() {
	}

	private static TagKey<Fluid> create(String pName) {
		return TagKey.create(RegistryHelper.fluidKey(), new ResourceLocation(Survive.MOD_ID, pName));
	}

	public static TagKey<Fluid> create(ResourceLocation name) {
		return TagKey.create(RegistryHelper.fluidKey(), name);
	}
}
