package com.stereowalker.survive.api.world.level.block;

import net.minecraft.world.level.block.state.BlockState;

public interface TemperatureEmitter {
	public float getTemperatureModification(BlockState pState);
	public float getModificationRange(BlockState pState);
}
