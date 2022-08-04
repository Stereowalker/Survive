package com.stereowalker.survive.json.property;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stereowalker.survive.api.IBlockPropertyHandler;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class EnumBlockPropertyHandlerImpl<T extends Enum<T> & StringRepresentable> implements IBlockPropertyHandler<T> {
	String name;
	List<String> allowed_values = Lists.newArrayList();
	String className = "";
	
	protected EnumBlockPropertyHandlerImpl() {
		SAVED_PROPERTIES.put("enum", this);
	}
	
	@Override
	public Map<String, Float> deserialize(JsonObject object) {
		className = object.get("class").getAsString();
		Map<String, Float> m = Maps.newHashMap();
		for (Entry<String, JsonElement> pr2 : object.get("values").getAsJsonObject().entrySet()) {
			m.put(pr2.getKey()+":"+object.get("class").getAsString(), pr2.getValue().getAsFloat());
		}
		if (object.has("allowed_values"))
			object.get("allowed_values").getAsJsonArray().forEach((elem) -> {
				allowed_values.add(elem.getAsString());
			});
		return m;
	}
	
	@Override
	public EnumProperty<T> derivedProperty() {
		return obtainProperty(name, className, allowed_values);
	}
	
	@Override
	public PropertyPair<T> requirements(JsonObject object) {
		List<String> allowed_values = Lists.newArrayList();
		if (object.has("allowed_values"))
			object.get("allowed_values").getAsJsonArray().forEach((elem) -> {
				allowed_values.add(elem.getAsString());
			});
		return new PropertyPair<T>(obtainProperty(object.get("property").getAsString(), object.get("class").getAsString(), allowed_values), fetchEnum(object.get("class").getAsString(), object.get("value").getAsString()));
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public T getValue(String value) {
		return fetchEnum(value.split(":")[1], value.split(":")[0]);
	}

	@SuppressWarnings("unchecked")
	public static<T extends Enum<T> & StringRepresentable> EnumProperty<T> obtainProperty(String propertyName, String className, List<String> values) {
		try {
			if (values != null && values.size() > 0) {
				T first = fetchEnum(className, values.get(0));
				return EnumProperty.create(propertyName, (Class<T>) Class.forName(className), Arrays.stream(first.getDeclaringClass().getEnumConstants()).filter((s) -> values.contains(s.name())).collect(Collectors.toList()));
			}
			return EnumProperty.create(propertyName, (Class<T>) Class.forName(className));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E fetchEnum(String className, String string) {
		try {
			return Enum.valueOf((Class<E>) Class.forName(className), string);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}