package com.stereowalker.survive.fluid;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.registries.IForgeRegistry;

public class SFluids {
	public static final List<Fluid> FLUIDS = new ArrayList<Fluid>();
	
	public static final FlowingFluid FLOWING_PURIFIED_WATER = register("flowing_purified_water", new PurifiedWaterFluid.Flowing());
	public static final FlowingFluid PURIFIED_WATER = register("purified_water", new PurifiedWaterFluid.Source());

	
	public static <T extends Fluid> T register(String name, T fluid) {
		fluid.setRegistryName(Survive.location(name));
		FLUIDS.add(fluid);
		return fluid;
	}

	public static void registerAll(IForgeRegistry<Fluid> registry) {
		for(Fluid fluid: FLUIDS) {
			registry.register(fluid);
			Survive.debug("Fluid: \""+fluid.getRegistryName().toString()+"\" registered");
		}
		Survive.debug("All Fluids Registered");
	}
}
