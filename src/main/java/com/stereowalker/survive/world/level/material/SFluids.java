package com.stereowalker.survive.world.level.material;

import com.stereowalker.unionlib.registries.RegistryHolder;

import net.minecraft.world.level.material.FlowingFluid;

@RegistryHolder("survive")
public class SFluids {
	@RegistryHolder("flowing_purified_water")
	public static final FlowingFluid FLOWING_PURIFIED_WATER = new PurifiedWaterFluid.Flowing();
	@RegistryHolder("purified_water")
	public static final FlowingFluid PURIFIED_WATER = new PurifiedWaterFluid.Source();
}
