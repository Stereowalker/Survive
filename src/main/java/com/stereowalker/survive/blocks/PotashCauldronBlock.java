package com.stereowalker.survive.blocks;

import java.util.Random;

import com.stereowalker.survive.item.SItems;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class PotashCauldronBlock extends CauldronBlock {

	public PotashCauldronBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void fillWithRain(World worldIn, BlockPos pos) {
		super.fillWithRain(worldIn, pos);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack itemstack = player.getHeldItem(handIn);
		if (itemstack.isEmpty()) {
			return ActionResultType.PASS;
		} else {
			int i = state.get(LEVEL);
			Item item = itemstack.getItem();
			if (item == Items.GLASS_BOTTLE) {
				 if (i > 0 && !worldIn.isRemote) {
					 if (!player.abilities.isCreativeMode) {
						 ItemStack itemstack4 = new ItemStack(SItems.POTASH_SOLUTION);
						 player.addStat(Stats.USE_CAULDRON);
						 itemstack.shrink(1);
						 if (itemstack.isEmpty()) {
							 player.setHeldItem(handIn, itemstack4);
						 } else if (!player.inventory.addItemStackToInventory(itemstack4)) {
							 player.dropItem(itemstack4, false);
						 } else if (player instanceof ServerPlayerEntity) {
							 ((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
						 }
					 }

					 worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
					 if (state.get(LEVEL) == 1)
						 worldIn.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
					 else 
						 this.setWaterLevel(worldIn, pos, state, i - 1);
				 }

				 return ActionResultType.func_233537_a_(worldIn.isRemote);
			 } else if (item == SItems.POTASH_SOLUTION) {
				 if (i < 3 && !worldIn.isRemote) {
					 if (!player.abilities.isCreativeMode) {
						 ItemStack itemstack3 = new ItemStack(Items.GLASS_BOTTLE);
						 player.addStat(Stats.USE_CAULDRON);
						 player.setHeldItem(handIn, itemstack3);
						 if (player instanceof ServerPlayerEntity) {
							 ((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
						 }
					 }

					 worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
					 this.setWaterLevel(worldIn, pos, state, i + 1);
				 }

				 return ActionResultType.func_233537_a_(worldIn.isRemote);
			 } else {
				 return ActionResultType.PASS;
			 }
		}
	}
	
	@Override
	public boolean ticksRandomly(BlockState state) {
		return state.get(LEVEL) > 0;
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (worldIn.getLight(pos.up()) >= 12 && random.nextInt(10) == 0 && state.get(LEVEL) > 0) {
			worldIn.summonEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(SItems.POTASH)));
			if (state.get(LEVEL) == 1)
				 worldIn.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
			 else 
				 this.setWaterLevel(worldIn, pos, state, state.get(LEVEL) - 1);
		}
	}

}
