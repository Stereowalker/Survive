package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements IRealisticEntity {
//	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V"), method = {"checkMovementStatistics"})
//	public void morphExhaustion(ServerPlayer player, float value) {
//		bypassFoodExhaustion(value, value*2.5f, Mth.ceil(value*2.5f), "Movement", player.isSprinting() || player.isSwimming());
//	}
}
