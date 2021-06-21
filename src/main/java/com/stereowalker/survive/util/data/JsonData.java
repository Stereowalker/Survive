package com.stereowalker.survive.util.data;

import com.google.gson.JsonObject;

/**
 * @author Stereowalker
 *
 */
public abstract class JsonData {
	public JsonData(JsonObject object) {
	}

	protected boolean hasMemberAndIsPrimitive(String member, JsonObject object) {
		return object.has(member) && object.get(member).isJsonPrimitive();
	}
	
}
