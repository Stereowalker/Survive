package com.stereowalker.survive.util.data;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

/**
 * @author Stereowalker
 *
 */
public abstract class JsonData {
	public JsonData(JsonObject object) {
	}
	
	public JsonData(CompoundNBT nbt) {
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
	
	public abstract CompoundNBT serialize();
}
