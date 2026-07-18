package com.stereowalker.survive.world.level.block;

import java.util.Optional;

import javax.annotation.Nullable;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.level.block.entity.RealisticCampfireBlockEntity;
import com.stereowalker.survive.world.level.block.entity.SBlockEntityType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RealisticCampfireBlock extends CampfireBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final IntegerProperty HEAT = IntegerProperty.create("heat", 0, 4);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape VIRTUAL_FENCE_POST = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    private static final int SMOKE_DISTANCE = 5;


    public RealisticCampfireBlock(boolean p_51236_, int p_51237_, BlockBehaviour.Properties p_51238_) {
        super(p_51236_, p_51237_, p_51238_);
        this.registerDefaultState(
            this.stateDefinition
                .any().setValue(LIT, false).setValue(HEAT, 0)
                .setValue(SIGNAL_FIRE, Boolean.valueOf(false))
                .setValue(WATERLOGGED, Boolean.valueOf(false))
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack pStack, BlockState pState, Level level, BlockPos pPos, Player player, InteractionHand hand, BlockHitResult pHitResult
    ) {
        if (level.getBlockEntity(pPos) instanceof RealisticCampfireBlockEntity campfireblockentity) {
            ItemStack itemInHand = player.getItemInHand(hand);
            if (level.recipeAccess().propertySet(RecipePropertySet.CAMPFIRE_INPUT).test(itemInHand)) {
                if (level instanceof ServerLevel serverLevel && campfireblockentity.placeFood(serverLevel, player, itemInHand)) {
                    player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return InteractionResult.SUCCESS_SERVER;
                }

                return InteractionResult.CONSUME;
            }
            if (level.fuelValues().isFuel(pStack)) {
            	if (!level.isClientSide() && campfireblockentity.placeFuel(player, itemInHand)) {
            		player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (pState.getValue(HEAT) > 0 && pEntity instanceof LivingEntity) {
            pEntity.hurt(pLevel.damageSources().campfire(), pState.getValue(HEAT) * .25f);
        }

        super.entityInside(pState, pLevel, pPos, pEntity, effectApplier, isPrecise);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        LevelAccessor levelaccessor = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        boolean flag = levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER;
        return this.defaultBlockState()
            .setValue(WATERLOGGED, Boolean.valueOf(flag))
            .setValue(SIGNAL_FIRE, Boolean.valueOf(this.isSmokeSource(levelaccessor.getBlockState(blockpos.below()))))
            .setValue(LIT, false).setValue(HEAT, 0)
            .setValue(FACING, pContext.getHorizontalDirection());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return directionToNeighbour == Direction.DOWN
            ? state.setValue(SIGNAL_FIRE, this.isSmokeSource(neighbourState))
            : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    private boolean isSmokeSource(BlockState pState) {
        return pState.is(Blocks.HAY_BLOCK);
    }

    public static void dowse(@Nullable Entity pEntity, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) {
            for (int i = 0; i < 20; i++) {
                makeParticles((Level)pLevel, pPos, pState.getValue(SIGNAL_FIRE), true);
            }
        }

        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof RealisticCampfireBlockEntity) {
            ((RealisticCampfireBlockEntity)blockentity).dowse();
        }

        pLevel.gameEvent(pEntity, GameEvent.BLOCK_CHANGE, pPos);
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        if (!pState.getValue(BlockStateProperties.WATERLOGGED) && pFluidState.getType() == Fluids.WATER) {
            boolean flag = pState.getValue(HEAT) > 0;
            if (flag) {
                if (!pLevel.isClientSide()) {
                    pLevel.playSound(null, pPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                dowse(null, pLevel, pPos, pState);
            }

            pLevel.setBlock(pPos, pState.setValue(WATERLOGGED, Boolean.valueOf(true)).setValue(LIT, false).setValue(HEAT, 0), 3);
            pLevel.scheduleTick(pPos, pFluidState.getType(), pFluidState.getType().getTickDelay(pLevel));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onProjectileHit(Level p_level, BlockState p_state, BlockHitResult hit, Projectile p_projectile) {
        BlockPos pos = hit.getBlockPos();
        if (p_level instanceof ServerLevel serverLevel && p_projectile.isOnFire() && p_projectile.mayInteract(serverLevel, pos) && !p_state.getValue(LIT) && !p_state.getValue(WATERLOGGED)) {
            p_level.setBlock(pos, p_state.setValue(BlockStateProperties.LIT, true), 11);
        }
    }

    public static void makeParticles(Level pLevel, BlockPos pPos, boolean pIsSignalFire, boolean pSpawnExtraSmoke) {
        RandomSource randomsource = pLevel.getRandom();
        SimpleParticleType simpleparticletype = pIsSignalFire ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        pLevel.addAlwaysVisibleParticle(
            simpleparticletype,
            true,
            (double)pPos.getX() + 0.5 + randomsource.nextDouble() / 3.0 * (double)(randomsource.nextBoolean() ? 1 : -1),
            (double)pPos.getY() + randomsource.nextDouble() + randomsource.nextDouble(),
            (double)pPos.getZ() + 0.5 + randomsource.nextDouble() / 3.0 * (double)(randomsource.nextBoolean() ? 1 : -1),
            0.0,
            0.07,
            0.0
        );
        if (pSpawnExtraSmoke) {
            pLevel.addParticle(
                ParticleTypes.SMOKE,
                (double)pPos.getX() + 0.5 + randomsource.nextDouble() / 4.0 * (double)(randomsource.nextBoolean() ? 1 : -1),
                (double)pPos.getY() + 0.4,
                (double)pPos.getZ() + 0.5 + randomsource.nextDouble() / 4.0 * (double)(randomsource.nextBoolean() ? 1 : -1),
                0.0,
                0.005,
                0.0
            );
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING, HEAT);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RealisticCampfireBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return pState.getValue(HEAT) > 0 ? createTickerHelper(pBlockEntityType, SBlockEntityType.REALISIC_CAMPFIRE, RealisticCampfireBlockEntity::particleTick) : null;
        } else {
        	RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
            return pState.getValue(HEAT) > 0
                ? createTickerHelper(pBlockEntityType, SBlockEntityType.REALISIC_CAMPFIRE, (innerLevel, pos, state, entity) -> RealisticCampfireBlockEntity.cookTick((ServerLevel)pLevel, pos, state, entity, quickCheck))
                : createTickerHelper(pBlockEntityType, SBlockEntityType.REALISIC_CAMPFIRE, RealisticCampfireBlockEntity::cooldownTick);
        }
    }
}
