package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.NutritionData;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

@Mixin(FoodData.class)
public class FoodDataMixin {

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"))
	public void nutritionHeal(Player player, float value) {
		if (Survive.CONFIG.nutrition_enabled) {
			NutritionData nutritionStats = SurviveEntityStats.getNutritionStats(player);
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
