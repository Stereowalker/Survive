package com.stereowalker.survive.util.data;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;

import net.minecraft.util.ResourceLocation;

/**
 * @author Stereowalker
 *
 */
public class ConsummableData {
    private static final Marker DRINK_DATA = MarkerManager.getMarker("DRINK_DATA");
    
	private ResourceLocation itemID;
	//Thirst
	private int thirstAmount = 0;
	private float hydrationAmount = 0;
	private float thirstChance = 0;
	//Hunger
	private int hungerAmount = 0;
	private float saturationAmount = 0;
	private float hungerChance = 0;
	//Stamina
	private int energyAmount = 0;
	//
	private boolean isChilled = false;
	private boolean isHeated = false;
	private boolean isEnergizing = false;
	//
	private boolean overwritesDefaultHunger = false;
	private boolean overwritesDefaultSaturation = false;
	private boolean overwritesDefaultHungerChance = false;
	
	public ConsummableData(ResourceLocation itemID, JsonObject object) {
		String NOTHING = "nothing";
		String THIRST = "thirst";
		String HYDRATION = "hydration";
		String THIRSTY = "thirst_chance";
		String HUNGER = "hunger";
		String ENERGY = "energy";
		String SATURATION = "saturation";
		String HUNGERY = "hunger_chance";
		String CHILLED = "gives_chilled_effect";
		String HEATED = "gives_heated_effect";
		String ENERGIZING = "gives_energizing_effect";
		
		this.itemID = itemID;
		if(object.entrySet().size() != 0) {
			String workingOn = NOTHING;
			try {
				
				if(object.has(THIRST) && object.get(THIRST).isJsonPrimitive()) {
					workingOn = THIRST;
					thirstAmount = object.get(THIRST).getAsInt();
					workingOn = NOTHING;
				}

				if(object.has(HYDRATION) && object.get(HYDRATION).isJsonPrimitive()) {
					workingOn = HYDRATION;
					hydrationAmount = object.get(HYDRATION).getAsFloat();
					workingOn = NOTHING;
				}
				
				if(object.has(THIRSTY) && object.get(THIRSTY).isJsonPrimitive()) {
					workingOn = THIRSTY;
					thirstChance = object.get(THIRSTY).getAsFloat();
					workingOn = NOTHING;
				}
				
				if(object.has(HUNGER) && object.get(HUNGER).isJsonPrimitive()) {
					workingOn = HUNGER;
					hungerAmount = object.get(HUNGER).getAsInt();
					overwritesDefaultHunger = true;
					workingOn = NOTHING;
				}

				if(object.has(SATURATION) && object.get(SATURATION).isJsonPrimitive()) {
					workingOn = SATURATION;
					saturationAmount = object.get(SATURATION).getAsFloat();
					overwritesDefaultSaturation = true;
					workingOn = NOTHING;
				}
				
				if(object.has(HUNGERY) && object.get(HUNGERY).isJsonPrimitive()) {
					workingOn = HUNGERY;
					hungerChance = object.get(HUNGERY).getAsFloat();
					overwritesDefaultHungerChance = true;
					workingOn = NOTHING;
				}
				
				if(object.has(ENERGY) && object.get(ENERGY).isJsonPrimitive()) {
					workingOn = ENERGY;
					energyAmount = object.get(ENERGY).getAsInt();
					workingOn = NOTHING;
				}
				
				if(object.has(CHILLED) && object.get(CHILLED).isJsonPrimitive()) {
					workingOn = CHILLED;
					isChilled = object.get(CHILLED).getAsBoolean();
					workingOn = NOTHING;
				}
				
				if(object.has(HEATED) && object.get(HEATED).isJsonPrimitive()) {
					workingOn = HEATED;
					isHeated = object.get(HEATED).getAsBoolean();
					workingOn = NOTHING;
				}
				
				if(object.has(ENERGIZING) && object.get(ENERGIZING).isJsonPrimitive()) {
					workingOn = ENERGIZING;
					isEnergizing = object.get(ENERGIZING).getAsBoolean();
					workingOn = NOTHING;
				}
				
			} catch (ClassCastException e) {
				Survive.getInstance().LOGGER.warn(DRINK_DATA, "Loading drink data $s from JSON: Parsing element %s: element was wrong type!", e, itemID, workingOn);
			} catch (NumberFormatException e) {
				Survive.getInstance().LOGGER.warn(DRINK_DATA, "Loading drink data $s from JSON: Parsing element %s: element was an invalid number!", e, itemID, workingOn);
			}
		}
	}

	public ResourceLocation getItemID() {
		return itemID;
	}

	public int getThirstAmount() {
		return thirstAmount;
	}

	public float getHydrationAmount() {
		return hydrationAmount;
	}

	public float getThirstChance() {
		return thirstChance;
	}
	
	public boolean isChilled() {
		return isChilled;
	}

	public boolean isHeated() {
		return isHeated;
	}

	public boolean isEnergizing() {
		return isEnergizing;
	}

	/**
	 * @return the hungerAmount
	 */
	public int getHungerAmount() {
		return hungerAmount;
	}

	/**
	 * @return the saturationAmount
	 */
	public float getSaturationAmount() {
		return saturationAmount;
	}

	/**
	 * @return the hungerChance
	 */
	public float getHungerChance() {
		return hungerChance;
	}

	/**
	 * @return the overwritesDefaultHunger
	 */
	public boolean overwritesDefaultHunger() {
		return overwritesDefaultHunger;
	}

	/**
	 * @return the overwritesDefaultSaturation
	 */
	public boolean overwritesDefaultSaturation() {
		return overwritesDefaultSaturation;
	}

	/**
	 * @return the overwritesDefaultHungerChance
	 */
	public boolean overwritesDefaultHungerChance() {
		return overwritesDefaultHungerChance;
	}
	
	/**
	 * Returns true if the food value of this item was altered in any way
	 * @return
	 */
	public boolean overwritesDefaultFood() {
		return overwritesDefaultHunger || overwritesDefaultHungerChance || overwritesDefaultSaturation;
	}

	/**
	 * @return the energyAmount
	 */
	public int getEnergyAmount() {
		return energyAmount;
	}
	
	
}
