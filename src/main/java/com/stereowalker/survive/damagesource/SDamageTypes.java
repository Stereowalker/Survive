package com.stereowalker.survive.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public interface SDamageTypes {
	ResourceKey<DamageType> HYPOTHERMIA = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("survive:hypothermia"));
	ResourceKey<DamageType> HYPERTHERMIA = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("survive:hyperthermia"));
	ResourceKey<DamageType> ROAST = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("survive:roast"));
	ResourceKey<DamageType> DEHYDRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("survive:dehydrate"));
	ResourceKey<DamageType> OVERHYDRATE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("survive:overhydrate"));
	ResourceKey<DamageType> OVEREAT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("survive:overeat"));
	ResourceKey<DamageType> OVERWORK = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("survive:overwork"));
}
