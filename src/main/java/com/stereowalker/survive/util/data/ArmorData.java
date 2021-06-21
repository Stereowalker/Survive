package com.stereowalker.survive.util.data;

import java.util.List;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.registries.SurviveRegistries;
import com.stereowalker.survive.temperature.TemperatureChangeCondition;
import com.stereowalker.survive.temperature.TemperatureChangeInstance;

import net.minecraft.util.ResourceLocation;

public class ArmorData {
    private static final Marker ARMOR_DATA = MarkerManager.getMarker("ARMOR_DATA");
    
	private ResourceLocation itemID;
	private final List<TemperatureChangeInstance> temperatureModifier;
	private final float weightModifier;
	
	public ArmorData(ResourceLocation itemID, JsonObject object) {
		String NOTHING = "nothing";
		String TEMPERATURE_MODIFIER = "temperature_modifiers";
		String WEIGHT_MODIFIER = "weight_modifier";
		
		List<TemperatureChangeInstance> temperatureModifierIn = Lists.newArrayList();
		float weightModifierIn = 0;
		
		this.itemID = itemID;
		if(object.entrySet().size() != 0) {
			String workingOn = NOTHING;
			try {
				
				if(object.has(TEMPERATURE_MODIFIER) && object.get(TEMPERATURE_MODIFIER).isJsonArray()) {
					workingOn = TEMPERATURE_MODIFIER;
					for (JsonElement elem : object.get(TEMPERATURE_MODIFIER).getAsJsonArray()) {
						if (elem.isJsonObject()) {
							JsonObject object2 = elem.getAsJsonObject();
							TemperatureChangeCondition<?> condition = null;

							String CONDITION = "condition";
							if(object2 != null && object2.entrySet().size() != 0) {
								if(object2.has(CONDITION) && object2.get(CONDITION).isJsonPrimitive()) {
									workingOn = CONDITION;
									condition = SurviveRegistries.CONDITION.getValue(new ResourceLocation(object2.get(CONDITION).getAsString()));
									if (condition != null) {
										temperatureModifierIn.add(condition.createInstance(object2));
									} else {
										Survive.getInstance().getLogger().error("Error loading armor data {} from JSON: The condition {} does not exist", itemID,  new ResourceLocation(object2.get(CONDITION).getAsString()));
									}
									workingOn = NOTHING;
								}
							}
						} else {
							Survive.getInstance().getLogger().error("Error loading armor data {} from JSON: The conditions for {} aren't a json object", itemID,  elem);
						}
					}
					workingOn = NOTHING;
				}
				
				if(object.has(WEIGHT_MODIFIER) && object.get(WEIGHT_MODIFIER).isJsonPrimitive()) {
					workingOn = WEIGHT_MODIFIER;
					weightModifierIn = object.get(WEIGHT_MODIFIER).getAsFloat();
					workingOn = NOTHING;
				}
			} catch (ClassCastException e) {
				Survive.getInstance().getLogger().warn(ARMOR_DATA, "Error loading armor data {} from JSON: Parsing element {}: element was wrong type!", itemID, workingOn);
			} catch (NumberFormatException e) {
				Survive.getInstance().getLogger().warn(ARMOR_DATA, "Error loading armor data {} from JSON: Parsing element {}: element was an invalid number!", itemID, workingOn);
			}
		}
		
		if (weightModifierIn < 0) {
			Survive.getInstance().getLogger().warn(ARMOR_DATA, "Error loading armor data {} from JSON: Parsing element {}: weight is less than zero, please fix this!", itemID, WEIGHT_MODIFIER);
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
	public List<TemperatureChangeInstance> getTemperatureModifier() {
		return temperatureModifier;
	}

	/**
	 * @return the weightModifier
	 */
	public float getWeightModifier() {
		return weightModifier;
	}
}
