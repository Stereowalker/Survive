package com.stereowalker.survive.world.level.material;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.level.block.SBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
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
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

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
	public void animateTick(Level worldIn, BlockPos pos, FluidState state, RandomSource pRandom) {
		if (!state.isSource() && !state.getValue(FALLING)) {
			if (pRandom.nextInt(64) == 0) {
				worldIn.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, pRandom.nextFloat() * 0.25F + 0.75F, pRandom.nextFloat() + 0.5F, false);
			}
		} else if (pRandom.nextInt(10) == 0) {
			worldIn.addParticle(ParticleTypes.UNDERWATER, (double)pos.getX() + pRandom.nextDouble(), (double)pos.getY() + pRandom.nextDouble(), (double)pos.getZ() + pRandom.nextDouble(), 0.0D, 0.0D, 0.0D);
		}

	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	@Override
	public ParticleOptions getDripParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean canConvertToSource(Level level) {
	      return level.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
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
	
	public static final FluidType TYPE = new FluidType(FluidType.Properties.create()
            .descriptionId("block.survive.purified_water")
            .fallDistanceModifier(0F)
            .canExtinguish(true)
            .canConvertToSource(true)
            .supportsBoating(true)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
            .canHydrate(true)
			.density(10)
			//.luminosity(1)
			//.color(0xff000000+Survive.PURIFIED_WATER_COLOR)
			.viscosity(10))
    {
        @Override
        public @Nullable BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog)
        {
            return canFluidLog ? super.getBlockPathType(state, level, pos, mob, true) : null;
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
        {
            consumer.accept(new IClientFluidTypeExtensions()
            {
                private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");

                @Override
                public ResourceLocation getStillTexture()
                {
                    return Survive.getInstance().location("block/purified_water_still");
                }

                @Override
                public ResourceLocation getFlowingTexture()
                {
                    return Survive.getInstance().location("block/purified_water_flow");
                }

                @Nullable
                @Override
                public ResourceLocation getOverlayTexture()
                {
                    return Survive.getInstance().location("block/purified_water_overlay");
                }

                @Override
                public ResourceLocation getRenderOverlayTexture(Minecraft mc)
                {
                    return UNDERWATER_LOCATION;
                }

                @Override
                public int getTintColor()
                {
                    return 0xFF3F76E4+Survive.PURIFIED_WATER_COLOR;
                }

                @Override
                public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos)
                {
                    return BiomeColors.getAverageWaterColor(getter, pos) | 0xFF000000+Survive.PURIFIED_WATER_COLOR;
                }
            });
        }
    };
	
	@Override
	public FluidType getFluidType() {
		return TYPE;
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