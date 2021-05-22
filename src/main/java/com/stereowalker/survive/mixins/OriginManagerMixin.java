package com.stereowalker.survive.mixins;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonElement;
import com.stereowalker.survive.Survive;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginManager;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.power.PowerTypeRegistry;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

@Mixin(OriginManager.class)
public class OriginManagerMixin {

	@Inject(at = @At(value = "RETURN"), method = {"apply"}, remap = false)
	public void add(Map<ResourceLocation, List<JsonElement>> loader, IResourceManager manager, IProfiler profiler, CallbackInfo info) {
		Origin g = OriginRegistry.get(Origins.identifier("blazeborn"));
		g.add(PowerTypeRegistry.get(Survive.getInstance().location("heat_resistance")));
	}
}
