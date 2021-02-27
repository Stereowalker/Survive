package com.stereowalker.survive.hooks;

import com.stereowalker.survive.api.event.TemperatureModifierSetEvent;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
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

