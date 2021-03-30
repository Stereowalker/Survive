package com.stereowalker.survive.config;

import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.config.UnionConfig;

import net.minecraftforge.fml.config.ModConfig.Type;

@UnionConfig(name = "survive.server", autoReload = true, appendWithType = false)
public class ServerConfig {

	//
	//Temperature
	@UnionConfig.Entry(group = "Temperature" , name = "Dimensional Temperature Modifier", type = Type.SERVER)
	@UnionConfig.Comment(comment = {"This list defines dimensional temperature modifiers","Dimensions in this list will multiply the tempeatures of their biomes by this value"})
	public static List<String> dimensionModifiers = Survive.defaultDimensionMods();
	
	//
	//Thirst
	
	//
	//Hunger
	
	//
	//Sleep
	
	//
	//Stamina
	
	//
	//Hygiene
	
	//
	//Misc
}
