package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.EnergyStats;

import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), method = {"jump", "damageEntity", "addMovementStat"})
	public void staminaExhaustion(PlayerEntity player, float value) {
		if (Config.enable_stamina) {
			EnergyStats energyStats = SurviveEntityStats.getEnergyStats(player);
			energyStats.addExhaustion(player, value);
			SurviveEntityStats.setEnergyStats(player, energyStats);
		}
		else {
			player.addExhaustion(value);
		}
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), method = "attackTargetEntityWithCurrentItem")
	public void exhaustStaminaDuringAttack(PlayerEntity player, float value) {
		if (Config.enable_stamina) {
			EnergyStats energyStats = SurviveEntityStats.getEnergyStats(player);
			energyStats.addExhaustion(player, 0.5f);
			SurviveEntityStats.setEnergyStats(player, energyStats);
		}
		else {
			player.addExhaustion(value);
		}
	}
}
