package com.stereowalker.survive.config;

import com.stereowalker.survive.core.TempMode;
import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.ConfigSide;
import com.stereowalker.unionlib.config.UnionConfig;
import com.stereowalker.unionlib.util.ScreenHelper.ScreenOffset;

@UnionConfig(name = "survive", autoReload = true)
public class Config implements ConfigObject {	
	//
	//Hunger
	@UnionConfig.Entry(group = "Hunger" , name = "Idle Hunger Exhaustion", side =  ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"The amount of hunger exaustion added to the player regardless of what the player is doing"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float idle_hunger_exhaustion = 0.1125F;
	
	@UnionConfig.Entry(group = "Hunger" , name = "Idle Hunger Tick Rate", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"The amount of time in ticks before hunger exaustion is added to the player regardless of what the player is doing. Set to -1 to disable"})
	@UnionConfig.Range(min = -1, max = 1000)
	public int idle_hunger_tick_rate = 120;
	
	//
	//Sleep
	@UnionConfig.Entry(group = "Sleep" , name = "Enable Sleep", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods sleep management system"})
	public boolean enable_sleep = true;
	
	@UnionConfig.Entry(group = "Sleep" , name = "Enable Tired Overlay", side = ConfigSide.Client)
	@UnionConfig.Comment(comment = {"Disabling this will prevent the mod from drawing an overlay the screen when you need sleep"})
	public boolean tired_overlay = true;
	
	@UnionConfig.Entry(group = "Sleep" , name = "Can Sleep During Day", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"Disabling this will prevent the player from sleeping during the day even if they have the tiredness effect"})
	public boolean canSleepDuringDay = true;
	
	@UnionConfig.Entry(group = "Sleep" , name = "Initial Tired Time", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"The amount of ticks after the player has not slept before The tiredness effect starts to manifest"})
	@UnionConfig.Range(min = 0, max = 240000)
	public int initialTiredTime = 24000;
	
	@UnionConfig.Entry(group = "Sleep" , name = "Tired Time Step", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"The amount of ticks after the Initial Tired Time before the amplifier of the tiredness effects increases"})
	@UnionConfig.Range(min = 0, max = 240000)
	public int tiredTimeStep = 12000;
	
	@UnionConfig.Entry(group = "Sleep" , name = "Tired Time Stacks", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"The maximum amount of sleep time can stack up to. This also determines the maximum amplifier for the tiredness effect"})
	@UnionConfig.Range(min = 0, max = 255)
	public int tiredTimeStacks = 20;
	
	//
	//Nutrition
	@UnionConfig.Entry(group = "Nutrition" , name = "Enable Nutrition (Beta)", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods nutrition system","The nutrition system might be pretty buggy, so proceed with caution"})
	public boolean nutrition_enabled = false;
	
	@UnionConfig.Entry(group = "Nutrition" , name = "Cell Maintenance Rate", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"How often in ticks the body will attempt to 'maintain' itself. It's actually just how often protein is consumed",
			"You can set this to 0 to disable this feature, but your protein levels will still reduce automatically once you go past 3000 to ensure you're still able to heal"})
	@UnionConfig.Range(min = 0, max = 10000)
	public int idle_protein_tick_rate = 200;
	
	@UnionConfig.Entry(group = "Nutrition", name = "High Protein Maintenance Modifier", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"This is a modifier on [Cell Maintenance Rate]. If protein levels go above 2000, the rate is multiplied by this value"})
	@UnionConfig.Range(min = 0.1, max = 1)
	public float idle_protein_tick_rate_high = 0.8f;
	
	@UnionConfig.Entry(group = "Nutrition", name = "Low Protein Maintenance Modifier", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"This is a modifier on [Cell Maintenance Rate]. If protein levels go below 1000, the rate is multiplied by this value"})
	@UnionConfig.Range(min = 0.1, max = 1)
	public float idle_protein_tick_rate_low = 1.5f;
	
	@UnionConfig.Entry(group = "Nutrition", name = "Always Show Nutrition", side = ConfigSide.Client)
	@UnionConfig.Comment(comment = {"Enabling will make the nutrition indicator display even if you're not holding a food item"})
	public boolean always_render_nut = false;
	
	@UnionConfig.Entry(group = "Nutrition", name = "Show Raw Values", side = ConfigSide.Client)
	@UnionConfig.Comment(comment = {"Enabling this shows your actual nutrition values instead of the icons"})
	public boolean show_raw_nut_vals = false;
	
	@UnionConfig.Entry(group = "Nutrition", name = "X Position", side = ConfigSide.Client)
	@UnionConfig.Comment(comment = {"The x offset from the center of the screen where nutrition should render"})
	public int nut_xLoc = 1;
	
	@UnionConfig.Entry(group = "Nutrition", name = "Y Position", side = ConfigSide.Client)
	@UnionConfig.Comment(comment = {"The y offset from the center of the screen where nutrition should render"})
	public int nut_yLoc = 1;
	
	@UnionConfig.Entry(group = "Nutrition", name = "Anchor Position", side = ConfigSide.Client)
	@UnionConfig.Comment(comment = {"Where on the screen should the nutrition indicator be anchored"})
	public ScreenOffset nut_offset = ScreenOffset.TOP_LEFT;
	
	
	//
	//Misc
	@UnionConfig.Entry(group = "Micellaneous" , name = "Debug Mode", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"Enable Debug mode to have some informational stuff print on your console"})
	public boolean debugMode = false;
	
	@UnionConfig.Entry(group = "Micellaneous" , name = "Disable All Enchantments", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {
			"For hardcore servers that do not need a little bit of magic to make life easier",
			"Be warned, this is not for the faint of heart.",
			"A lot of the mechanics in this mod become a lot more unreasonable without enchantments"})
	public boolean disable_enchantments = false;
	
	@UnionConfig.Entry(group = "Micellaneous", name = "Odds To Ignite Campfire", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"The odds a campfire will light on fire each time you attempt to ignite it"})
	@UnionConfig.Range(min = 0, max = 1)
	public float igniteCampfireOdds = 0.5f;
	
	//
	//Other mods
	@UnionConfig.Entry(group = "Other" , name = "Origins Heat Resistant Races", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"If you have the origins mod, specify the names of races to add heat resistance to. Comma separated list"})
	public String originsHeat = "origins:blazeborn";
	
	@UnionConfig.Entry(group = "Other" , name = "Origins Cold Blooded Races", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"If you have the origins mod, specify the names of races to add cold bloodedness to. Comma separated list"})
	public String originsCold = "origins:merling";
	
	@UnionConfig.Entry(group = "Other" , name = "Air From Canteen Power", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"If you have the origins mod, specify the names of origins to add the \"Air from Canteen\" power to. Comma separated list"})
	public String originsAirFromCanteen = "origins:merling";

}
