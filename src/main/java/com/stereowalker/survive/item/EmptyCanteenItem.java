package com.stereowalker.survive.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EmptyCanteenItem extends Item{

	public EmptyCanteenItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
		BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
		if (worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
			playerIn.setHeldItem(handIn, CanteenItem.addPropertiesToCanteen(new ItemStack(SItems.WATER_CANTEEN), 3));
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

}
