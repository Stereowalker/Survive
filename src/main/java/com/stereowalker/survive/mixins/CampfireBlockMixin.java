package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.world.level.block.SBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BaseEntityBlock implements SimpleWaterloggedBlock {

	protected CampfireBlockMixin(Properties p_49224_) {
		super(p_49224_);
	}

	@Inject(method = "onRemove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
	public void onUnpurifyWater(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving, CallbackInfo info) {
		if (!pLevel.isClientSide && ServerConfig.purifiedCauldronRevert) {
			if (pLevel.getBlockState(pPos.above()).getBlock() == SBlocks.PURIFIED_WATER_CAULDRON) {
				BlockState old = pLevel.getBlockState(pPos.above());
				pLevel.setBlockAndUpdate(pPos.above(), Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, old.getValue(LayeredCauldronBlock.LEVEL)));
			}
		}
	}
	
	@Override
	public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
		if (!pLevel.isClientSide) {
			if (pLevel.getBlockState(pPos.above()).getBlock() == Blocks.WATER_CAULDRON) {
				BlockState old = pLevel.getBlockState(pPos.above());
				pLevel.setBlockAndUpdate(pPos.above(), SBlocks.PURIFIED_WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, old.getValue(LayeredCauldronBlock.LEVEL)));
			}
		}
		super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
	}


}
