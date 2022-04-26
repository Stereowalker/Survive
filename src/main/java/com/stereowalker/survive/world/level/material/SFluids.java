package com.stereowalker.survive.world.level.material;

import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

@RegistryHolder(registry = Fluid.class)
public class SFluids {
	@RegistryObject("flowing_purified_water")
	public static final FlowingFluid FLOWING_PURIFIED_WATER = new PurifiedWaterFluid.Flowing();
	@RegistryObject("purified_water")
	public static final FlowingFluid PURIFIED_WATER = new PurifiedWaterFluid.Source();
}
