package com.stereowalker.survive.hooks;

import com.stereowalker.survive.api.event.TemperatureModifierSetEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

public class SurviveHooks {

	public static double getTemperatureModifer(LivingEntity entity, ResourceLocation id, double originalValue)
	{
		TemperatureModifierSetEvent event = new TemperatureModifierSetEvent(id, originalValue);
		if (MinecraftForge.EVENT_BUS.post(event))
		{
			return 0;
		}
		return event.getModifier();
	}
}

