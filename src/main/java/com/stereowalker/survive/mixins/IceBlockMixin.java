package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin extends HalfTransparentBlock {

	public IceBlockMixin(BlockBehaviour.Properties p_54155_) {
		super(p_54155_);
	}
	
	@ModifyArg(method = "<init>", at = @At(value= "INVOKE", target = "Lnet/minecraft/world/level/block/HalfTransparentBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"), index = 0)
	private static BlockBehaviour.Properties adjustYCoord(BlockBehaviour.Properties y) {
	    return y.requiresCorrectToolForDrops().strength(0.1f);
	}
	

}
