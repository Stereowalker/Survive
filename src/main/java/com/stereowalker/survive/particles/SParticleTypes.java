package com.stereowalker.survive.particles;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.registries.IForgeRegistry;

public class SParticleTypes {
	public static final List<ParticleType<?>> PARTICLES = new ArrayList<ParticleType<?>>();
	public static final BasicParticleType STINK = register("stink", false);

	private static BasicParticleType register(String key, boolean alwaysShow) {
		BasicParticleType particle = new BasicParticleType(alwaysShow);
		particle.setRegistryName(Survive.getInstance().location(key));
		PARTICLES.add(particle);
		return particle;
		//		return (BasicParticleType)Registry.<ParticleType<? extends IParticleData>>register(Registry.PARTICLE_TYPE, key, );
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
