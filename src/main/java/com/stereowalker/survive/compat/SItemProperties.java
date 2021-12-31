package com.stereowalker.survive.compat;

import com.stereowalker.survive.world.temperature.TemperatureModifier.ContributingFactor;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.temperature.TemperatureQuery;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class SItemProperties {
	public static void registerAll() {
		ItemProperties.register(SItems.THERMOMETER, new ResourceLocation("temperature"), (stack, level, livingentity, count) -> {
			Entity entity = (Entity)(livingentity != null ? livingentity : stack.getEntityRepresentation());
			if (entity == null) {
				return 0.0f;
			}
			float environmentalMods = 0;
			for (Tuple<TemperatureQuery, ContributingFactor> query : TemperatureQuery.queries.values()) {
				if (query.getB() == ContributingFactor.ENVIRONMENTAL ) {
					environmentalMods += query.getA().run(null, 0, level, entity.blockPosition());
				}
			}
			return (Mth.clamp(environmentalMods, -2.0f, 2.0f) + 4.0f) / 2.0f;
		});
	}
}
