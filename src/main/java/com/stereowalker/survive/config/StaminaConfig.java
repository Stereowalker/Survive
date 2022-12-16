package com.stereowalker.survive.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.UnionConfig;

import net.minecraftforge.fml.config.ModConfig.Type;

@UnionConfig(folder = "Survive Configs", name = "stamina", translatableName = "config.survive.stamina.file", autoReload = true)
public class StaminaConfig implements ConfigObject {	
	
	@UnionConfig.Entry(name = "Enable Stamina", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will disable this mods stamina system and energy management system"})
	public boolean enabled = true;
	
	@UnionConfig.Entry(name = "Ideal Amount Of Sleep", type = Type.SERVER)
	@UnionConfig.Comment(comment = {"The amount of time the player needs to sleep to recover all their energy"})
	public long sleepTime = 6000L;
	
	@UnionConfig.Entry(name = "Enable Armor Weights", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Disabling this will allow the weight of certain armor pieces to affect your stamina"})
	public boolean enable_weights = true;
	
	@UnionConfig.Entry(name = "Display Weights in Pounds", type = Type.CLIENT)
	@UnionConfig.Comment(comment = {"If Enabled, armor weights will be displayed in pounds rather than kilograms"})
	public boolean displayWeightInPounds = false;
	
	@UnionConfig.Entry(name = "Stamina Exhaustion From Interacting With Blocks", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Adds stamina exhaustion from interacting with blocks","This will only count if the block's interaction is successful i.e opening a chest","Stamina exhaustion unlike food exhaustion counts up to 10"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float stamina_drain_from_using_blocks = 1.0F;
	
	@UnionConfig.Entry(name = "Stamina Exhaustion From Interacting With Items", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Adds stamina exhaustion from interacting with items","This will only count if the block's interaction is successful i.e drawing a bow","Stamina exhaustion unlike food exhaustion counts up to 10"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float stamina_drain_from_items = 1.0F;
	
	@UnionConfig.Entry(name = "Stamina Exhaustion From Breaking Harvestable Blocks", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Adds stamina exhaustion from breaking harvestable blocks","This will only count if the player uses the incorrect tool to break the block","Stamina exhaustion unlike food exhaustion counts up to 10"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float stamina_drain_from_breaking_blocks_without_tool = 1.50F;
	
	@UnionConfig.Entry(name = "Stamina Exhaustion From Breaking Non-Harvestable Blocks", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"Adds stamina exhaustion from breaking non harvestable blocks","This will only count if the player uses the correct tool to break the block","Stamina exhaustion unlike food exhaustion counts up to 10"})
	@UnionConfig.Range(min = 0.0D, max = 4.0D)
	public float stamina_drain_from_breaking_blocks_with_tool = 0.125F;
	
	@UnionConfig.Entry(name = "Stamina Recovery Ticks", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"How often in ticks does the player recover stamina","This recovers 1 point of stamina after the amount of set ticks passes"})
	@UnionConfig.Range(min = 0.0D, max = 10000.0D)
	public int stamina_recovery_ticks = 300;
	
	@UnionConfig.Entry(name = "Maximum Armor Carry Weight", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The maximum weight (kg) of armor that the player can carry without losing stamina"})
	@UnionConfig.Range(min = 0.0D, max = 10000.0D)
	public float max_weight = 21.0F;
	
	@UnionConfig.Entry(name = "Maximum Stamina", type = Type.COMMON)
	@UnionConfig.Comment(comment = {"The maximum stamina the player has","If you want to modify this, you'll need to first restart the world then kill all the players in your world to have the changes applied to all the players"})
	@UnionConfig.Range(min = 0.0D, max = 100.0D)
	public float max_stamina = 20.0F;

}
