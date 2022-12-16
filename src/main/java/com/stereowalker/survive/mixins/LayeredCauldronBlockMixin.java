package com.stereowalker.survive.mixins;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;

import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.world.level.block.SBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LayeredCauldronBlock.class)
public abstract class LayeredCauldronBlockMixin extends AbstractCauldronBlock {

	public LayeredCauldronBlockMixin(Properties pProperties, Map<Item, CauldronInteraction> pInteractions) {
		super(pProperties, pInteractions);
	}

	@Override
	public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
		if (!pLevel.isClientSide) {
			heatOrCoolCauldron(pState, pLevel, pPos);
		}
		super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
	}
	
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
			boolean pIsMoving) {
		if (!pLevel.isClientSide) {
			heatOrCoolCauldron(pState, pLevel, pPos);
		}
		super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
	}
	
	public void heatOrCoolCauldron(BlockState pState, Level pLevel, BlockPos cauldronPos) {
		if (pState.getBlock() == Blocks.WATER_CAULDRON && 
				pLevel.getBlockState(cauldronPos.below()).getBlock() instanceof CampfireBlock && 
				pLevel.getBlockState(cauldronPos.below()).getValue(CampfireBlock.LIT)) {
			pLevel.setBlockAndUpdate(cauldronPos, SBlocks.PURIFIED_WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, pState.getValue(LayeredCauldronBlock.LEVEL)));
		}
		if (ServerConfig.purifiedCauldronRevert && 
				pState.getBlock() == SBlocks.PURIFIED_WATER_CAULDRON && 
				(!(pLevel.getBlockState(cauldronPos.below()).getBlock() instanceof CampfireBlock) || !pLevel.getBlockState(cauldronPos.below()).getValue(CampfireBlock.LIT))) {
			pLevel.setBlockAndUpdate(cauldronPos,Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, pState.getValue(LayeredCauldronBlock.LEVEL)));
		}
	}

}
