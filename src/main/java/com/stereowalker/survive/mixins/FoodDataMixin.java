package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;

@Mixin(FoodData.class)
public class FoodDataMixin {

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V"))
	public void nutritionHeal(ServerPlayer player, float value) {
		if (Survive.CONFIG.nutrition_enabled && player instanceof IRealisticEntity real) {
			int protein = real.nutritionData().protein().level();
			if (protein > 2000 && protein <= 3000) {
				player.heal(value*1.5f);
				real.nutritionData().protein().remove(10);
			} else if (protein > 1000 && protein <= 2000) {
				player.heal(value);
				real.nutritionData().protein().remove(10);
			} else if (protein > 0 && protein <= 1000) {
				player.heal(value*0.5f);
				real.nutritionData().protein().remove(10);
			} else if (protein >= -1000 && protein <= 0) {
				player.heal(value*0.1f);
				real.nutritionData().protein().remove(10);
			}
		} else {
			player.heal(value);
		}
	}
}
