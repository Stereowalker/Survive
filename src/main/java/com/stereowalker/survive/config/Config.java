package com.stereowalker.survive.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.UnionConfig;

import net.minecraftforge.fml.config.ModConfig.Type;

@UnionConfig(name = "survive", autoReload = true)
public class Config implements ConfigObject {

	//
	//Thirst
	@UnionConfig.Entry(group = "Thirst" , name = "Enable Thirst", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods thirst system"})
	public boolean enable_thirst = true;
	
	@UnionConfig.Entry(group = "Thirst" , name = "Drink From Flowing Water", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Enabling this allows players to drink water from flowing water sources. This is only allowed for infinite water sources"})
	public boolean drinkFromFlowingWater = false;
	
	@UnionConfig.Entry(group = "Thirst" , name = "Remove Source Water Block", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Enabling this will remove the water source block when it is drunk"})
	public boolean shouldRemoveSourceWaterBlock = false;
	
	@UnionConfig.Entry(group = "Thirst" , name = "Idle Thirst Exhaustion", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Enabling this will remove the water source block when it is drunk"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float idle_thirst_exhaustion = 0.1125F;
	
	@UnionConfig.Entry(group = "Thirst" , name = "Idle Thrist Tick Rate", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The amount of time in ticks before thirst exaustion is added to the player regardless of what the player is doing. Set to -1 to disable"})
	@UnionConfig.Range(min = -1, max = 1000)
	public int idle_thirst_tick_rate = 100;
	
	@UnionConfig.Entry(group = "Thirst" , name = "Canteen Fill Amount", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The amount of times we can drink water from a canteen before it's empty"})
	@UnionConfig.Range(min = 1, max = 10)
	public int canteen_fill_amount = 3;
	
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
	//Stamina
	@UnionConfig.Entry(group = "Stamina" , name = "Enable Stamina", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods stamina system and energy management system"})
	public boolean enable_stamina = true;
	
	@UnionConfig.Entry(group = "Stamina" , name = "Enable Armor Weights", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will allow the weight of certain armor pieces to affect your stamina"})
	public boolean enable_weights = true;
	
	@UnionConfig.Entry(group = "Stamina" , name = "Display Weights in Pounds", type = Type.CLIENT)
	@UnionConfig.Comment(comment = {"If Enabled, armor weights will be displayed in pounds rather than kilograms"})
	public boolean displayWeightInPounds = false;
	
	@UnionConfig.Entry(group = "Stamina" , name = "Stamina Exhaustion From Interacting With Blocks", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Adds stamina exhaustion from interacting with blocks","This will only count if the block's interaction is successful i.e opening a chest","Stamina exhaustion unlike food exhaustion counts up to 10"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float stamina_drain_from_using_blocks = 1.0F;
	
	@UnionConfig.Entry(group = "Stamina" , name = "Stamina Exhaustion From Interacting With Items", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Adds stamina exhaustion from interacting with items","This will only count if the block's interaction is successful i.e drawing a bow","Stamina exhaustion unlike food exhaustion counts up to 10"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float stamina_drain_from_items = 1.0F;
	
	@UnionConfig.Entry(group = "Stamina" , name = "Stamina Exhaustion From Breaking Harvestable Blocks", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Adds stamina exhaustion from breaking harvestable blocks","This will only count if the player uses the incorrect tool to break the block","Stamina exhaustion unlike food exhaustion counts up to 10"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float stamina_drain_from_breaking_blocks_without_tool = 1.50F;
	
	@UnionConfig.Entry(group = "Stamina" , name = "Stamina Exhaustion From Breaking Non-Harvestable Blocks", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Adds stamina exhaustion from breaking non harvestable blocks","This will only count if the player uses the correct tool to break the block","Stamina exhaustion unlike food exhaustion counts up to 10"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float stamina_drain_from_breaking_blocks_with_tool = 0.125F;
	
	@UnionConfig.Entry(group = "Stamina" , name = "Stamina Recovery Ticks", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"How often in ticks does the player recover stamina","This recovers 1 point of stamina after the amount of set ticks passes"})
	@UnionConfig.Range(min = 0.0D, max = 10000.0D)
	public int stamina_recovery_ticks = 300;
	
	@UnionConfig.Entry(group = "Stamina" , name = "Maximum Armor Carry Weight", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The maximum weight (kg) of armor that the player can carry without losing stamina"})
	@UnionConfig.Range(min = 0.0D, max = 10000.0D)
	public float max_weight = 21.0F;
	
	//
	//Hygiene
	@UnionConfig.Entry(group = "Hygiene" , name = "Enable Hygiene (Beta)", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods hygiene system","The hygiene system might be pretty buggy, so proceed with caution"})
	public boolean enable_hygiene = false;
	
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

}
