package com.stereowalker.survive.hooks;

import com.stereowalker.survive.api.event.TemperatureModifierSetEvent;
import com.stereowalker.survive.world.temperature.TemperatureModifier;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;

public class SurviveHooks {

	public static TemperatureModifier getTemperatureModifer(LivingEntity entity, TemperatureModifier originalModifier)
	{
		TemperatureModifierSetEvent event = new TemperatureModifierSetEvent(originalModifier);
		if (NeoForge.EVENT_BUS.post(event).isCanceled())
		{
			return originalModifier.setMod(0);
		}
		return event.getModifier();
	}
}

