package com.stereowalker.survive.world.level.block;

import javax.annotation.Nullable;

import com.stereowalker.survive.world.level.block.entity.SaltBoxBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class SaltBoxBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

   public SaltBoxBlock(BlockBehaviour.Properties pProperties) {
      super(pProperties);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)));
   }

   public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      if (pLevel.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockEntity blockentity = pLevel.getBlockEntity(pPos);
         if (blockentity instanceof SaltBoxBlockEntity) {
            pPlayer.openMenu((SaltBoxBlockEntity)blockentity);
            pPlayer.awardStat(Stats.OPEN_BARREL);
            PiglinAi.angerNearbyPiglins(pPlayer, true);
         }

         return InteractionResult.CONSUME;
      }
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pState.is(pNewState.getBlock())) {
         BlockEntity blockentity = pLevel.getBlockEntity(pPos);
         if (blockentity instanceof Container) {
            Containers.dropContents(pLevel, pPos, (Container)blockentity);
            pLevel.updateNeighbourForOutputSignal(pPos, this);
         }

         super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
      }
   }

   @Override
   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
	   BlockEntity blockentity = level.getBlockEntity(pos);
	   if (blockentity instanceof SaltBoxBlockEntity saltBox) {
		   saltBox.recheckOpen();
		   saltBox.massivelyExtendLifespan();
	   }
   }

   @Override
   public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
	   BlockEntity blockentity = pLevel.getBlockEntity(pPos);
	   if (blockentity instanceof SaltBoxBlockEntity saltBox) {
		   saltBox.massivelyExtendLifespan();
	   }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new SaltBoxBlockEntity(pPos, pState);
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getRenderShape}
    * whenever possible. Implementing/overriding is fine.
    */
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   /**
    * Called by BlockItem after this block has been placed.
    */
   public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      if (pStack.hasCustomHoverName()) {
         BlockEntity blockentity = pLevel.getBlockEntity(pPos);
         if (blockentity instanceof SaltBoxBlockEntity) {
            ((SaltBoxBlockEntity)blockentity).setCustomName(pStack.getHoverName());
         }
      }

   }

   /**
    * @deprecated call via {@link
    * net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#hasAnalogOutputSignal} whenever possible.
    * Implementing/overriding is fine.
    */
   public boolean hasAnalogOutputSignal(BlockState pState) {
      return true;
   }

   /**
    * Returns the analog signal this block emits. This is the signal a comparator can read from it.
    * 
    * @deprecated call via {@link
    * net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getAnalogOutputSignal} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#rotate} whenever
    * possible. Implementing/overriding is fine.
    */
   public BlockState rotate(BlockState pState, Rotation pRotation) {
      return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#mirror} whenever
    * possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState pState, Mirror pMirror) {
      return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
      pBuilder.add(FACING, OPEN);
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return this.defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
   }
	
	public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelperExposed(
	        BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker
	    ) {
	        return createTickerHelper(pServerType, pClientType, pTicker);
	    }
}
