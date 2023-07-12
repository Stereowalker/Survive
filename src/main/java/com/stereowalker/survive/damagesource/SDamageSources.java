package com.stereowalker.survive.damagesource;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class SDamageSources {

	public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> p_270957_) {
		return new DamageSource(access.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(p_270957_));
	}
}
