package com.stereowalker.survive.json;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;


/**
 * @author Stereowalker
 *
 */
public abstract class JsonHolder {
	public JsonHolder(JsonObject object) {
	}
	
	public JsonHolder(CompoundTag nbt) {
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
