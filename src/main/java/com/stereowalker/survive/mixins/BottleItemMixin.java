package com.stereowalker.survive.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(BottleItem.class)
public class BottleItemMixin {
	BlockPos fillPos = BlockPos.ZERO;
	@Inject(method = "use", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void turnBottleIntoItem(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder> cir, List $$3, ItemStack $$4, BlockHitResult $$7, BlockPos $$8) {
		fillPos = $$8;
	}

	@Inject(method = "turnBottleIntoItem", at = @At("HEAD"))
	public void turnBottleIntoItem(ItemStack pBottleStack, Player pPlayer, ItemStack pFilledBottleStack, CallbackInfoReturnable<ItemStack> cir) {
		if (PotionUtils.getPotion(pFilledBottleStack) == Potions.WATER)
			pFilledBottleStack.getTag().putString("biome_source", pPlayer.level().getBiome(fillPos).unwrapKey().get().location().toString());
	}
}
