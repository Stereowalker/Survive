package com.stereowalker.survive.world.item;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.tags.FluidSTags;
import com.stereowalker.survive.world.item.alchemy.SPotions;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EmptyCanteenItem extends Item {

	public EmptyCanteenItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		HitResult hitresult = getPlayerPOVHitResult(levelIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
		int i = Survive.THIRST_CONFIG.canteen_fill_amount;
		if (hitresult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemstack);
		} else {
			if (hitresult.getType() == HitResult.Type.BLOCK) {
				BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
				if (!levelIn.mayInteract(playerIn, blockpos)) {
					return InteractionResultHolder.pass(itemstack);
				}

				if (levelIn.getFluidState(blockpos).is(FluidSTags.PURIFIED_WATER)) {
					//TODO: Replace with canteen fill sounds
					levelIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
					levelIn.gameEvent(playerIn, GameEvent.FLUID_PICKUP, blockpos);
					return InteractionResultHolder.sidedSuccess(this.turnCanteenIntoItem(itemstack, blockpos, playerIn, CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), i, SPotions.PURIFIED_WATER)), levelIn.isClientSide());
				}
				if (levelIn.getFluidState(blockpos).is(FluidTags.WATER)) {
					levelIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
					levelIn.gameEvent(playerIn, GameEvent.FLUID_PICKUP, blockpos);
					return InteractionResultHolder.sidedSuccess(this.turnCanteenIntoItem(itemstack, blockpos, playerIn, CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), i, Potions.WATER)), levelIn.isClientSide());
				}
				return InteractionResultHolder.pass(itemstack);
			}
			return InteractionResultHolder.pass(itemstack);
		}
	}

	protected ItemStack turnCanteenIntoItem(ItemStack canteenStack, BlockPos fillPos, Player pPlayer, ItemStack pFilledBottleStack) {
		pPlayer.awardStat(Stats.ITEM_USED.get(this));
		if (PotionUtils.getPotion(pFilledBottleStack) == Potions.WATER)
			pFilledBottleStack.getTag().putString("biome_source", pPlayer.level().getBiome(fillPos).unwrapKey().get().location().toString());
		return ItemUtils.createFilledResult(canteenStack, pPlayer, pFilledBottleStack);
	}

}
