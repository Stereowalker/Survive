package com.stereowalker.survive.json;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;

import net.minecraft.resources.ResourceLocation;

public class FoodJsonHolder extends ConsummableJsonHolder {
    private static final Marker MARKER = MarkerManager.getMarker("FOOD_DRINK_DATA");
	private int timeFresh = 12000;
	private int lifespan = 156000;

	public FoodJsonHolder(ResourceLocation itemID, JsonObject object) {
		super(itemID, object);
		
		if(object.entrySet().size() != 0) {
			stopWorking();
			try {
				lifespan = this.workOnInt("lifespan", object);
				timeFresh = this.workOnInt("ticks_fresh", object);
				stopWorking();
			} catch (ClassCastException e) {
				Survive.getInstance().getLogger().warn(MARKER, "Loading food & drink data $s from JSON: Parsing element %s: element was wrong type!", e, itemID, getworkingOn());
			} catch (NumberFormatException e) {
				Survive.getInstance().getLogger().warn(MARKER, "Loading food & drink data $s from JSON: Parsing element %s: element was an invalid number!", e, itemID, getworkingOn());
			}
		}
	}

	public int lifespan() {
		return lifespan + timeFresh;
	}

	public int ticksFresh() {
		return timeFresh;
	}

}
