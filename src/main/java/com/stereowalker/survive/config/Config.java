package com.stereowalker.survive.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.UnionConfig;

import net.minecraftforge.fml.config.ModConfig.Type;

@UnionConfig(name = "survive", autoReload = true)
public class Config implements ConfigObject {	
	//
	//Hunger
	@UnionConfig.Entry(group = "Hunger" , name = "Idle Hunger Exhaustion", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The amount of hunger exaustion added to the player regardless of what the player is doing"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float idle_hunger_exhaustion = 0.1125F;
	
	@UnionConfig.Entry(group = "Hunger" , name = "Idle Hunger Tick Rate", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The amount of time in ticks before hunger exaustion is added to the player regardless of what the player is doing. Set to -1 to disable"})
	@UnionConfig.Range(min = -1, max = 1000)
	public int idle_hunger_tick_rate = 120;
	
	//
	//Sleep
	@UnionConfig.Entry(group = "Sleep" , name = "Enable Sleep", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods sleep management system"})
	public boolean enable_sleep = true;
	
	@UnionConfig.Entry(group = "Sleep" , name = "Enable Tired Overlay", type = Type.CLIENT)
	@UnionConfig.Comment(comment = {"Disabling this will prevent the mod from drawing an overlay the screen when you need sleep"})
	public boolean tired_overlay = true;
	
	@UnionConfig.Entry(group = "Sleep" , name = "Can Sleep During Day", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will prevent the player from sleeping during the day even if they have the tiredness effect"})
	public boolean canSleepDuringDay = true;
	
	@UnionConfig.Entry(group = "Sleep" , name = "Initial Tired Time", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The amount of ticks after the player has not slept before The tiredness effect starts to manifest"})
	@UnionConfig.Range(min = 0, max = 240000)
	public int initialTiredTime = 24000;
	
	@UnionConfig.Entry(group = "Sleep" , name = "Tired Time Step", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The amount of ticks after the Initial Tired Time before the amplifier of the tiredness effects increases"})
	@UnionConfig.Range(min = 0, max = 240000)
	public int tiredTimeStep = 12000;
	
	@UnionConfig.Entry(group = "Sleep" , name = "Tired Time Stacks", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The maximum amount of sleep time can stack up to. This also determines the maximum amplifier for the tiredness effect"})
	@UnionConfig.Range(min = 0, max = 255)
	public int tiredTimeStacks = 20;
	
	//
	//Nutrition
	@UnionConfig.Entry(group = "Nutrition" , name = "Enable Nutrition (Beta)", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods nutrition system","The nutrition system might be pretty buggy, so proceed with caution"})
	public boolean nutrition_enabled = false;
	
	
	//
	//Misc
	@UnionConfig.Entry(group = "Micellaneous" , name = "Debug Mode", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Enable Debug mode to have some informational stuff print on your console"})
	public boolean debugMode = false;
	
	@UnionConfig.Entry(group = "Micellaneous" , name = "Disable All Enchantments", type = Type.COMMON)
	@UnionConfig.Comment(comment = {
			"For hardcore servers that do not need a little bit of magic to make life easier",
			"Be warned, this is not for the faint of heart.",
			"A lot of the mechanics in this mod become a lot more unreasonable without enchantments"})
	public boolean disable_enchantments = false;

	//
	//Other mods
	@UnionConfig.Entry(group = "Other" , name = "Origins Heat Resistant Races", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"If you have the origins mod, specify the names of races to add heat resistance to. Comma separated list"})
	public String originsHeat = "origins:blazeborn";
	
	@UnionConfig.Entry(group = "Other" , name = "Origins Cold Blooded Races", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"If you have the origins mod, specify the names of races to add cold bloodedness to. Comma separated list"})
	public String originsCold = "origins:merling";
	
	@UnionConfig.Entry(group = "Other" , name = "Air From Canteen Power", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"If you have the origins mod, specify the names of origins to add the \"Air from Canteen\" power to. Comma separated list"})
	public String originsAirFromCanteen = "origins:merling";

}
