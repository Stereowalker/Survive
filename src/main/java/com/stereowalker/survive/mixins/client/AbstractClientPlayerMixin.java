package com.stereowalker.survive.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player implements IRealisticEntity {

	public AbstractClientPlayerMixin(Level level, GameProfile gameProfile) {
		super(level, gameProfile);
	}
	
	@Inject(method = "tick", at = @At("TAIL"))
	public void tickInject(CallbackInfo ci) {
		AbstractClientPlayer player = (AbstractClientPlayer)(Object)this;
		staminaData().baseClientTick(player);
		hygieneData().baseClientTick(player);
		nutritionData().baseClientTick(player);
		temperatureData().baseClientTick(player);
		waterData().baseClientTick(player);
		wellbeingData().baseClientTick(player);
		sleepData().baseClientTick(player);
	}
}
