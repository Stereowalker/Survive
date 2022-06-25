package com.stereowalker.survive.config;

import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.config.UnionConfig;

import net.minecraftforge.fml.config.ModConfig.Type;

@UnionConfig(name = "survive-server", autoReload = true, appendWithType = false)
public class ServerConfig {

	//
	//Temperature
	@UnionConfig.Entry(group = "Temperature" , name = "Dimensional Temperature Modifier", type = Type.SERVER)
	@UnionConfig.Comment(comment = {"This list defines dimensional temperature modifiers","Dimensions in this list will multiply the tempeatures of their biomes by this value.","Values here should typically be between 2 (hot) and -2 (cold) but they can be anything"})
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
	static final String MISC = "Miscellaneous";
	@UnionConfig.Entry(group = MISC , name = "Should Purified Water Cauldron Revert", type = Type.SERVER)
	@UnionConfig.Comment(comment = {"Should the purified water cauldron revert back to a water cauldron when no longer underneath a campfire?"})
	public static boolean purifiedCauldronRevert = false;
	
	@UnionConfig.Entry(group = MISC , name = "Animal Fat Drops", type = Type.SERVER)
	@UnionConfig.Comment(comment = {"Should Animal Fat drop from animals upon death?"})
	public static boolean animalFatDrops = true;
}
