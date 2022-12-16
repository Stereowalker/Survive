package com.stereowalker.survive.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.stereowalker.survive.network.protocol.game.ServerboundThirstMovementPacket;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.Difficulty;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
	@Shadow public Input input;
	public LocalPlayerMixin(ClientLevel pClientLevel, GameProfile pGameProfile) {
		super(pClientLevel, pGameProfile);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sendPosition()V"))
	public void tickInject(CallbackInfo ci) {
		if (this.tickCount%290 == 288) {
			if (this.level.getDifficulty() != Difficulty.PEACEFUL) {
				new ServerboundThirstMovementPacket(this.input.forwardImpulse, this.input.leftImpulse, this.input.jumping).send();
			}
		}
	}

}
