package com.stereowalker.survive.world.item;

import com.stereowalker.survive.Survive;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EmptyCanteenItem extends Item {

	public EmptyCanteenItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
		BlockPos blockpos = ((BlockHitResult)raytraceresult).getBlockPos();
		if (worldIn.getFluidState(blockpos).is(FluidTags.WATER)) {
			playerIn.setItemInHand(handIn, CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.WATER_CANTEEN), Survive.CONFIG.canteen_fill_amount));
		}
		return super.use(worldIn, playerIn, handIn);
	}

}
