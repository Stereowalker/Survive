package com.stereowalker.survive.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.stereowalker.survive.events.SurviveEvents;
import com.stereowalker.survive.world.item.alchemy.SPotions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

@Mixin(BottleItem.class)
public class BottleItemMixin {
	@Shadow protected ItemStack turnBottleIntoItem(ItemStack pBottleStack, Player pPlayer, ItemStack pFilledBottleStack) {return ItemStack.EMPTY;}
	@Inject(method = "use", at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/core/BlockPos;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void s(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, List<?> list, ItemStack itemstack, HitResult hitresult, BlockPos blockpos) {
		if (!SurviveEvents.getRegisteredThirstEffect(pLevel.getFluidState(blockpos).getType())) {
			cir.setReturnValue(InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem(itemstack, pPlayer, PotionUtils.setPotion(new ItemStack(Items.POTION), SPotions.PURIFIED_WATER)), pLevel.isClientSide()));
		}
	}
}
