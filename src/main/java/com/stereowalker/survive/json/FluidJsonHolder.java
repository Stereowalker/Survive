package com.stereowalker.survive.json;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.json.JsonHolder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Stereowalker
 *
 */
public class FluidJsonHolder implements JsonHolder {
    private static final Marker DRINK_DATA = MarkerManager.getMarker("FLUID");
    
	private ResourceLocation itemID;
	//Thirst
	private int thirstAmount = 0;
	private float hydrationAmount = 0;
	private float thirstChance = 0;
	
	public FluidJsonHolder(CompoundTag nbt) {
		this.itemID = new ResourceLocation(nbt.getString("id"));
		this.thirstAmount = nbt.getInt("thirst_amount");
		this.hydrationAmount = nbt.getFloat("hydration_amount");
		this.thirstChance = nbt.getFloat("thirst_chance");
	}
	
	public FluidJsonHolder(ResourceLocation itemID, JsonObject object) {
		String THIRST = "thirst";
		String THIRSTY = "thirst_chance";
		
		this.itemID = itemID;
		if(object.entrySet().size() != 0) {
			stopWorking();
			try {
				if(this.hasMemberAndIsPrimitive(THIRST, object)) {
					setWorkingOn(THIRST);
					thirstAmount = object.get(THIRST).getAsInt();
					stopWorking();
				}

				if(this.hasMemberAndIsPrimitive("hydration", object)) {
					hydrationAmount = workOnFloat("hydration", object);
				}
				
				if(this.hasMemberAndIsPrimitive(THIRSTY, object)) {
					setWorkingOn(THIRSTY);
					thirstChance = object.get(THIRSTY).getAsFloat();
					stopWorking();
				}
				
			} catch (ClassCastException e) {
				Survive.getInstance().getLogger().warn(DRINK_DATA, "Loading fluid data $s from JSON: Parsing element %s: element was wrong type!", e, itemID, getworkingOn());
			} catch (NumberFormatException e) {
				Survive.getInstance().getLogger().warn(DRINK_DATA, "Loading fluid data $s from JSON: Parsing element %s: element was an invalid number!", e, itemID, getworkingOn());
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

	@Override
	public CompoundTag serialize() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("id", this.itemID.toString());
		nbt.putInt("thirst_amount", this.thirstAmount);
		nbt.putFloat("hydration_amount", this.hydrationAmount);
		nbt.putFloat("thirst_chance", this.thirstChance);
		return nbt;
	}

	@Override
	public FluidJsonHolder deserialize(CompoundTag input) {
		return new FluidJsonHolder(input);
	}
	
	String wo = "NOTHING";
	@Override
	public String getworkingOn() {return wo;}

	@Override
	public void setWorkingOn(String member) {
		this.wo = member;
	}

}
