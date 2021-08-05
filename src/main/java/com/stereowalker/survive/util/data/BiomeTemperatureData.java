package com.stereowalker.survive.util.data;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.Survive;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class BiomeTemperatureData extends JsonData {
    private static final Marker BLOCK_TEMPERATURE_DATA = MarkerManager.getMarker("BLOCK_TEMPERATURE_DATA");
    
	private ResourceLocation biomeID;
	private final float temperature;
	private final Pair<Float, Float> altitude_level_modifier;
	
	public BiomeTemperatureData(ResourceLocation biomeID, JsonObject object) {
		super(object);
		String NOTHING = "nothing";
		String TEMPERATURE = "temperature";
		String ALTITUDE_LEVEL_MODIFIER = "altitude_level_modifier";
		
		float temperatureIn = 0;
		Pair<Float, Float> altitude_level_modifierIn = Pair.of(1.0f, 1.0f);
		
		this.biomeID = biomeID;
		if(object.entrySet().size() != 0) {
			String workingOn = NOTHING;
			try {
				if(this.hasMemberAndIsPrimitive(TEMPERATURE, object)) {
					workingOn = TEMPERATURE;
					temperatureIn = object.get(TEMPERATURE).getAsFloat();
					workingOn = NOTHING;
				}
				if (this.hasMemberAndIsObject(ALTITUDE_LEVEL_MODIFIER, object)) {
					workingOn = ALTITUDE_LEVEL_MODIFIER;
					JsonObject sea = object.getAsJsonObject(ALTITUDE_LEVEL_MODIFIER);
					if(sea.entrySet().size() != 0) {
						workingOn = NOTHING;
						try {
							if(this.hasMemberAndIsPrimitive("upper", sea)) {
								workingOn = "upper";
								altitude_level_modifierIn = Pair.of(sea.get("upper").getAsFloat(), altitude_level_modifierIn.getSecond());
								workingOn = NOTHING;
							}
							if (this.hasMemberAndIsPrimitive("lower", object)) {
								workingOn = "lower";
								altitude_level_modifierIn = Pair.of(altitude_level_modifierIn.getFirst(), sea.get("lower").getAsFloat());
								workingOn = NOTHING;
							}
						} catch (ClassCastException e) {
							Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was wrong type!", e, biomeID, workingOn);
						} catch (NumberFormatException e) {
							Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was an invalid number!", e, biomeID, workingOn);
						}
					}
					workingOn = NOTHING;
				}
			} catch (ClassCastException e) {
				Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was wrong type!", e, biomeID, workingOn);
			} catch (NumberFormatException e) {
				Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was an invalid number!", e, biomeID, workingOn);
			}
		}
		
		this.temperature = temperatureIn;
		this.altitude_level_modifier = altitude_level_modifierIn;
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

	public Pair<Float, Float> getAltitudeLevelModifier() {
		return altitude_level_modifier;
	}

	@Override
	public CompoundNBT serialize() {
		// TODO Auto-generated method stub
		return null;
	}
}
