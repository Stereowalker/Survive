package com.stereowalker.survive.tags;

import com.stereowalker.survive.Survive;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class FluidSTags {
	public static final TagKey<Fluid> PURIFIED_WATER = create("purified_water");

	public FluidSTags() {
	}

	private static TagKey<Fluid> create(String pName) {
		return TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(Survive.MOD_ID, pName));
	}

	public static TagKey<Fluid> create(ResourceLocation name) {
		return TagKey.create(Registry.FLUID_REGISTRY, name);
	}
}
