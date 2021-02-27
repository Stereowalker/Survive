//package com.stereowalker.survive.config;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.stereowalker.survive.Survive;
//import com.stereowalker.survive.compat.CombatCompat;
//import com.stereowalker.survive.entity.TempDisplayMode;
//import com.stereowalker.survive.events.SurviveEvents.TempMode;
//import com.stereowalker.unionlib.util.ConfigHelper;
//
//import net.minecraftforge.common.ForgeConfigSpec;
//
//public class OldConfig {
//	//Temperature
//	public static ForgeConfigSpec.BooleanValue enable_temperature;
//	public static ForgeConfigSpec.BooleanValue hyp_allow_sleep;
//	public static ForgeConfigSpec.IntValue tempXLoc;
//	public static ForgeConfigSpec.IntValue tempYLoc;
//	public static ForgeConfigSpec.EnumValue<TempMode> tempMode;
//	public static ForgeConfigSpec.EnumValue<TempDisplayMode> tempDisplayMode;
//	public static ForgeConfigSpec.BooleanValue displayTempInFahrenheit;
//	public static ForgeConfigSpec.ConfigValue<List<String>> dimensionModifiers;
//	public static ForgeConfigSpec.ConfigValue<List<String>> armorTemperatures;
//	public static ForgeConfigSpec.ConfigValue<List<String>> blockTemperatures;
//	public static ForgeConfigSpec.ConfigValue<List<String>> litOrActiveBlockTemperatures;
//	public static ForgeConfigSpec.IntValue tempCalcRange;
//	//Thirst
//	public static ForgeConfigSpec.BooleanValue enable_thirst;
//	public static ForgeConfigSpec.ConfigValue<List<String>> waterContainers;
//	public static ForgeConfigSpec.ConfigValue<List<String>> thirstContainers;
//	public static ForgeConfigSpec.ConfigValue<List<String>> chilledContainers;
//	public static ForgeConfigSpec.ConfigValue<List<String>> heatedContainers;
//	public static ForgeConfigSpec.ConfigValue<List<String>> stimulatingContainers;
//	public static ForgeConfigSpec.ConfigValue<List<String>> pamsWaterContainers;
//	public static ForgeConfigSpec.BooleanValue drinkFromFlowingWater;
//	public static ForgeConfigSpec.BooleanValue shouldRemoveSourceWaterBlock;
//	public static ForgeConfigSpec.DoubleValue idle_thirst_exhaustion;
//	public static ForgeConfigSpec.IntValue idle_thirst_tick_rate;
//	//Hunger
//	public static ForgeConfigSpec.DoubleValue idle_hunger_exhaustion;
//	public static ForgeConfigSpec.IntValue idle_hunger_tick_rate;
//	//Sleep
//	public static ForgeConfigSpec.BooleanValue enable_sleep;
//	public static ForgeConfigSpec.BooleanValue tired_overlay;
//	public static ForgeConfigSpec.BooleanValue canSleepDuringDay;
//	public static ForgeConfigSpec.IntValue initialTiredTime;
//	public static ForgeConfigSpec.IntValue tiredTimeStep;
//	public static ForgeConfigSpec.IntValue tiredTimeStacks;
//	//Stamina
//	public static ForgeConfigSpec.BooleanValue enable_stamina;
//	//Hygiene
//	public static ForgeConfigSpec.BooleanValue enableHygiene;
//	
//	
//	//TODO: Attempt to move this to the compat class
//	public static ForgeConfigSpec.BooleanValue debug_mode;
//
//
//	public static void init(ForgeConfigSpec.Builder server, ForgeConfigSpec.Builder common, ForgeConfigSpec.Builder client) {
//		//Temperature
//		tempXLoc = client
//				.comment("The x offset from the center of the screen where the temperature should render")
//				.defineInRange("Temperature.Temperature X Offset", -66, -200, 200);
//		tempYLoc = client
//				.comment("The y offset from the top of the screen where the temperature should render")
//				.defineInRange("Temperature.Temperature Y Offset", 5, -200, 200);
//		tempDisplayMode = client
//				.defineEnum("Temperature.Temperature Display Mode", TempDisplayMode.HOTBAR);
//		displayTempInFahrenheit = client
//				.comment("If Enabled, temperature will be displayed in fahrenheit if the display mode is set to NUMBERS")
//				.define("Temperature.Display In Fahrenheit", false);
//
//		enable_temperature = common
//				.comment("Disabling this will disable this mods temperature system")
//				.define("Temperature.Enable Temperature", true);
//		
//		drinkFromFlowingWater = common
//				.comment("Enabling this allows players to drink water from flowing water sources. This is only allowed for infinite water sources")
//				.define("Temperature.Allow Drinking From Flowing Water", true);
//		
//		shouldRemoveSourceWaterBlock = common
//				.comment("Enabling this will remove the water source block when it is drunk")
//				.define("Temperature.Remove Water Source Block", false);
//		
//		hyp_allow_sleep = common
//				.comment("Disabling this will prevent players from sleeping if they have hyperthermia or hypothermia")
//				.define("temperature.Enable Hypothermia/Hyperthermia Allow Sleep", true);
//		tempCalcRange = client
//				.comment("The range in blocks which temperature is calculated. It can also be interpreted as the range you have to be for a block for it to factor in your temperature")
//				.defineInRange("Temperature.Block Calculation Range", 5, 2, 10);
//		tempMode = common
//				.comment("This determines the mode used when deciding the temperature","NORMAL Mode: calculates the average temperature of each block around the player","BLEND Mode: calculates the average temperature similar to that of NORMAL, but blends the temperature of the target positions with the player's. The blend ratio is determined by your distance from the block")
//				.defineEnum("temperature.Temperature Mode", TempMode.BLEND);
//		List<String> dims = new ArrayList<String>();
//		dims.add("minecraft:overworld,1.0");
//		dims.add("minecraft:the_nether,1.0");
//		dims.add("minecraft:the_end,1.0");
//		dimensionModifiers = ConfigHelper.listValue("Temperature", "Dimensional Temperature Modifiers", server, dims, "This list defines dimensional temperature modifiers","Dimensions in this list will multiply the tempeatures of their biomes by this value");
//
//		List<String> armorTemps = new ArrayList<String>();
//		
//		armorTemps.add("survive:stiffened_honey_helmet,-5.0");
//		armorTemps.add("survive:stiffened_honey_chestplate,-5.0");
//		armorTemps.add("survive:stiffened_honey_leggings,-5.0");
//		armorTemps.add("survive:stiffened_honey_boots,-5.0");
//		
//		armorTemps.add("survive:wool_hat,5.0");
//		armorTemps.add("survive:wool_jacket,5.0");
//		armorTemps.add("survive:wool_pants,5.0");
//		armorTemps.add("survive:wool_boots,5.0");
//		
//		armorTemps.add("minecraft:chainmail_helmet,0.1");
//		armorTemps.add("minecraft:chainmail_chestplate,0.1");
//		armorTemps.add("minecraft:chainmail_leggings,0.1");
//		armorTemps.add("minecraft:chainmail_boots,0.1");
//		
//		armorTemps.add("minecraft:leather_helmet,4.0");
//		armorTemps.add("minecraft:leather_chestplate,4.0");
//		armorTemps.add("minecraft:leather_leggings,4.0");
//		armorTemps.add("minecraft:leather_boots,4.0");
//		
//		armorTemps.add("minecraft:iron_helmet,2.7");
//		armorTemps.add("minecraft:iron_chestplate,2.7");
//		armorTemps.add("minecraft:iron_leggings,2.7");
//		armorTemps.add("minecraft:iron_boots,2.7");
//		
//		armorTemps.add("minecraft:golden_helmet,1.5");
//		armorTemps.add("minecraft:golden_chestplate,1.5");
//		armorTemps.add("minecraft:golden_leggings,1.5");
//		armorTemps.add("minecraft:golden_boots,1.5");
//		
//		armorTemps.add("minecraft:diamond_helmet,0.5");
//		armorTemps.add("minecraft:diamond_chestplate,0.5");
//		armorTemps.add("minecraft:diamond_leggings,0.5");
//		armorTemps.add("minecraft:diamond_boots,0.5");
//		
//		armorTemps.add("minecraft:netherite_helmet,1.9");
//		armorTemps.add("minecraft:netherite_chestplate,1.9");
//		armorTemps.add("minecraft:netherite_leggings,1.9");
//		armorTemps.add("minecraft:netherite_boots,1.9");
//		
//		armorTemps.add("minecraft:turtle_helmet,0.75");
//		
//		//Ars Nouveau
//		armorTemps.add("ars_nouveau:novice_robes,3.0");
//		armorTemps.add("ars_nouveau:novice_leggings,3.0");
//		armorTemps.add("ars_nouveau:novice_hood,3.0");
//		armorTemps.add("ars_nouveau:novice_boots,3.0");
//		armorTemps.add("ars_nouveau:apprentice_robes,3.0");
//		armorTemps.add("ars_nouveau:apprentice_leggings,3.0");
//		armorTemps.add("ars_nouveau:apprentice_hood,3.0");
//		armorTemps.add("ars_nouveau:apprentice_boots,3.0");
//		armorTemps.add("ars_nouveau:archmage_robes,3.0");
//		armorTemps.add("ars_nouveau:archmage_leggings,3.0");
//		armorTemps.add("ars_nouveau:archmage_hood,3.0");
//		armorTemps.add("ars_nouveau:archmage_boots,3.0");
//		
//		armorTemperatures = ConfigHelper.listValue("Temperature", "Armor Temperature Modifiers", common, armorTemps, "This list defines the temperatures of armors when they're put on","Changes to this list will require you to reset your game for them to be applied","NOTE: Wool Armor is set to 5.0");
//
//		List<String> blockTemps = new ArrayList<String>();
//		blockTemperatures = ConfigHelper.listValue("Temperature", "Block Temperatures", common, blockTemps, "This list defines the temperatures of blocks","Changes to this list will require you to reset your game for them to be applied","NOTE: Lava is set to 10.0");
//		
//		List<String> litBlockTemps = new ArrayList<String>();
//		litOrActiveBlockTemperatures = ConfigHelper.listValue("Temperature", "Lit/Active Block Temperatures", common, litBlockTemps, "This list defines the temperatures of blocks","Blocks in this list will apply their temperatures only then they're \"lit\"","Changes to this list will require you to reset your game for them to be applied");
//		
//		if (Survive.isCombatLoaded()) {
//			CombatCompat.setupConfig(common);
//		}
//
//		//Thirst
//		enable_thirst = common
//				.comment("Disabling this will disable this mods thirst system")
//				.define("thirst.Enable Thirst", true);
//
//		idle_thirst_exhaustion = common
//				.comment("The amount of thirst exaustion added to the player regardless of what the player is doing")
//				.defineInRange("thirst.Idle Thirst Exhaustion", 0.1125F , 0.0D, 4.0D);
//		idle_thirst_tick_rate = common
//				.comment("The amount of time in ticks before thirst exaustion is added to the player regardless of what the player is doing. Set to -1 to disable")
//				.defineInRange("thirst.Idle Thirst Tick Rate", 100 , -1, 1000);
//
//		List<String> water = new ArrayList<String>();
//		water.add("minecraft:sweet_berries,-2");
//		water.add("farmersdelight:milk_bottle,4");
//		water.add("minecraft:pumpkin_pie,-4");
//		waterContainers = ConfigHelper.listValue("Thirst", "Water Containers (Normal)", common, water, "This list defines which items replenish thirst when consumed","Items in this list will not give any extra effects");
//		List<String> thirst = new ArrayList<String>();
//		thirst.add("minecraft:honey_bottle,1");
//		thirst.add("minecraft:pufferfish,-8");
//		thirst.add("minecraft:rotten_flesh,-4");
//		thirst.add("minecraft:poisonous_potato,-8");
//		thirst.add("minecraft:spider_eye,-8");
//		thirst.add("minecraft:bread,-6");
//		thirst.add("minecraft:cookie,-1");
//		thirst.add("farmersdelight:raw_pasta,-4");
//		thirst.add("farmersdelight:pie_crust,-2");
//		thirst.add("farmersdelight:slice_of_cake,-2");
//		thirst.add("farmersdelight:slice_of_apple_pie,-2");
//		thirst.add("farmersdelight:slice_of_sweet_berry_cheesecake,-2");
//		thirst.add("farmersdelight:slice_of_chocolate_pie,-2");
//		thirst.add("farmersdelight:sweet_berry_cookie,-1");
//		thirst.add("farmersdelight:honey_cookie,-1");
//		thirst.add("farmersdelight:cooked_rice,-4");
//		thirstContainers = ConfigHelper.listValue("Thirst", "Water Containers (Thirst)", common, thirst, "This list defines which items replenish thirst when consumed","Items in this list will randomly give the thirst effect when drunk");
//		List<String> chilled = new ArrayList<String>();
//		chilled.add("minecraft:beetroot_soup,3");
//		chilled.add("minecraft:potato,0");
//		chilled.add("minecraft:carrot,0");
//		chilled.add("create:builders_tea,8");
//		chilled.add("farmersdelight:tomato_sauce,1");
//		chilled.add("farmersdelight:cabbage,0");
//		chilled.add("farmersdelight:cabbage_leaf,0");
//		chilled.add("farmersdelight:tomato,0");
//		chilled.add("farmersdelight:onion,0");
//		chilled.add("farmersdelight:pumpkin_slice,0");
//		chilled.add("farmersdelight:minced_beef,0");
//		chilled.add("farmersdelight:mixed_salad,0");
//		chilledContainers = ConfigHelper.listValue("Thirst", "Water Containers (Chilled)", common, chilled, "This list defines which items replenish thirst when consumed","Items in this list will give the chilled effect upon drunk");
//		List<String> heated = new ArrayList<String>(); 
//		heated.add("minecraft:rabbit_stew,0");
//		heated.add("farmersdelight:hot_cocoa,4");
//		heated.add("farmersdelight:beef_stew,1");
//		heated.add("farmersdelight:chicken_soup,5");
//		heated.add("farmersdelight:vegetable_soup,5");
//		heated.add("farmersdelight:pumpkin_soup,5");
//		heated.add("farmersdelight:nether_salad,0");
//		heated.add("farmersdelight:dumplings,0");
//		heated.add("farmersdelight:stuffed_pumpkin,0");
//		heated.add("farmersdelight:fish_stew,1");
//		heated.add("farmersdelight:baked_cod_stew,1");
//		heated.add("farmersdelight:honey_glazed_ham,-2");
//		heated.add("farmersdelight:pasta_with_meatballs,0");
//		heated.add("farmersdelight:pasta_with_mutton_chop,0");
//		heated.add("farmersdelight:vegetable_noodles,0");
//		heated.add("farmersdelight:steak_and_potatoes,0");
//		heated.add("farmersdelight:shepherds_pie,0");
//		heated.add("farmersdelight:ratatouille,0");
//		heated.add("farmersdelight:squid_ink_pasta,0");
//		heated.add("farmersdelight:grilled_salmon,0");
//		heatedContainers = ConfigHelper.listValue("Thirst", "Water Containers (Heated)", common, heated, "This list defines which items replenish thirst when consumed","Items in this list will give the heated effect when drunk");
//		List<String> energized = new ArrayList<String>();
//		energized.add("survive:sugar_water_bottle,2");
//		stimulatingContainers = ConfigHelper.listValue("Thirst", "Water Containers (Energize)", common, energized, "This list defines which items will replenish thirst and energy","Items in this list will gived the energized effect when drunk","The energized effect will allow the user to operate even if they've not slept for a while and it replenishes energy");
//
//		//Sleep
//		enable_sleep = common
//				.comment("Disabling this will disable this mods sleep management system")
//				.define("sleep.Enable Sleep", true);
//		tired_overlay = common
//				.comment("Disabling this will disable the color changing of the screen when you need sleep")
//				.define("sleep.Enable Tired Overlay", true);
//		canSleepDuringDay = common
//				.comment("Disabling this will prevent the player from sleeping during the day even if they have the tiredness effect")
//				.define("sleep.Can Sleep During Day", true);
//		initialTiredTime = common
//				.comment("The amount of ticks after the player has not slept before The tiredness effect starts to manifest")
//				.defineInRange("sleep.Initial Tired Time", 24000, 0, 240000);
//		tiredTimeStep = common
//				.comment("The amount of ticks after the Initial Tired Time before the amplifier of the tiredness effects increases")
//				.defineInRange("sleep.Tired Time Step", 12000, 0, 240000);
//		tiredTimeStacks = common
//				.comment("The maximum amount of sleep time can stack up to. This also determines the maximum amplifier for the tiredness effect")
//				.defineInRange("sleep.Tired Time Stacks", 20, 0, 255);
//		//Stamina
//		enable_stamina = common
//				.comment("Disabling this will disable this mods energy/stamina management system")
//				.define("stamina.Enable Stamina", true);
//		
//		//HUnger
//		idle_hunger_exhaustion = common
//				.comment("The amount of hunger exaustion added to the player regardless of what the player is doing")
//				.defineInRange("hunger.Idle Hunger Exhaustion", 0.1125F , 0.0D, 4.0D);
//		idle_hunger_tick_rate = common
//				.comment("The amount of time in ticks before hunger exaustion is added to the player regardless of what the player is doing. Set to -1 to disable")
//				.defineInRange("hunger.Idle Hunger Tick Rate", 120 , -1, 1000);
//		//Hygiene
//		enableHygiene = common
//				.comment("Disabling this will disable this mods hygiene management system")
//				.define("hygiene.Enable Hygiene", true);
//		//Debug
//		debug_mode = common
//				.define("developerOptions.debug_mode", false);
//	}
//}
