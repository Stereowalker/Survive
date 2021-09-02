package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.StaminaStats;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

@Mixin(PlayerInteractionManager.class)
public abstract class PlayerInteractionManagerMixin {
	/**
	 * If you want to do the same on the client, you should target PlayerController.func_217292_a
	 */
	@Inject(at = @At(value = "RETURN"), method = "func_219441_a")
	public void mixinBlockInteraction(ServerPlayerEntity playerIn, World worldIn, ItemStack stackIn, Hand handIn, BlockRayTraceResult blockRaytraceResultIn, CallbackInfoReturnable<ActionResultType> cir) {
		if (cir.getReturnValue().isSuccessOrConsume()) {
			StaminaStats energyStats = SurviveEntityStats.getEnergyStats(playerIn);
			energyStats.addExhaustion(playerIn, Config.stamina_drain_from_using_blocks);
			SurviveEntityStats.setStaminaStats(playerIn, energyStats);
		}
	}
	
	/**
	 * If you want to do the same on the client, you should target PlayerController.processRightClick
	 */
	@Inject(at = @At(value = "RETURN"), method = "processRightClick")
	public void mixinItemInteraction(ServerPlayerEntity player, World worldIn, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
		StaminaStats energyStats = SurviveEntityStats.getEnergyStats(player);
		energyStats.addExhaustion(player, Config.stamina_drain_from_items);
		SurviveEntityStats.setStaminaStats(player, energyStats);
	}
}
