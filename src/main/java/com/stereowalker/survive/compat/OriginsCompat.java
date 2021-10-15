package com.stereowalker.survive.compat;

public class OriginsCompat {
	//TODO: Uncomment this when origins is working in 1.17
	public static void initOriginsPatcher() {
//		OriginEventsArchitectury.ORIGIN_LOADING.register(OriginsCompat::modifyOrigins);
	}

//	public static void modifyOrigins(Origin origin) {
//		String[] origin_idents = Config.originsHeat.split(",");
//		for (String id : origin_idents) {
//			if (origin.getIdentifier().toString().equals(id)) {
//				origin.add(PowerTypeRegistry.get(Survive.getInstance().location("heat_resistance")));
//			}
//		}
//	}
}
