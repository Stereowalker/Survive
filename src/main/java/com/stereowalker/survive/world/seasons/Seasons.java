package com.stereowalker.survive.world.seasons;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.stereowalker.survive.Survive;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;

public class Seasons {
	public static final Map<ResourceLocation, Season> SEASON_LIST = new HashMap<ResourceLocation, Season>();

	public static final Season NONE = register("none", new Season(0));
	public static final Season DRY_BEGIN = register("dry_begin", new Season(0.1f));
	public static final Season DRY_MIDST = register("dry_midst", new Season(0.2f));
	public static final Season DRY_CLOSE = register("dry_close", new Season(0.1f));
	public static final Season WET_BEGIN = register("wet_begin", new Season(-0.1f));
	public static final Season WET_MIDST = register("wet_midst", new Season(-0.2f));
	public static final Season WET_CLOSE = register("wet_close", new Season(-0.1f));
	public static final Season WINTER_BEGIN = register("winter_begin", new Season(-0.6f));
	public static final Season WINTER_MIDST = register("winter_midst", new Season(-0.9f));
	public static final Season WINTER_CLOSE = register("winter_close", new Season(-0.6f));
	public static final Season SPRING_BEGIN = register("spring_begin", new Season(-0.3f));
	public static final Season SPRING_MIDST = register("spring_midst", new Season(0.0f));
	public static final Season SPRING_CLOSE = register("spring_close", new Season(0.3f));
	public static final Season SUMMER_BEGIN = register("summer_begin", new Season(0.6f));
	public static final Season SUMMER_MIDST = register("summer_midst", new Season(0.9f));
	public static final Season SUMMER_CLOSE = register("summer_close", new Season(0.6f));
	public static final Season AUTUMN_BEGIN = register("autumn_begin", new Season(0.3f));
	public static final Season AUTUMN_MIDST = register("autumn_midst", new Season(0.0f));
	public static final Season AUTUMN_CLOSE = register("autumn_close", new Season(-0.3f));
	
	public static Season register(String name, Season season) {
		SEASON_LIST.put(Survive.getInstance().location(name), season);
		return season;
	}
	
	public static void registerAll(RegisterHelper<Season> registry) {
		for(Entry<ResourceLocation, Season> season : SEASON_LIST.entrySet()) {
			registry.register(season.getKey(), season.getValue());
			Survive.getInstance().debug("Season: \""+season.getKey().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Seasons Registered");
	}
}
