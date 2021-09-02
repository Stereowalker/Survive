package com.stereowalker.survive.compat;

import com.stereowalker.survive.Survive;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.integration.OriginEventsArchitectury;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.power.PowerTypeRegistry;

public class OriginsCompat {
	public static void initOriginsPatcher() {
		OriginEventsArchitectury.ORIGIN_LOADING.register(OriginsCompat::modifyOrigins);
	}

	public static void modifyOrigins(Origin origin) {
		if (origin.getIdentifier().equals(Origins.identifier("blazeborn"))) {
			origin.add(PowerTypeRegistry.get(Survive.getInstance().location("heat_resistance")));
		}
	}
}
