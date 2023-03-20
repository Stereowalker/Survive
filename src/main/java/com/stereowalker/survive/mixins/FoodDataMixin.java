package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

@Mixin(FoodData.class)
public class FoodDataMixin {

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"))
	public void nutritionHeal(Player player, float value) {
		if (Survive.CONFIG.nutrition_enabled && player instanceof IRealisticEntity real) {
			int protein = real.getNutritionData().getProteinLevel();
			if (protein > 200 && protein <= 300) {
				player.heal(value*0.8f);
				real.getNutritionData().removeProtein(1);
			} else if (protein > 100 && protein <= 200) {
				player.heal(value);
				real.getNutritionData().removeProtein(1);
			} else if (protein > 0 && protein <= 100) {
				player.heal(value*0.5f);
				real.getNutritionData().removeProtein(1);
			} else if (protein >= -100 && protein <= 0) {
				player.heal(value*0.1f);
				real.getNutritionData().removeProtein(1);
			}
		} else {
			player.heal(value);
		}
	}
}
