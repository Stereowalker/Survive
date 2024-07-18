package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.survive.hooks.ColdStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BarrelBlock.class)
public abstract class BarrelBlockMixin extends BaseEntityBlock {
	protected BarrelBlockMixin(Properties pProperties) {
		super(pProperties);
	}

	@Override
	protected void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (pLevel.getBlockEntity(pPos) instanceof BarrelBlockEntity pBlockEntity) {
			((ColdStorage)pBlockEntity).coldTick(pLevel);;
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private static void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo ci) {
		if (pLevel.getBlockEntity(pPos) instanceof BarrelBlockEntity pBlockEntity) {
			((ColdStorage)pBlockEntity).coldTick(pLevel);;
		}
	}
}
