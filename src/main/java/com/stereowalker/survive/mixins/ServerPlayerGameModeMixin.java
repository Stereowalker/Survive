package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.StaminaData;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
	/**
	 * If you want to do the same on the client, you should target PlayerController.func_217292_a
	 */
	@Inject(at = @At(value = "RETURN"), method = "useItemOn")
	public void mixinBlockInteraction(ServerPlayer playerIn, Level worldIn, ItemStack stackIn, InteractionHand handIn, BlockHitResult blockRaytraceResultIn, CallbackInfoReturnable<InteractionResult> cir) {
		if (cir.getReturnValue().consumesAction()) {
			StaminaData energyStats = SurviveEntityStats.getEnergyStats(playerIn);
			energyStats.addExhaustion(playerIn, Survive.CONFIG.stamina_drain_from_using_blocks, "used an item on something");
			SurviveEntityStats.setStaminaStats(playerIn, energyStats);
		}
	}
	
	/**
	 * If you want to do the same on the client, you should target PlayerController.processRightClick
	 */
	@Inject(at = @At(value = "RETURN"), method = "useItem")
	public void mixinItemInteraction(ServerPlayer player, Level worldIn, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		StaminaData energyStats = SurviveEntityStats.getEnergyStats(player);
		energyStats.addExhaustion(player, Survive.CONFIG.stamina_drain_from_items, "Used item in air");
		SurviveEntityStats.setStaminaStats(player, energyStats);
	}
}
