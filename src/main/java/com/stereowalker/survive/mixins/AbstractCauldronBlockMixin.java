package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.survive.world.level.block.entity.DryingCauldronBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlockMixin {
	@Inject(method = "useItemOn", at = @At(value = "HEAD"), cancellable = true)
	public void useItemOn_inject(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult, CallbackInfoReturnable<ItemInteractionResult> cir) {
		if (pState.getBlock() == Blocks.CAULDRON && !pLevel.isClientSide && DryingCauldronBlockEntity.setResult(pLevel, pStack, pPos, pPlayer, pHand)) {
			cir.setReturnValue(ItemInteractionResult.sidedSuccess(pLevel.isClientSide));
		}
	}
}
