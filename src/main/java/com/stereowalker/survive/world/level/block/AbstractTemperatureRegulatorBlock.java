package com.stereowalker.survive.world.level.block;

import javax.annotation.Nullable;

import com.stereowalker.survive.world.item.TemperatureRegulatorPlateItem;
import com.stereowalker.survive.world.level.block.state.properties.SBlockStateProperties;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateSize;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateType;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractTemperatureRegulatorBlock extends Block {
	public static final EnumProperty<TempRegulationPlateSize> RADIATOR_SIZE = SBlockStateProperties.TEMP_REG_SIZE;
	public static final IntegerProperty PLATE_COUNT = SBlockStateProperties.PLATE_COUNT;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final DirectionProperty FACING = DirectionalBlock.FACING;

	public AbstractTemperatureRegulatorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(RADIATOR_SIZE, TempRegulationPlateSize.SMALL).setValue(PLATE_COUNT, Integer.valueOf(0)).setValue(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

	public abstract boolean canAddPlate(BlockState pState, ItemStack plate);
	public abstract boolean canRemovePlates(BlockState pState);
	public abstract ItemStack getPlateStack(BlockState pState);

	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		int plate_count = pState.getValue(PLATE_COUNT);
		if (plate_count < 4 && canAddPlate(pState, pPlayer.getItemInHand(pHand))) {
			return handlePlates(pPlayer.getItemInHand(pHand), pState, pLevel, pPos, true);
		} else if (plate_count > 0 && canRemovePlates(pState) && pPlayer.getItemInHand(pHand).isEmpty()) {
			pPlayer.addItem(getPlateStack(pState));
			return handlePlates(pPlayer.getItemInHand(pHand), pState, pLevel, pPos, false);
		} else {
			return InteractionResult.PASS;
		}
	}

	public InteractionResult handlePlates(ItemStack plate, BlockState pState, Level pLevel, BlockPos pPos, boolean add) {
		int plate_count = pState.getValue(PLATE_COUNT);
		int newCount = plate_count+(add?1:-1);

		boolean alteredPlates = false;

		TempRegulationPlateType newState = null;
		TempRegulationPlateSize newSize = null;
		if (plate.getItem() instanceof TemperatureRegulatorPlateItem) {
			newState = ((TemperatureRegulatorPlateItem)plate.getItem()).getType();
			newSize = ((TemperatureRegulatorPlateItem)plate.getItem()).getSize();
		}

		if (add) {
			plate.shrink(1);
		}
		if (add && plate_count == 0) {
			pLevel.setBlockAndUpdate(pPos, SBlocks.PLATED_TEMPERATURE_REGULATOR.defaultBlockState().setValue(FACING, pState.getValue(FACING)).setValue(PlatedTemperatureRegulatorBlock.TEMP_REG_TYPE, newState).setValue(RADIATOR_SIZE, newSize).setValue(PLATE_COUNT, newCount).setValue(PlatedTemperatureRegulatorBlock.POWERED, Boolean.valueOf(PlatedTemperatureRegulatorBlock.hasPowerInEitherDirection(pLevel, pPos, pState.getValue(FACING)))));
			alteredPlates = true;
		} else if (!add && plate_count == 1) {
			pLevel.setBlockAndUpdate(pPos, SBlocks.TEMPERATURE_REGULATOR.defaultBlockState().setValue(FACING, pState.getValue(FACING)).setValue(RADIATOR_SIZE, TempRegulationPlateSize.SMALL).setValue(PLATE_COUNT, 0));
			alteredPlates = true;
		} else {
			pLevel.setBlockAndUpdate(pPos, pState.setValue(PLATE_COUNT, newCount));
			alteredPlates = true;
		}

		if (alteredPlates) {
			pLevel.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F + pLevel.random.nextFloat(), 0, false);
		}
		return InteractionResult.sidedSuccess(pLevel.isClientSide);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(RADIATOR_SIZE, FACING, PLATE_COUNT, WATERLOGGED);
	}

}
