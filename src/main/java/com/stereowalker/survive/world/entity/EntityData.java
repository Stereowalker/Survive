package com.stereowalker.survive.world.entity;

import com.stereowalker.unionlib.network.syncher.RevisedSynchedEntityData;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;

public class EntityData {
	public static final EntityDataAccessor<Integer> DATA_TICKS_ROASTED = RevisedSynchedEntityData.defineId(VersionHelper.toLoc("survive:data_ticks_roasted"), EntityDataSerializers.INT);
}
