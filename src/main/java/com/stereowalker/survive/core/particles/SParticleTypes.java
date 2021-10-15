package com.stereowalker.survive.core.particles;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.IForgeRegistry;

public class SParticleTypes {
	public static final List<ParticleType<?>> PARTICLES = new ArrayList<ParticleType<?>>();
	public static final SimpleParticleType STINK = register("stink", false);

	private static SimpleParticleType register(String key, boolean alwaysShow) {
		SimpleParticleType particle = new SimpleParticleType(alwaysShow);
		particle.setRegistryName(Survive.getInstance().location(key));
		PARTICLES.add(particle);
		return particle;
		//		return (SimpleParticleType)Registry.<ParticleType<? extends IParticleData>>register(Registry.PARTICLE_TYPE, key, );
	}

	//	private static <T extends IParticleData> ParticleType<T> register(String key, IParticleData.IDeserializer<T> deserializer) {
	//		return Registry.register(Registry.PARTICLE_TYPE, key, new ParticleType<>(false, deserializer));
	//	}

	public static void registerAll(IForgeRegistry<ParticleType<?>> registry) {
		for(ParticleType<?> particle: PARTICLES) {
			registry.register(particle);
			Survive.getInstance().debug("Particle: \""+particle.getRegistryName().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Particles Registered");
	}

}
