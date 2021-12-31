package com.stereowalker.survive.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.UnionConfig;

import net.minecraftforge.fml.config.ModConfig.Type;

@UnionConfig(folder = "Survive Configs", name = "hygiene", translatableName = "config.survive.hygiene.file", autoReload = true)
public class HygieneConfig implements ConfigObject {

	@UnionConfig.Entry(name = "Enable Hygiene (Beta)", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods hygiene system","The hygiene system might be pretty buggy, so proceed with caution"})
	public boolean enabled = false;
	
	@UnionConfig.Entry(name = "Dirty Tick Rate", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"How many ticks should pass before the players grime counter increases by one","Setting this to -1 prevents the player from getting dirty this way"})
	@UnionConfig.Range(min = -1, max = 5000)
	@UnionConfig.Slider
	public int dirtyTickRate = 500;

}
