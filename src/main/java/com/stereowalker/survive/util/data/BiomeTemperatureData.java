package com.stereowalker.survive.util.data;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;

import net.minecraft.util.ResourceLocation;

public class BiomeTemperatureData {
    private static final Marker BLOCK_TEMPERATURE_DATA = MarkerManager.getMarker("BLOCK_TEMPERATURE_DATA");
    
	private ResourceLocation biomeID;
	private final float temperature;
	
	public BiomeTemperatureData(ResourceLocation biomeID, JsonObject object) {
		String NOTHING = "nothing";
		String TEMPERATURE = "temperature";
		
		float temperatureIn = 0;
		
		this.biomeID = biomeID;
		if(object.entrySet().size() != 0) {
			String workingOn = NOTHING;
			try {
				if(object.has(TEMPERATURE) && object.get(TEMPERATURE).isJsonPrimitive()) {
					workingOn = TEMPERATURE;
					temperatureIn = object.get(TEMPERATURE).getAsFloat();
					workingOn = NOTHING;
				}
			} catch (ClassCastException e) {
				Survive.getInstance().LOGGER.warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was wrong type!", e, biomeID, workingOn);
			} catch (NumberFormatException e) {
				Survive.getInstance().LOGGER.warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was an invalid number!", e, biomeID, workingOn);
			}
		}
		
		this.temperature = temperatureIn;
	}

	public ResourceLocation getItemID() {
		return biomeID;
	}

	/**
	 * @return the temperatureModifier
	 */
	public float getTemperature() {
		return temperature;
	}
}
