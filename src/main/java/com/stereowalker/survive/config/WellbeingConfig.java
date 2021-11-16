package com.stereowalker.survive.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.UnionConfig;

import net.minecraftforge.fml.config.ModConfig.Type;

@UnionConfig(folder = "Survive Configs", name = "wellbeing", translatableName = "config.survive.wellbeing.file", autoReload = true)
public class WellbeingConfig implements ConfigObject {

	@UnionConfig.Entry(name = "Enable Well Being (Beta)", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods well being system","The well being system might be pretty buggy, so proceed with caution"})
	public boolean enabled = false;

}
