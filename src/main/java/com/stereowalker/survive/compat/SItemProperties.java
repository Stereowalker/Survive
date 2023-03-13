package com.stereowalker.survive.compat;

import com.stereowalker.survive.world.temperature.TemperatureModifier.ContributingFactor;

import java.util.Map.Entry;

import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.temperature.TemperatureQuery;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class SItemProperties {
	public static void registerAll() {
		ItemProperties.register(SItems.THERMOMETER, new ResourceLocation("temperature"), (stack, level, livingentity, count) -> {
			Entity entity = (Entity)(livingentity != null ? livingentity : stack.getEntityRepresentation());
			if (entity == null) {
				return 0.0F;
			} else {
				if (level == null && entity.level instanceof ClientLevel) {
					level = (ClientLevel)entity.level;
				}

				if (level == null) {
					return 0.0F;
				} else {
					float environmentalMods = 0;
					for (Entry<ResourceLocation, Tuple<TemperatureQuery, ContributingFactor>> query : TemperatureQuery.queries.entrySet()) {
						if (query.getValue().getB() == ContributingFactor.ENVIRONMENTAL ) {
							environmentalMods += query.getValue().getA().run(null, 0, level, entity.blockPosition(), false);
						}
					}
					float val = 0.0f;
					if (environmentalMods >= 2.0f) {
						val = 1.0f;
					} else if (environmentalMods >= 1.6f) {
						val = 0.9f;
					} else if (environmentalMods >= 1.2f) {
						val = 0.8f;
					} else if (environmentalMods >= 0.8f) {
						val = 0.7f;
					} else if (environmentalMods >= 0.4f) {
						val = 0.6f;
					} else if (environmentalMods >= 0.0f) {
						val = 0.5f;
					} else if (environmentalMods >= -0.4f) {
						val = 0.4f;
					} else if (environmentalMods >= -0.8f) {
						val = 0.3f;
					} else if (environmentalMods >= -1.2f) {
						val = 0.2f;
					} else if (environmentalMods >= -1.6f) {
						val = 0.1f;
					}
					return val;
				}
			}
		});
	}
}
