package com.stereowalker.survive.util.data;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.state.properties.UBlockStateProperties;

import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockTemperatureData {
    private static final Marker BLOCK_TEMPERATURE_DATA = MarkerManager.getMarker("BLOCK_TEMPERATURE_DATA");
    
	private ResourceLocation itemID;
	private final float temperatureModifier;
	private final int range;
	private final boolean usesLitOrActiveProperty;
	private final boolean usesLevelProperty;
	
	public BlockTemperatureData(ResourceLocation blockID, JsonObject object) {
		String NOTHING = "nothing";
		String TEMPERATURE_MODIFIER = "temperature_modifier";
		String LIT_PROPERTY = "uses_lit_or_active_property";
		String LEVEL_PROPERTY = "uses_level_property";
		String RANGE = "range";
		
		float temperatureModifierIn = 0;
		boolean usesLitOrActivePropertyIn = false;
		boolean usesLevelPropertyIn = false;
		int rangeIn = 0;
		
		this.itemID = blockID;
		if(object.entrySet().size() != 0) {
			String workingOn = NOTHING;
			try {
				if(object.has(TEMPERATURE_MODIFIER) && object.get(TEMPERATURE_MODIFIER).isJsonPrimitive()) {
					workingOn = TEMPERATURE_MODIFIER;
					temperatureModifierIn = object.get(TEMPERATURE_MODIFIER).getAsFloat();
					workingOn = NOTHING;
				}
				
				if(object.has(LIT_PROPERTY) && object.get(LIT_PROPERTY).isJsonPrimitive()) {
					workingOn = LIT_PROPERTY;
					usesLitOrActivePropertyIn = object.get(LIT_PROPERTY).getAsBoolean();
					workingOn = NOTHING;
				}
				
				if(object.has(LEVEL_PROPERTY) && object.get(LEVEL_PROPERTY).isJsonPrimitive()) {
					workingOn = LEVEL_PROPERTY;
					usesLevelPropertyIn = object.get(LEVEL_PROPERTY).getAsBoolean();
					workingOn = NOTHING;
				}
				
				if(object.has(RANGE) && object.get(RANGE).isJsonPrimitive()) {
					workingOn = RANGE;
					rangeIn = object.get(RANGE).getAsInt();
					workingOn = NOTHING;
				}
			} catch (ClassCastException e) {
				Survive.getInstance().LOGGER.warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was wrong type!", e, blockID, workingOn);
			} catch (NumberFormatException e) {
				Survive.getInstance().LOGGER.warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was an invalid number!", e, blockID, workingOn);
			}
		}
		
		if (rangeIn > 5) {
			Survive.getInstance().LOGGER.warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Range should not be greater that 5", blockID);
			rangeIn = 5;
		}
		
		if (rangeIn < 0) {
			Survive.getInstance().LOGGER.warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Range should not be less than 0", blockID);
			rangeIn = 0;
		}
		
		if (usesLitOrActivePropertyIn && ForgeRegistries.BLOCKS.getValue(blockID).getDefaultState().has(BlockStateProperties.LIT) && ForgeRegistries.BLOCKS.getValue(blockID).getDefaultState().has(UBlockStateProperties.ACTIVE)) {
			Survive.getInstance().LOGGER.warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: This block has neither the lit property nor the active property, please set this to false", blockID);
			usesLitOrActivePropertyIn = false;
		}
		
		if (usesLevelPropertyIn 
				&& ForgeRegistries.BLOCKS.getValue(blockID).getDefaultState().has(BlockStateProperties.LEVEL_0_15) 
				&& ForgeRegistries.BLOCKS.getValue(blockID).getDefaultState().has(BlockStateProperties.LEVEL_0_3)
				&& ForgeRegistries.BLOCKS.getValue(blockID).getDefaultState().has(BlockStateProperties.LEVEL_0_8)
				&& ForgeRegistries.BLOCKS.getValue(blockID).getDefaultState().has(BlockStateProperties.LEVEL_1_8)) {
			Survive.getInstance().LOGGER.warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: This block does not have the level property, please set this to false", blockID);
			usesLitOrActivePropertyIn = false;
		}
		
		this.temperatureModifier = temperatureModifierIn;
		this.usesLitOrActiveProperty = usesLitOrActivePropertyIn;
		this.usesLevelProperty = usesLevelPropertyIn;
		this.range = rangeIn;
		
	}

	public ResourceLocation getItemID() {
		return itemID;
	}

	/**
	 * @return the temperatureModifier
	 */
	public float getTemperatureModifier() {
		return temperatureModifier;
	}

	/**
	 * @return the usesLitOrActiveProperty
	 */
	public boolean usesLitOrActiveProperty() {
		return usesLitOrActiveProperty;
	}

	/**
	 * @return the range
	 */
	public int getRange() {
		return range;
	}

	/**
	 * @return the usesLevelProperty
	 */
	public boolean usesLevelProperty() {
		return usesLevelProperty;
	}
}
