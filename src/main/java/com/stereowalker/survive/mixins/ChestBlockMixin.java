package com.stereowalker.survive.mixins;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.survive.hooks.ColdStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends AbstractChestBlock<ChestBlockEntity> {
	protected ChestBlockMixin(Properties pProperties, Supplier<BlockEntityType<? extends ChestBlockEntity>> pBlockEntityType) {
		super(pProperties, pBlockEntityType);
	}

	@Override
	protected void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (pLevel.getBlockEntity(pPos) instanceof ChestBlockEntity pBlockEntity) {
			((ColdStorage)pBlockEntity).coldTick(pLevel);;
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private static void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo ci) {
		if (pLevel.getBlockEntity(pPos) instanceof ChestBlockEntity pBlockEntity) {
			((ColdStorage)pBlockEntity).coldTick(pLevel);;
		}
	}
}
