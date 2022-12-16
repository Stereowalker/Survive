package com.stereowalker.survive.config;

import com.stereowalker.survive.core.TempDisplayMode;
import com.stereowalker.survive.core.TempMode;
import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.UnionConfig;

import net.minecraftforge.fml.config.ModConfig.Type;

@UnionConfig(folder = "Survive Configs", name = "temperature", translatableName = "config.survive.temperature.file", autoReload = true)
public class TemperatureConfig implements ConfigObject {
	
	@UnionConfig.Entry(name = "Enable", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods temperature system"})
	public boolean enabled = true;
	
	@UnionConfig.Entry(name = "Hypothermia/Hyperthermia Allows Sleep", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will prevent players from sleeping if they have hyperthermia or hypothermia"})
	public boolean hyp_allow_sleep = true;
	
	@UnionConfig.Entry(name = "Temperature Bar X Position", type = Type.CLIENT)
	@UnionConfig.Comment(comment = {"The x offset from the center of the screen where the temperature should render"})
	public int tempXLoc = -66;
	
	@UnionConfig.Entry(name = "Temperature Bar Y Position", type = Type.CLIENT)
	@UnionConfig.Comment(comment = {"The y offset from the center of the screen where the temperature should render"})
	public int tempYLoc = 5;
	
	@UnionConfig.Entry(name = "Hypo/Hyperthermia Effects", type = Type.CLIENT)
	@UnionConfig.Comment(comment = {"Change the texture of the temperature bar when you have hypothermia or hyperthermia"})
	public boolean tempEffects = true;
	
	@UnionConfig.Entry(name = "Temperature Mode", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"This determines the mode used when deciding the temperature","NORMAL Mode: calculates the average temperature of each block around the player","BLEND Mode: calculates the average temperature similar to that of NORMAL, but blends the temperature of the target positions with the player's. The blend ratio is determined by your distance from the block"})
	public TempMode tempMode = TempMode.BLEND;
	
	@UnionConfig.Entry(name = "Temperature Display Mode", type = Type.CLIENT)
	@UnionConfig.Comment(comment = {"The method that temperature display with"})
	public TempDisplayMode tempDisplayMode = TempDisplayMode.HOTBAR;
	
	@UnionConfig.Entry(name = "Display Temperature in Fahrenheit", type = Type.CLIENT)
	@UnionConfig.Comment(comment = {"If Enabled, temperature will be displayed in fahrenheit if the display mode is set to NUMBERS"})
	public boolean displayTempInFahrenheit = false;
	
	@UnionConfig.Entry(name = "Hypo/Hyperthermia Grace Timer", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"This is a timer used to decide when a player should get affected by the elements when their temperature hits the extreme"})
	@UnionConfig.Range(min = 0, max = 1980)
	public int tempGrace = 20;
	
	@UnionConfig.Entry(name = "Temperature Update Speed", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The speed at which the players temperature will change from their current temperature to the target temperature.","Setting it to 1 makes the players temperature change instantly to its target"})
	@UnionConfig.Range(min = 0.0001D, max = 1.0D)
	public double tempChangeSpeed = 0.001D;
	
	@UnionConfig.Entry(name = "Use Legacy Temperature System", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"This is intended for users who want to use the older temperature system","This feature will be removed in a later version of the mod"})
	public boolean useLegacyTemperatureSystem = false;

}
