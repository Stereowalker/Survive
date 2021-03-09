package com.stereowalker.survive.util.data;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;

import net.minecraft.util.ResourceLocation;

public class ArmorData {
    private static final Marker ARMOR_DATA = MarkerManager.getMarker("ARMOR_DATA");
    
	private ResourceLocation itemID;
	private final float temperatureModifier;
	private final float weightModifier;
	
	public ArmorData(ResourceLocation itemID, JsonObject object) {
		String NOTHING = "nothing";
		String TEMPERATURE_MODIFIER = "temperature_modifier";
		String WEIGHT_MODIFIER = "weight_modifier";
		
		float temperatureModifierIn = 0;
		float weightModifierIn = 0;
		
		this.itemID = itemID;
		if(object.entrySet().size() != 0) {
			String workingOn = NOTHING;
			try {
				
				if(object.has(TEMPERATURE_MODIFIER) && object.get(TEMPERATURE_MODIFIER).isJsonPrimitive()) {
					workingOn = TEMPERATURE_MODIFIER;
					temperatureModifierIn = object.get(TEMPERATURE_MODIFIER).getAsFloat();
					workingOn = NOTHING;
				}
				
				if(object.has(WEIGHT_MODIFIER) && object.get(WEIGHT_MODIFIER).isJsonPrimitive()) {
					workingOn = WEIGHT_MODIFIER;
					weightModifierIn = object.get(WEIGHT_MODIFIER).getAsFloat();
					workingOn = NOTHING;
				}
			} catch (ClassCastException e) {
				Survive.getInstance().LOGGER.warn(ARMOR_DATA, "Loading armor temperature data $s from JSON: Parsing element %s: element was wrong type!", itemID, workingOn);
			} catch (NumberFormatException e) {
				Survive.getInstance().LOGGER.warn(ARMOR_DATA, "Loading armor temperature data $s from JSON: Parsing element %s: element was an invalid number!", itemID, workingOn);
			}
		}
		
		if (weightModifierIn < 0) {
			Survive.getInstance().LOGGER.warn(ARMOR_DATA, "Loading armor temperature data $s from JSON: Parsing element %s: weight is less than zero!", itemID, WEIGHT_MODIFIER);
			weightModifierIn = 0;
		}
		
		this.temperatureModifier = temperatureModifierIn;
		this.weightModifier = weightModifierIn;
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
	 * @return the weightModifier
	 */
	public float getWeightModifier() {
		return weightModifier;
	}
}
