package com.stereowalker.survive.world.seasons;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;

import net.minecraftforge.registries.IForgeRegistry;

public class Seasons {
	public static final List<Season> SEASON_LIST = new ArrayList<Season>();

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
	
	public static Season register(String name, Season condition) {
		condition.setRegistryName(Survive.getInstance().location(name));
		SEASON_LIST.add(condition);
		return condition;
	}
	
	public static void registerAll(IForgeRegistry<Season> registry) {
		for(Season condition : SEASON_LIST) {
			registry.register(condition);
			Survive.getInstance().debug("Season: \""+condition.getRegistryName().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Seasons Registered");
	}
}