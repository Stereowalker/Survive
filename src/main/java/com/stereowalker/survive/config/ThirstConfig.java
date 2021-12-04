package com.stereowalker.survive.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.UnionConfig;

import net.minecraftforge.fml.config.ModConfig.Type;

@UnionConfig(folder = "Survive Configs", name = "thirst", translatableName = "config.survive.thirst.file", autoReload = true)
public class ThirstConfig implements ConfigObject {

	@UnionConfig.Entry(name = "Enable Thirst", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods thirst system"})
	public boolean enabled = true;
	
	@UnionConfig.Entry(name = "Drink From Flowing Water", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Enabling this allows players to drink water from flowing water sources. This is only allowed for infinite water sources"})
	public boolean drinkFromFlowingWater = false;
	
	@UnionConfig.Entry(name = "Remove Source Water Block", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Enabling this will remove the water source block when it is drunk"})
	public boolean shouldRemoveSourceWaterBlock = false;
	
	@UnionConfig.Entry(name = "Idle Thirst Exhaustion", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Enabling this will remove the water source block when it is drunk"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float idle_thirst_exhaustion = 0.1125F;
	
	@UnionConfig.Entry(name = "Idle Thrist Tick Rate", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The amount of time in ticks before thirst exaustion is added to the player regardless of what the player is doing. Set to -1 to disable"})
	@UnionConfig.Range(min = -1, max = 1000)
	public int idle_thirst_tick_rate = 100;
	
	@UnionConfig.Entry(name = "Canteen Fill Amount", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The amount of times we can drink water from a canteen before it's empty"})
	@UnionConfig.Range(min = 1, max = 10)
	public int canteen_fill_amount = 3;

}
