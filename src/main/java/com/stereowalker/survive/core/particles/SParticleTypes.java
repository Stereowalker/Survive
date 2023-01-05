package com.stereowalker.survive.core.particles;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.stereowalker.survive.Survive;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;

public class SParticleTypes {
	public static final Map<ResourceLocation, ParticleType<?>> PARTICLES = new HashMap<ResourceLocation, ParticleType<?>>();
	public static final SimpleParticleType STINK = register("stink", false);
	public static final SimpleParticleType CLEAN = register("clean", false);

	private static SimpleParticleType register(String key, boolean alwaysShow) {
		SimpleParticleType particle = new SimpleParticleType(alwaysShow);
		PARTICLES.put(Survive.getInstance().location(key), particle);
		return particle;
		//		return (SimpleParticleType)Registry.<ParticleType<? extends IParticleData>>register(Registry.PARTICLE_TYPE, key, );
	}

	//	private static <T extends IParticleData> ParticleType<T> register(String key, IParticleData.IDeserializer<T> deserializer) {
	//		return Registry.register(Registry.PARTICLE_TYPE, key, new ParticleType<>(false, deserializer));
	//	}

	public static void registerAll(RegisterHelper<ParticleType<?>> registry) {
		for(Entry<ResourceLocation, ParticleType<?>> particle: PARTICLES.entrySet()) {
			registry.register(particle.getKey(), particle.getValue());
			Survive.getInstance().debug("Particle: \""+particle.getKey().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Particles Registered");
	}

}
