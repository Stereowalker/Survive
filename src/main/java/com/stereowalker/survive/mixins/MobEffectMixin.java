package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.food.FoodData;

@Mixin(MobEffect.class)
public class MobEffectMixin {

	@Redirect(method = "applyEffectTick", at =@At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
	public void isFull(FoodData data, int i, float f) {
		if (data.needsFood()) data.eat(i, f);
	}
}
