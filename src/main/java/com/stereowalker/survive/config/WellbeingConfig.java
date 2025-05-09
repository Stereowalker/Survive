package com.stereowalker.survive.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.ConfigSide;
import com.stereowalker.unionlib.config.UnionConfig;

@UnionConfig(folder = "Survive Configs", name = "wellbeing", translatableName = "config.survive.wellbeing.file", autoReload = true)
public class WellbeingConfig implements ConfigObject {

	@UnionConfig.Entry(name = "Enable Well Being", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods well being system"})
	public boolean enabled = true;

}
