package com.stereowalker.survive.needs;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;

public interface IRoastedEntity {
	public static final int BASE_TICKS_REQUIRED_TO_ROAST = 140;
	public static final int ROAST_HURT_FREQUENCY = 40;
	public static final EntityDataAccessor<Integer> DATA_TICKS_ROASTED = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.INT);

	public abstract int getTicksRoasted();
	public abstract void setTicksRoasted(int pTicksFrozen);
	public abstract float getPercentRoasted();
	public abstract boolean isFullyRoasted();
	
	public default boolean canRoast() {
		return true;
	}

	public default int getTicksRequiredToRoast() {
		return 140;
	}
}
