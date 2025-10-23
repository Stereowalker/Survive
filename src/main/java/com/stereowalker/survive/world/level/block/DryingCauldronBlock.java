package com.stereowalker.survive.world.level.block;

import javax.annotation.Nullable;

import com.stereowalker.survive.core.cauldron.SCauldronInteraction;
import com.stereowalker.survive.damagesource.SDamageSources;
import com.stereowalker.survive.damagesource.SDamageTypes;
import com.stereowalker.survive.world.level.block.entity.DryingCauldronBlockEntity;
import com.stereowalker.survive.world.level.block.entity.SBlockEntityType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class DryingCauldronBlock extends LayeredCauldronBlock implements EntityBlock {
	public enum FluidToDry implements StringRepresentable { SEA_SALT, POTASH;

	@Override
	public String getSerializedName() {
		return switch (this) {
		case SEA_SALT -> "sea_salt";
		case POTASH -> "potash";
		};
	} }
	public static final IntegerProperty BOILING = IntegerProperty.create("boiling", 0, 10);
	public static final EnumProperty<FluidToDry> FLUID = EnumProperty.create("fluid", FluidToDry.class);
	
	public DryingCauldronBlock(BlockBehaviour.Properties properties) {
		super(Biome.Precipitation.NONE, SCauldronInteraction.DRYING, properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1).setValue(BOILING, 0).setValue(FLUID, FluidToDry.SEA_SALT));
	}

	@Override
	protected void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
		int boiling = pState.getValue(BOILING);
		if (boiling > 0 && this.isEntityInsideContent(pState, pPos, pEntity)) {
			pEntity.hurt(SDamageSources.source(pLevel.registryAccess(), SDamageTypes.BOIL), boiling / 5f);
		}
		super.entityInside(pState, pLevel, pPos, pEntity);
	}

	@Override
	protected int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		return pState.getValue(LEVEL);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(LEVEL, FLUID, BOILING);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos,
			Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
		
		if (!pLevel.isClientSide && DryingCauldronBlockEntity.setResult(pLevel, pStack, pPos, pPlayer, pHand)) {
			return ItemInteractionResult.sidedSuccess(pLevel.isClientSide);
		}
		else return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
	}

	//Drying cauldron
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? null : SaltBoxBlock.createTickerHelperExposed(pBlockEntityType, SBlockEntityType.DRYING_CAULDRON, (level, pos, state, blockentity) -> {
			blockentity.tick((ServerLevel) pLevel, state);
		});
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DryingCauldronBlockEntity(pos, state);
	}

    @Override
    public void animateTick(BlockState pState, Level level, BlockPos pPos, RandomSource random) {
    	int boil = pState.getValue(BOILING);
        double x = (double)pPos.getX() + .5f;
        double y = (double)pPos.getY() + getContentHeight(pState);
        double z = (double)pPos.getZ() + .5f;
    	if (boil > 0 && random.nextDouble() < 0.1) {
        	//TODO: Figure out boil soud
//            level.playLocalSound(x, y, z, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }
    	if (random.nextDouble() < 0.5)
    		for (int i = 0; i < boil; i++) {
    			double xOff = (random.nextDouble() * 0.9f) - .45f;
    			double zOff = (random.nextDouble() * 0.9f) - .45f;
    			if (boil % 2 == 0)
    				level.addParticle(ParticleTypes.BUBBLE_POP, x + xOff, y, z + zOff, 0.0, 0.0, 0.0);
    			else
    				level.addParticle(ParticleTypes.BUBBLE, x + xOff, y, z + zOff, 0.0, random.nextDouble() * 0.1f, 0.0);
    		}
    }

	/**
	 * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#hasAnalogOutputSignal} whenever possible. Implementing/overriding is fine.
	 */
	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
}
