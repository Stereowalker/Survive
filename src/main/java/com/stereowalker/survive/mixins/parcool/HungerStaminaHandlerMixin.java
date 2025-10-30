package com.stereowalker.survive.mixins.parcool;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.payload.StaminaProcessOnServerPayload;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.common.stamina.handlers.HungerStaminaHandler;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.needs.PlayerNeeds;
import com.stereowalker.survive.needs.StaminaData;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@Mixin(HungerStaminaHandler.class)
public class HungerStaminaHandlerMixin implements IParCoolStaminaHandler {
	@Shadow private int consumed = 0;

	@Overwrite
	@OnlyIn(value = Dist.CLIENT)
	public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current) {
		if (Survive.STAMINA_CONFIG.parcool)
			return new ReadonlyStamina(false, PlayerNeeds.api().getStamina(player).getBurstStamina(), 20);
		else
			return new ReadonlyStamina(false, player.getFoodData().getFoodLevel(), 20);
	}

	@Overwrite
	@OnlyIn(value = Dist.CLIENT)
	public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value) {
		this.consumed += value;
		return current;
	}

	@Overwrite
	@OnlyIn(value = Dist.CLIENT)
	public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value) {
		return current;
	}

	@Overwrite
	@OnlyIn(value = Dist.CLIENT)
	public ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
		if (this.consumed > 0) {
			PacketDistributor.sendToServer(
					(CustomPacketPayload) new StaminaProcessOnServerPayload(StaminaType.HUNGER, this.consumed),
					(CustomPacketPayload[]) new CustomPacketPayload[0]);
			this.consumed = 0;
		}
		if (Survive.STAMINA_CONFIG.parcool)
			return new ReadonlyStamina(((StaminaData)PlayerNeeds.api().getStamina(player)).isShortOfBreath(), PlayerNeeds.api().getStamina(player).getBurstStamina(), 20);
		else
			return new ReadonlyStamina(player.getFoodData().getFoodLevel() < 6, player.getFoodData().getFoodLevel(), 20);
	}
	
	@Inject(method = "processOnServer", at = @At("HEAD"), cancellable = true)
	public void processOnServer_inject(Player player, int value, CallbackInfo ci) {
		if (Survive.STAMINA_CONFIG.parcool) {
			((StaminaData)PlayerNeeds.api().getStamina(player)).addExhaustion((float) value / 1000.0f, true);
			ci.cancel();
		}
	}
}