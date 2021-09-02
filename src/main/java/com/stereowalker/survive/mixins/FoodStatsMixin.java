package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.NutritionStats;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;

@Mixin(FoodStats.class)
public class FoodStatsMixin {

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"))
	public void nutritionHeal(PlayerEntity player, float value) {
		if (Config.nutrition_enabled) {
			NutritionStats nutritionStats = SurviveEntityStats.getNutritionStats(player);
			int protein = nutritionStats.getProteinLevel();
			if (protein > 200 && protein <= 300) {
				player.heal(value*0.8f);
				nutritionStats.removeProtein(1);
			} else if (protein > 100 && protein <= 200) {
				player.heal(value);
				nutritionStats.removeProtein(1);
			} else if (protein > 0 && protein <= 100) {
				player.heal(value*0.5f);
				nutritionStats.removeProtein(1);
			} else if (protein >= -100 && protein <= 0) {
				player.heal(value*0.1f);
				nutritionStats.removeProtein(1);
			}
			nutritionStats.save(player);
		} else {
			player.heal(value);
		}
	}
}
