package com.stereowalker.survive.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.ConfigSide;
import com.stereowalker.unionlib.config.UnionConfig;

@UnionConfig(folder = "Survive Configs", name = "hygiene", translatableName = "config.survive.hygiene.file", autoReload = true)
public class HygieneConfig implements ConfigObject {

	@UnionConfig.Entry(name = "Enable Hygiene (Beta)", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods hygiene system","The hygiene system might be pretty buggy, so proceed with caution"})
	public boolean enabled = false;
	
	@UnionConfig.Entry(name = "Dirty Tick Rate", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"How many ticks should pass before the players grime counter increases by one","Setting this to -1 prevents the player from getting dirty this way"})
	@UnionConfig.Range(min = -1, max = 5000, useSlider = true)
	public int dirtyTickRate = 600;
	
	@UnionConfig.Entry(name = "Illness Level", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"How many dirty stacks would cause the player to turn sick",
			"Setting this to -1 prevents the player from getting sick this way if the wellbeing module is enabled",
			"Keep in mind that the player becomes dirty at 30 stacks"})
	@UnionConfig.Range(min = -1, max = 5000, useSlider = true)
	public int illnessLevel = 200;

}
