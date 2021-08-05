package com.stereowalker.survive.util.data;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

/**
 * @author Stereowalker
 *
 */
public class ConsummableData extends JsonData {
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
	//Nutrition
	private int carbohydrateRatio = 1;
	private int proteinRatio = 1;
	//
	private boolean isChilled = false;
	private boolean isHeated = false;
	private boolean isEnergizing = false;
	//
	private boolean overwritesDefaultHunger = false;
	private boolean overwritesDefaultSaturation = false;
	private boolean overwritesDefaultHungerChance = false;
	
	public ConsummableData(ResourceLocation itemID, JsonObject object) {
		super(object);
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
		String NUTRITION = "nutrition";
		
		this.itemID = itemID;
		if(object.entrySet().size() != 0) {
			String workingOn = NOTHING;
			try {
				
				if(this.hasMemberAndIsPrimitive(THIRST, object)) {
					workingOn = THIRST;
					thirstAmount = object.get(THIRST).getAsInt();
					workingOn = NOTHING;
				}

				if(this.hasMemberAndIsPrimitive(HYDRATION, object)) {
					workingOn = HYDRATION;
					hydrationAmount = object.get(HYDRATION).getAsFloat();
					workingOn = NOTHING;
				}
				
				if(this.hasMemberAndIsPrimitive(THIRSTY, object)) {
					workingOn = THIRSTY;
					thirstChance = object.get(THIRSTY).getAsFloat();
					workingOn = NOTHING;
				}
				
				if(this.hasMemberAndIsPrimitive(HUNGER, object)) {
					workingOn = HUNGER;
					hungerAmount = object.get(HUNGER).getAsInt();
					overwritesDefaultHunger = true;
					workingOn = NOTHING;
				}

				if(this.hasMemberAndIsPrimitive(SATURATION, object)) {
					workingOn = SATURATION;
					saturationAmount = object.get(SATURATION).getAsFloat();
					overwritesDefaultSaturation = true;
					workingOn = NOTHING;
				}
				
				if(this.hasMemberAndIsPrimitive(HUNGERY, object)) {
					workingOn = HUNGERY;
					hungerChance = object.get(HUNGERY).getAsFloat();
					overwritesDefaultHungerChance = true;
					workingOn = NOTHING;
				}
				
				if(this.hasMemberAndIsPrimitive(ENERGY, object)) {
					workingOn = ENERGY;
					energyAmount = object.get(ENERGY).getAsInt();
					workingOn = NOTHING;
				}
				
				if(this.hasMemberAndIsPrimitive(CHILLED, object)) {
					workingOn = CHILLED;
					isChilled = object.get(CHILLED).getAsBoolean();
					workingOn = NOTHING;
				}
				
				if(this.hasMemberAndIsPrimitive(HEATED, object)) {
					workingOn = HEATED;
					isHeated = object.get(HEATED).getAsBoolean();
					workingOn = NOTHING;
				}
				
				if(this.hasMemberAndIsPrimitive(ENERGIZING, object)) {
					workingOn = ENERGIZING;
					isEnergizing = object.get(ENERGIZING).getAsBoolean();
					workingOn = NOTHING;
				}
				
				if(this.hasMemberAndIsObject(NUTRITION, object)) {
					workingOn = NUTRITION;
					JsonObject object2 = object.get(NUTRITION).getAsJsonObject();
					String CARB_RATIO = "carbohydrate_ratio";
					String PROTEIN_RATIO = "protein_ratio";
					if(object2.entrySet().size() != 0) {
						try {
							
							if(this.hasMemberAndIsPrimitive(CARB_RATIO, object2)) {
								workingOn = CARB_RATIO;
								carbohydrateRatio = object.get(CARB_RATIO).getAsInt();
								workingOn = NOTHING;
							}
							
							if(this.hasMemberAndIsPrimitive(PROTEIN_RATIO, object2)) {
								workingOn = PROTEIN_RATIO;
								proteinRatio = object.get(PROTEIN_RATIO).getAsInt();
								workingOn = NOTHING;
							}
							
						} catch (ClassCastException e) {
							Survive.getInstance().getLogger().warn(DRINK_DATA, "Loading drink data $s from JSON: Parsing element %s: element was wrong type!", e, itemID, workingOn);
						} catch (NumberFormatException e) {
							Survive.getInstance().getLogger().warn(DRINK_DATA, "Loading drink data $s from JSON: Parsing element %s: element was an invalid number!", e, itemID, workingOn);
						}
					}
					workingOn = NOTHING;
				}
				
			} catch (ClassCastException e) {
				Survive.getInstance().getLogger().warn(DRINK_DATA, "Loading drink data $s from JSON: Parsing element %s: element was wrong type!", e, itemID, workingOn);
			} catch (NumberFormatException e) {
				Survive.getInstance().getLogger().warn(DRINK_DATA, "Loading drink data $s from JSON: Parsing element %s: element was an invalid number!", e, itemID, workingOn);
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

	public int getCarbohydrateRatio() {
		return carbohydrateRatio;
	}

	public int getProteinRatio() {
		return proteinRatio;
	}

	@Override
	public CompoundNBT serialize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
