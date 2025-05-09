package com.stereowalker.survive.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.ConfigSide;
import com.stereowalker.unionlib.config.UnionConfig;

@UnionConfig(folder = "Survive Configs", name = "food", translatableName = "config.survive.food.file", autoReload = true)
public class FoodConfig implements ConfigObject {

	@UnionConfig.Entry(name = "Enable Food Spoiling", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"Disabling this will prevent food from spoiling"})
	public boolean enabled = true;

}
