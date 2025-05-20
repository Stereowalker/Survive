package com.stereowalker.survive.damagesource;

import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface SDamageTypes {
	ResourceKey<DamageType> HYPOTHERMIA = ResourceKey.create(Registries.DAMAGE_TYPE, VersionHelper.toLoc("survive:hypothermia"));
	ResourceKey<DamageType> HYPERTHERMIA = ResourceKey.create(Registries.DAMAGE_TYPE, VersionHelper.toLoc("survive:hyperthermia"));
	ResourceKey<DamageType> ROAST = ResourceKey.create(Registries.DAMAGE_TYPE, VersionHelper.toLoc("survive:roast"));
	ResourceKey<DamageType> DEHYDRATE = ResourceKey.create(Registries.DAMAGE_TYPE, VersionHelper.toLoc("survive:dehydrate"));
	ResourceKey<DamageType> OVERHYDRATE = ResourceKey.create(Registries.DAMAGE_TYPE, VersionHelper.toLoc("survive:overhydrate"));
	ResourceKey<DamageType> OVEREAT = ResourceKey.create(Registries.DAMAGE_TYPE, VersionHelper.toLoc("survive:overeat"));
	ResourceKey<DamageType> OVERWORK = ResourceKey.create(Registries.DAMAGE_TYPE, VersionHelper.toLoc("survive:overwork"));
}
