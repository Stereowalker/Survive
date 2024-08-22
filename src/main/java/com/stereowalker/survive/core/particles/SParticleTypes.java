package com.stereowalker.survive.core.particles;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.core.particles.SimpleParticleType;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SParticleTypes {
	@RegistryObject("stink")
	public static final SimpleParticleType STINK =  new SimpleParticleType(false);
	@RegistryObject("clean")
	public static final SimpleParticleType CLEAN = new SimpleParticleType(false);
}
