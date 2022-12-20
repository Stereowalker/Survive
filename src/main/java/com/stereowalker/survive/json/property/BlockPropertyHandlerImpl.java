package com.stereowalker.survive.json.property;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stereowalker.survive.api.IBlockPropertyHandler;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public abstract class BlockPropertyHandlerImpl<X extends Comparable<X>> implements IBlockPropertyHandler<X> {
	public static void init() {
		new BlockPropertyHandlerImpl<Boolean>("boolean") {
			@Override
			public Map<String, Float> deserialize(JsonObject object) {
				Map<String, Float> m = Maps.newHashMap();
				for (Entry<String, JsonElement> pr2 : object.get("values").getAsJsonObject().entrySet()) {
					m.put(pr2.getKey()+"b", pr2.getValue().getAsFloat());
				}
				return m;
			}
			
			@Override
			public PropertyPair<Boolean> requirements(JsonObject object) {
				return PropertyPair.create(BooleanProperty.create(object.get("property").getAsString()), object.get("value").getAsBoolean());
			}

			@Override
			public BooleanProperty derivedProperty() {
				return BooleanProperty.create(name);
			}

			@Override
			public Boolean getValue(String value) {
				return Boolean.parseBoolean(value.substring(0, value.length() - 1));
			}
		};
		new BlockPropertyHandlerImpl<Integer>("integer") {
			int min;
			int max;
			@Override
			public Map<String, Float> deserialize(JsonObject object) {
				min = object.get("min").getAsInt();
				max = object.get("max").getAsInt();
				Map<String, Float> m = Maps.newHashMap();
				for (Entry<String, JsonElement> pr2 : object.get("values").getAsJsonObject().entrySet()) {
					m.put(pr2.getKey()+"i", pr2.getValue().getAsFloat());
				}
				return m;
			}

			@Override
			public IntegerProperty derivedProperty() {
				return IntegerProperty.create(name, min, max);
			}
			
			@Override
			public PropertyPair<Integer> requirements(JsonObject object) {
				return PropertyPair.create(IntegerProperty.create(object.get("property").getAsString(), object.get("min").getAsInt(), object.get("max").getAsInt()), object.get("value").getAsInt());
			}

			@Override
			public Integer getValue(String value) {
				return Integer.parseInt(value.substring(0, value.length() - 1));
			}
		};
		new EnumBlockPropertyHandlerImpl<>();
	}
	
	String name;
	
	protected BlockPropertyHandlerImpl(String key) {
		SAVED_PROPERTIES.put(key, this);
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public static Map<String,Float> deserialize(IBlockPropertyHandler<?> handler, JsonObject object) {
		handler.setName(object.get("name").getAsString());
		object.remove("name");
		return handler.deserialize(object);
	}
}