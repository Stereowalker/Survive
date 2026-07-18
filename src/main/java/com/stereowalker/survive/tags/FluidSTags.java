package com.stereowalker.survive.tags;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class FluidSTags {
	public static final TagKey<Fluid> PURIFIED_WATER = create("purified_water");

	public FluidSTags() {
	}

	private static TagKey<Fluid> create(String pName) {
		return TagKey.create(RegistryHelper.fluidKey(), VersionHelper.toLoc(Survive.MOD_ID, pName));
	}

	public static TagKey<Fluid> create(Identifier name) {
		return TagKey.create(RegistryHelper.fluidKey(), name);
	}
}
