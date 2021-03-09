package com.stereowalker.survive.fluid;

import java.util.Random;

import javax.annotation.Nullable;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.blocks.SBlocks;
import com.stereowalker.survive.item.SItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class PurifiedWaterFluid extends FlowingFluid {
   public Fluid getFlowingFluid() {
      return SFluids.FLOWING_PURIFIED_WATER;
   }

   public Fluid getStillFluid() {
      return SFluids.PURIFIED_WATER;
   }

   public Item getFilledBucket() {
      return SItems.PURIFIED_WATER_BUCKET;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(World worldIn, BlockPos pos, FluidState state, Random random) {
      if (!state.isSource() && !state.get(FALLING)) {
         if (random.nextInt(64) == 0) {
            worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
         }
      } else if (random.nextInt(10) == 0) {
         worldIn.addParticle(ParticleTypes.UNDERWATER, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IParticleData getDripParticleData() {
      return ParticleTypes.DRIPPING_WATER;
   }

   protected boolean canSourcesMultiply() {
      return false;
   }

   @Override
   protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {
      TileEntity tileentity = state.hasTileEntity() ? worldIn.getTileEntity(pos) : null;
      Block.spawnDrops(state, worldIn.getWorld(), pos, tileentity);
   }

   public int getSlopeFindDistance(IWorldReader worldIn) {
      return 4;
   }
   
	@Override
	protected FluidAttributes createAttributes() {
		return net.minecraftforge.fluids.FluidAttributes.Water.builder(
				Survive.getInstance().location("block/purified_water_still"),
				Survive.getInstance().location("block/purified_water_flow"))
               .overlay(Survive.getInstance().location("block/purified_water_overlay"))
               .translationKey("block.survive.purified_water")
               .density(10)
               .viscosity(10)
               .luminosity(1)
               .build(this);
	}

   public BlockState getBlockState(IFluidState state) {
      return SBlocks.PURIFIED_WATER.getDefaultState().with(FlowingFluidBlock.LEVEL, Integer.valueOf(getLevelFromState(state)));
   }

   public boolean isEquivalentTo(Fluid fluidIn) {
      return fluidIn == SFluids.PURIFIED_WATER || fluidIn == SFluids.FLOWING_PURIFIED_WATER;
   }

   public int getLevelDecreasePerBlock(IWorldReader worldIn) {
      return 1;
   }

   public int getTickRate(IWorldReader p_205569_1_) {
      return 5;
   }

   public boolean canDisplace(IFluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction) {
	   //TODO: Change This when you figure out tags
      return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends PurifiedWaterFluid {
      protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
         super.fillStateContainer(builder);
         builder.add(LEVEL_1_8);
      }

      public int getLevel(IFluidState p_207192_1_) {
         return p_207192_1_.get(LEVEL_1_8);
      }

      public boolean isSource(IFluidState state) {
         return false;
      }
   }

   public static class Source extends PurifiedWaterFluid {
      public int getLevel(IFluidState p_207192_1_) {
         return 8;
      }

      public boolean isSource(IFluidState state) {
         return true;
      }
   }
}