package com.stereowalker.survive.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.ConfigSide;
import com.stereowalker.unionlib.config.UnionConfig;

@UnionConfig(folder = "Survive Configs", name = "thirst", translatableName = "config.survive.thirst.file", autoReload = true)
public class ThirstConfig implements ConfigObject {

	@UnionConfig.Entry(name = "Enable Thirst", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods thirst system"})
	public boolean enabled = true;

	@UnionConfig.Entry(name = "Drink From Flowing Water", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"Enabling this allows players to drink water from flowing water sources. This is only allowed for infinite water sources"})
	public boolean drinkFromFlowingWater = false;

	@UnionConfig.Entry(name = "Remove Source Water Block", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"Enabling this will remove the water source block when it is drunk"})
	public boolean shouldRemoveSourceWaterBlock = false;

	@UnionConfig.Entry(name = "Idle Thirst Exhaustion", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"The amount of thirst exaustion added to the player regardless of what the player is doing"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float idle_thirst_exhaustion = 0.1125F;

	@UnionConfig.Entry(name = "Idle Thrist Tick Rate", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"The amount of time in ticks before thirst exaustion is added to the player regardless of what the player is doing. Set to -1 to disable"})
	@UnionConfig.Range(min = -1, max = 1000)
	public int idle_thirst_tick_rate = 100;

	@UnionConfig.Entry(name = "Canteen Fill Amount", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"The amount of times we can drink water from a canteen before it's empty"})
	@UnionConfig.Range(min = 1, max = 10)
	public int canteen_fill_amount = 3;

	@UnionConfig.Entry(name = "Netherite Canteen Fill Amount", side = ConfigSide.Shared)
	@UnionConfig.Comment(comment = {"The amount of times we can drink water from a netherite canteen before it's empty"})
	@UnionConfig.Range(min = 1, max = 10)
	public int nether_canteen_fill_amount = 4;

	@UnionConfig.Entry(name = "Hydration Restoration Rate", side = ConfigSide.Server)
	@UnionConfig.Comment(comment = {"How much hydration is restored from the total of certain foods and drinks",
			"0 is 0% and 1 is 100%"
	})
	@UnionConfig.Range(min = 0, max = 1)
	public float hydration_restoration = 0.1f;
	
	public int canteenFillAmount(boolean isNetherite) {
		return isNetherite ? nether_canteen_fill_amount : 
			canteen_fill_amount;
	}

}
