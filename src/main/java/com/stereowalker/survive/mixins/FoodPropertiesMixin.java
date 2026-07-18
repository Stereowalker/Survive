package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.survive.events.SurviveEvents;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {
	@Inject(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
	public void onConsume_inject(Level level, LivingEntity user, ItemStack stack, Consumable consumable, CallbackInfo ci) {
		SurviveEvents.eat(user, stack);
	}
	
	@Inject(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V")) //TODO: 26.2 This needs to be static, it's the same thing as LivingEntityMixin
	public void addNutrients(Level level, LivingEntity user, ItemStack stack, Consumable consumable, CallbackInfo ci) {
		SurviveEvents.eatNutrition(user, stack);
	}
}
