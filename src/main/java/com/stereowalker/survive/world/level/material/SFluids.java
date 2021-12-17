package com.stereowalker.survive.world.level.material;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.registries.RegistryHolder;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.IForgeRegistry;

@RegistryHolder("survive")
public class SFluids {
	@RegistryHolder("flowing_purified_water")
	public static final FlowingFluid FLOWING_PURIFIED_WATER = new PurifiedWaterFluid.Flowing();
	@RegistryHolder("purified_water")
	public static final FlowingFluid PURIFIED_WATER = new PurifiedWaterFluid.Source();
}
