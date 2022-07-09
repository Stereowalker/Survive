package com.stereowalker.survive.world.level.material;

import java.util.Random;

import javax.annotation.Nullable;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.level.block.SBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class PurifiedWaterFluid extends FlowingFluid {
	@Override
	public Fluid getFlowing() {
		return SFluids.FLOWING_PURIFIED_WATER;
	}

	@Override
	public Fluid getSource() {
		return SFluids.PURIFIED_WATER;
	}

	@Override
	public Item getBucket() {
		return SItems.PURIFIED_WATER_BUCKET;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(Level worldIn, BlockPos pos, FluidState state, Random random) {
		if (!state.isSource() && !state.getValue(FALLING)) {
			if (random.nextInt(64) == 0) {
				worldIn.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
			}
		} else if (random.nextInt(10) == 0) {
			worldIn.addParticle(ParticleTypes.UNDERWATER, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
		}

	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	@Override
	public ParticleOptions getDripParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean canConvertToSource() {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
		BlockEntity tileentity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
		Block.dropResources(state, worldIn, pos, tileentity);
	}

	@Override
	public int getSlopeFindDistance(LevelReader worldIn) {
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
				.color(0xff000000+Survive.PURIFIED_WATER_COLOR)
				.build(this);
	}

	@Override
	public BlockState createLegacyBlock(FluidState state) {
		return SBlocks.PURIFIED_WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(state)));
	}

	@Override
	public boolean isSame(Fluid fluidIn) {
		return fluidIn == SFluids.PURIFIED_WATER || fluidIn == SFluids.FLOWING_PURIFIED_WATER;
	}

	@Override
	public int getDropOff(LevelReader worldIn) {
		return 1;
	}

	@Override
	public int getTickDelay(LevelReader p_205569_1_) {
		return 5;
	}

	@Override
	public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
		//TODO: Change This when you figure out tags
		return direction == Direction.DOWN && !fluid.is(FluidTags.WATER);
	}

	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}

	public static class Flowing extends PurifiedWaterFluid {
		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState p_207192_1_) {
			return p_207192_1_.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Source extends PurifiedWaterFluid {
		@Override
		public int getAmount(FluidState p_207192_1_) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}
}