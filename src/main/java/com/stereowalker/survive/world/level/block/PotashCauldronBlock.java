package com.stereowalker.survive.world.level.block;

import java.util.Random;
import java.util.function.Predicate;

import com.stereowalker.survive.core.cauldron.SCauldronInteraction;
import com.stereowalker.survive.world.item.HygieneItems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class PotashCauldronBlock extends LayeredCauldronBlock {

	public static final Predicate<Biome.Precipitation> NONE = (p_153526_) -> {
		return false;
	};

	public PotashCauldronBlock(BlockBehaviour.Properties properties) {
		super(properties, NONE, SCauldronInteraction.POTASH);
	}

	@Override
	public void handlePrecipitation(BlockState pState, Level pLevel, BlockPos pPos, Precipitation pPrecipitation) {

	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return state.getValue(LEVEL) > 0;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
		if (isUnderSun(state, worldIn, pos) && random.nextInt(2) == 0) {
			worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(HygieneItems.POTASH), 0, 0, 0));
			lowerFillLevel(state, worldIn, pos);
		}
		else if (!worldIn.isClientSide && worldIn.getMaxLocalRawBrightness(pos.above()) >= 12 && random.nextInt(10) == 0) {
			worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(HygieneItems.POTASH), 0, 0, 0));
			lowerFillLevel(state, worldIn, pos);
		}
	}

	protected boolean isUnderSun(BlockState state, ServerLevel level, BlockPos pos) {
		if (level.isDay() && !level.isClientSide) {
			float f = this.getBrightness(level, pos);
			boolean flag = level.isRainingAt(pos);
			if (f > 0.5F && !flag && level.canSeeSky(pos)) {
				return true;
			}
		}

		return false;
	}

	public float getBrightness(ServerLevel level, BlockPos pos) {
		return level.hasChunkAt(pos) ? level.getBrightness(pos) : 0.0F;
	}

}
