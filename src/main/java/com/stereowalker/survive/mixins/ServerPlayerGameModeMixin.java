package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
	@Shadow @Final protected ServerPlayer player;
	/**
	 * If you want to do the same on the client, you should target PlayerController.func_217292_a
	 */
	@Inject(at = @At(value = "RETURN"), method = "useItemOn")
	public void mixinBlockInteraction(ServerPlayer playerIn, Level worldIn, ItemStack stackIn, InteractionHand handIn, BlockHitResult blockRaytraceResultIn, CallbackInfoReturnable<InteractionResult> cir) {
		if (cir.getReturnValue().consumesAction()) {
			((IRealisticEntity)playerIn).addStaminaExhaustion(Survive.STAMINA_CONFIG.stamina_drain_from_using_blocks, "used an item on something", false);
		}
	}
	
	/**
	 * If you want to do the same on the client, you should target PlayerController.processRightClick
	 */
	@Inject(at = @At(value = "RETURN"), method = "useItem")
	public void mixinItemInteraction(ServerPlayer player, Level worldIn, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		((IRealisticEntity)player).addStaminaExhaustion(Survive.STAMINA_CONFIG.stamina_drain_from_items, "Used item in air", false);
	}
	
	/**
	 * Drains Stamina when we start to break a block. 
	 */
	@Inject(method = "handleBlockBreakAction", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
	public void exhaustOnBlockBreak(BlockPos pPos, ServerboundPlayerActionPacket.Action pAction, Direction pFace, int pMaxBuildHeight, int pSequence, CallbackInfo ci) {
		((IRealisticEntity)player).addStaminaExhaustion(0.06F, "Start Breaking block", true);
	}
	
	/**
	 * Drains Stamina when we try to break a block. 
	 */
	@Inject(method = "incrementDestroyProgress", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
	public void exhaustOnBlockBreak(BlockState pState, BlockPos pPos, int pStartTick, CallbackInfoReturnable<Float> cir) {
		((IRealisticEntity)player).addStaminaExhaustion(0.06F, "Contnue Breaking block", true);
	}
}
