package com.stereowalker.survive.api.needs;

import net.minecraft.world.entity.LivingEntity;

public abstract class PlayerNeeds {
	public abstract Temperature getTemperature(LivingEntity entity);
	public abstract Stamina getStamina(LivingEntity entity);
	public abstract Water getWater(LivingEntity entity);
	
	public static PlayerNeeds needsApi;
}
