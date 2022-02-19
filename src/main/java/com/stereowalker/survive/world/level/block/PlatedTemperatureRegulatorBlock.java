package com.stereowalker.survive.world.level.block;

import javax.annotation.Nullable;

import com.stereowalker.survive.api.world.level.block.TemperatureEmitter;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.TemperatureRegulatorPlateItem;
import com.stereowalker.survive.world.level.block.state.properties.SBlockStateProperties;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateSize;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class PlatedTemperatureRegulatorBlock extends AbstractTemperatureRegulatorBlock implements TemperatureEmitter {
	public static final EnumProperty<TempRegulationPlateType> TEMP_REG_TYPE = SBlockStateProperties.TEMP_REG_TYPE;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public PlatedTemperatureRegulatorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(TEMP_REG_TYPE, TempRegulationPlateType.HEATER).setValue(RADIATOR_SIZE, TempRegulationPlateSize.LARGE).setValue(PLATE_COUNT, Integer.valueOf(0)).setValue(POWERED, Boolean.valueOf(false)).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite()).setValue(POWERED, Boolean.valueOf(hasPowerInEitherDirection(pContext.getLevel(), pContext.getClickedPos(), pContext.getClickedFace())));
	}

	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
		if (hasPowerInEitherDirection(pLevel, pPos, pState.getValue(FACING)) && !pState.getValue(POWERED)) {
			pLevel.setBlockAndUpdate(pPos, pState.setValue(POWERED, true));
		} else if (!hasPowerInEitherDirection(pLevel, pPos, pState.getValue(FACING)) && pState.getValue(POWERED)) {
			pLevel.setBlockAndUpdate(pPos, pState.setValue(POWERED, false));
		}
	}
	
	public static boolean hasPowerInEitherDirection(Level pLevel, BlockPos pPos, Direction pDirection) {
		return pLevel.hasSignal(pPos.relative(pDirection), pDirection) || pLevel.hasSignal(pPos.relative(pDirection.getOpposite()), pDirection.getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(TEMP_REG_TYPE, RADIATOR_SIZE, PLATE_COUNT, FACING, POWERED, WATERLOGGED);
	}

	@Override
	public boolean canAddPlate(BlockState pState, ItemStack plate) {
		TempRegulationPlateType newControl = null;
		TempRegulationPlateSize newSize = null;
		if (plate.getItem() instanceof TemperatureRegulatorPlateItem) {
			newControl = ((TemperatureRegulatorPlateItem)plate.getItem()).getType();
			newSize = ((TemperatureRegulatorPlateItem)plate.getItem()).getSize();
		}
		return pState.getValue(TEMP_REG_TYPE) == newControl && pState.getValue(RADIATOR_SIZE) == newSize;
	}

	public static int getColor(BlockState pState) {
		if (pState.getBlock() instanceof PlatedTemperatureRegulatorBlock) {
			if (pState.getValue(POWERED)) {
				switch (pState.getValue(TEMP_REG_TYPE)) {
				case HEATER:
					return 0xbd6d3f;
				case CHILLER:
					return 0x7ab2d3;
				}
			} else return pState.getValue(TEMP_REG_TYPE).getColor();
		}
		return 0xFFFFFF;
	}

	@Override
	public boolean canRemovePlates(BlockState pState) {
		return true;
	}

	@Override
	public ItemStack getPlateStack(BlockState pState) {
		if (pState.getValue(RADIATOR_SIZE) == TempRegulationPlateSize.LARGE && pState.getValue(TEMP_REG_TYPE) == TempRegulationPlateType.HEATER) {
			return new ItemStack(SItems.LARGE_HEATING_PLATE);
		} else if (pState.getValue(RADIATOR_SIZE) == TempRegulationPlateSize.LARGE && pState.getValue(TEMP_REG_TYPE) == TempRegulationPlateType.CHILLER) {
			return new ItemStack(SItems.LARGE_COOLING_PLATE);
		} else if (pState.getValue(RADIATOR_SIZE) == TempRegulationPlateSize.MEDIUM && pState.getValue(TEMP_REG_TYPE) == TempRegulationPlateType.HEATER) {
			return new ItemStack(SItems.MEDIUM_HEATING_PLATE);
		} else if (pState.getValue(RADIATOR_SIZE) == TempRegulationPlateSize.MEDIUM && pState.getValue(TEMP_REG_TYPE) == TempRegulationPlateType.CHILLER) {
			return new ItemStack(SItems.MEDIUM_COOLING_PLATE);
		} else if (pState.getValue(RADIATOR_SIZE) == TempRegulationPlateSize.SMALL && pState.getValue(TEMP_REG_TYPE) == TempRegulationPlateType.HEATER) {
			return new ItemStack(SItems.SMALL_HEATING_PLATE);
		} else if (pState.getValue(RADIATOR_SIZE) == TempRegulationPlateSize.SMALL && pState.getValue(TEMP_REG_TYPE) == TempRegulationPlateType.CHILLER) {
			return new ItemStack(SItems.SMALL_COOLING_PLATE);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public float getTemperatureModification(BlockState pState) {
		if (pState.getValue(PlatedTemperatureRegulatorBlock.POWERED)) {
			float blockTemp = pState.getValue(AbstractTemperatureRegulatorBlock.PLATE_COUNT) * pState.getValue(AbstractTemperatureRegulatorBlock.RADIATOR_SIZE).getModifier();
			switch (pState.getValue(TEMP_REG_TYPE)) {
			case CHILLER:
				blockTemp*=-1;
				break;
			case HEATER:
				blockTemp*=1;
				break;
			default:
				blockTemp*=0;
				break;
			}
			return blockTemp;
		} else return 0;
	}

	@Override
	public float getModificationRange(BlockState pState) {
		return pState.getValue(AbstractTemperatureRegulatorBlock.RADIATOR_SIZE).getRange();
	}

}
