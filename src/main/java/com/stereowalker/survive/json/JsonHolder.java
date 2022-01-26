package com.stereowalker.survive.json;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;


/**
 * @author Stereowalker
 *
 */
public abstract class JsonHolder {
	protected String workingOn = "NOTHING";
	public JsonHolder(JsonObject object) {
	}
	
	public JsonHolder(CompoundTag nbt) {
	}
	
	public float workOnFloat(String member, JsonObject object) {
		float f = 0;
		workingOn = member;
		f = object.get(member).getAsFloat();
		stopWorking();
		return f;
	}
	
	public void stopWorking() {
		workingOn = "NOTHING";
	}
	
	protected boolean hasMemberAndIsPrimitive(String member, JsonObject object) {
		return object.has(member) && object.get(member).isJsonPrimitive();
	}
	
	protected boolean hasMemberAndIsObject(String member, JsonObject object) {
		return object.has(member) && object.get(member).isJsonObject();
	}
	
	protected boolean hasMemberAndIsJsonArray(String member, JsonObject object) {
		return object.has(member) && object.get(member).isJsonArray();
	}
	
	public abstract CompoundTag serialize();
}
