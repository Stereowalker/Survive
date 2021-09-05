package com.stereowalker.survive.compat;

import com.stereowalker.survive.Survive;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.integration.OriginEventsArchitectury;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.power.PowerTypeRegistry;

import com.stereowalker.survive.config.Config;

public class OriginsCompat {
	public static void initOriginsPatcher() {
		OriginEventsArchitectury.ORIGIN_LOADING.register(OriginsCompat::modifyOrigins);
	}

	public static void modifyOrigins(Origin origin) {
		String[] origin_idents = Config.originsHeat.split(",");
		for (String id : origin_idents) {
			if (origin.getIdentifier().toString().equals(id)) {
				origin.add(PowerTypeRegistry.get(Survive.getInstance().location("heat_resistance")));
			}
		}
	}
}
