package com.stereowalker.survive.api;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.level.block.state.properties.Property;

/**
 * Use this if you have completely custom block state property (not an enum, boolean or integer) to tell the mod how to read and interpret your property
 * @author Stereowalker
 *
 */
public interface IBlockPropertyHandler<T extends Comparable<T>> {

	public static final Map<String,IBlockPropertyHandler<?>> SAVED_PROPERTIES = Maps.newHashMap();
	public Map<String,Float> deserialize(JsonObject object);
	public PropertyPair<?> requirements(JsonObject object);
	public T getValue(String value);
	public Property<T> derivedProperty();
	public void setName(String name);
	
	public class PropertyPair<S extends Comparable<S>> extends Pair<Property<S>, S> {

		public PropertyPair(Property<S> first, S second) {
			super(first, second);
		}

	    public static <S extends Comparable<S>> PropertyPair<S> create(Property<S> first, S second) {
	        return new PropertyPair<>(first, second);
	    }
	}
}
