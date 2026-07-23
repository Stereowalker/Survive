package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(BucketItem.class)
public class BucketItemMixin {
	@Inject(method = "use", at = @At(value = "INVOKE_ASSIGN", ordinal = 1, target = "Lnet/minecraft/world/item/ItemUtils;createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void turnBottle(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, ItemStack itemStack, BlockHitResult hitResult, BlockPos pos, Direction direction, BlockPos directionOffsetPos, BlockState clicked, BlockPos placePos, BlockState blockState, BucketPickup bucketPickupBlock, ItemStack taken, ItemStack result) {
		SDataComponents.BIOME_SOURCE_D.setData(result, player.level().getBiome(pos).unwrapKey().get().identifier());
	}
}
