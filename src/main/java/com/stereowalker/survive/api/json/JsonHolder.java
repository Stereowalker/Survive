package com.stereowalker.survive.api.json;

import java.lang.reflect.InvocationTargetException;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.stereowalker.survive.json.ArmorJsonHolder;
import com.stereowalker.survive.json.BiomeJsonHolder;
import com.stereowalker.survive.json.FluidJsonHolder;

import net.minecraft.nbt.CompoundTag;


/**
 * @author Stereowalker
 *
 */
public interface JsonHolder {
	public String getworkingOn();
	
	public void setWorkingOn(String member);
	
	public default float workOnFloat(String member, JsonObject object) {
		float f = 0;
		setWorkingOn(member);
		f = object.get(member).getAsFloat();
		stopWorking();
		return f;
	}
	
	public default int workOnInt(String member, JsonObject object) {
		int i = 0;
		setWorkingOn(member);
		i = object.get(member).getAsInt();
		stopWorking();
		return i;
	}
	
	public default float workOnFloatIfAvailable(String member, JsonObject object, float defaultValue) {
		if (this.hasMemberAndIsPrimitive(member, object))
			return workOnFloat(member, object);
		else
			return defaultValue;
	}
	
	public default int workOnIntIfAvailable(String member, JsonObject object, int defaultValue) {
		if (this.hasMemberAndIsPrimitive(member, object))
			return workOnInt(member, object);
		else
			return defaultValue;
	}
	
	public default void stopWorking() {
		setWorkingOn("NOTHING");
	}
	
	public default boolean hasMemberAndIsPrimitive(String member, JsonObject object) {
		
		return object.has(member) && object.get(member).isJsonPrimitive();
	}
	
	public default boolean hasMemberAndIsObject(String member, JsonObject object) {
		return object.has(member) && object.get(member).isJsonObject();
	}
	
	public default boolean hasMemberAndIsJsonArray(String member, JsonObject object) {
		return object.has(member) && object.get(member).isJsonArray();
	}
	
	public CompoundTag serialize();
	public JsonHolder deserialize(CompoundTag input);

	public static ImmutableMap<String, Class<? extends JsonHolder>> HOLD = new ImmutableMap.Builder<String, Class<? extends JsonHolder>>()
			.put("Lcom/stereowalker/survive/json/ArmorJsonHolder;", ArmorJsonHolder.class)
			.put("Lcom/stereowalker/survive/json/FluidJsonHolder;", FluidJsonHolder.class)
			.put("Lcom/stereowalker/survive/json/BiomeJsonHolder;", BiomeJsonHolder.class)
			.build();
	public static JsonHolder deserialize(CompoundTag input, Class<? extends JsonHolder> jsonClass) {
		try {
			return jsonClass.getConstructor(input.getClass()).newInstance(input);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}
