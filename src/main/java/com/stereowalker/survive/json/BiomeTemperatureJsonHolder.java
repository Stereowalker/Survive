package com.stereowalker.survive.json;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.json.JsonHolder;
import com.stereowalker.survive.core.registries.SurviveRegistries;
import com.stereowalker.survive.world.seasons.Season;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class BiomeTemperatureJsonHolder implements JsonHolder {
	private static final Marker BLOCK_TEMPERATURE_DATA = MarkerManager.getMarker("BLOCK_TEMPERATURE_DATA");

	private ResourceLocation biomeID;
	private final float temperature;
	private final float wetnessModifier;
	private final float sun_intensity;
	private final Pair<Float, Float> altitude_level_modifier;
	private final Map<Season,Float> seasonModifiers;

	public BiomeTemperatureJsonHolder(ResourceLocation biomeID, JsonObject object) {
		String ALTITUDE_LEVEL_MODIFIER = "altitude_level_modifier";
		String SEASON_MODIFIER = "season_modifier";

		float temperatureIn = 0;
		float wetnessModiIn = 1;
		float sunIntensitIn = 5;
		Pair<Float, Float> altitude_level_modifierIn = Pair.of(1.0f, 1.0f);
		Map<Season,Float> seasonModifiersIn = Maps.newHashMap();

		this.biomeID = biomeID;
		if(object.entrySet().size() != 0) {
			try {
				stopWorking();
				if(this.hasMemberAndIsPrimitive("temperature", object)) {
					temperatureIn = workOnFloat("temperature", object);
				}
				if(this.hasMemberAndIsPrimitive("wetness_modifier", object)) {
					wetnessModiIn = workOnFloat("wetness_modifier", object);
				}
				if(this.hasMemberAndIsPrimitive("sun_intensity", object)) {
					sunIntensitIn = workOnFloat("sun_intensity", object);
				}
				if(this.hasMemberAndIsObject(SEASON_MODIFIER, object)) {
					setWorkingOn(SEASON_MODIFIER);
					for (Entry<String, JsonElement> elem : object.get(SEASON_MODIFIER).getAsJsonObject().entrySet()) {
						Season season = null;
						setWorkingOn(elem.getKey());
						season = SurviveRegistries.SEASON.getValue(new ResourceLocation(elem.getKey()));
						if (season != null) {
							if(elem.getValue().isJsonPrimitive()) {
								seasonModifiersIn.put(season, elem.getValue().getAsFloat());
							} else {
								Survive.getInstance().getLogger().error("Error loading biome data {} from JSON: The season's modifier does not exist", biomeID);
							}
						} else {
							Survive.getInstance().getLogger().error("Error loading biome data {} from JSON: The season {} does not exist", biomeID,  new ResourceLocation(elem.getKey()));
						}
					}
					stopWorking();
				}
				if (this.hasMemberAndIsObject(ALTITUDE_LEVEL_MODIFIER, object)) {
					setWorkingOn(ALTITUDE_LEVEL_MODIFIER);
					JsonObject sea = object.getAsJsonObject(ALTITUDE_LEVEL_MODIFIER);
					if(sea.entrySet().size() != 0) {
						stopWorking();
						try {
							if(this.hasMemberAndIsPrimitive("upper", sea)) {
								setWorkingOn("upper");
								altitude_level_modifierIn = Pair.of(sea.get("upper").getAsFloat(), altitude_level_modifierIn.getSecond());
								stopWorking();
							}
							if (this.hasMemberAndIsPrimitive("lower", object)) {
								setWorkingOn("lower");
								altitude_level_modifierIn = Pair.of(altitude_level_modifierIn.getFirst(), sea.get("lower").getAsFloat());
								stopWorking();
							}
						} catch (ClassCastException e) {
							Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was wrong type!", e, biomeID, getworkingOn());
						} catch (NumberFormatException e) {
							Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was an invalid number!", e, biomeID, getworkingOn());
						}
					}
					stopWorking();
				}
			} catch (ClassCastException e) {
				Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was wrong type!", e, biomeID, getworkingOn());
			} catch (NumberFormatException e) {
				Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was an invalid number!", e, biomeID, getworkingOn());
			}
		}

		for (Season season : SurviveRegistries.SEASON) {
			if (!seasonModifiersIn.containsKey(season)) {
				seasonModifiersIn.put(season, season.getModifier());
			}
		}
		this.seasonModifiers = seasonModifiersIn;
		this.temperature = temperatureIn;
		this.wetnessModifier = wetnessModiIn;
		this.sun_intensity = sunIntensitIn;
		this.altitude_level_modifier = altitude_level_modifierIn;
	}

	public ResourceLocation getItemID() {
		return biomeID;
	}

	/**
	 * @return the temperatureModifier
	 */
	public float getTemperature() {
		return temperature;
	}
	
	public float getWetnessModifier() {
		return wetnessModifier;
	}

	public Pair<Float, Float> getAltitudeLevelModifier() {
		return altitude_level_modifier;
	}

	public Map<Season,Float> getSeasonModifiers() {
		return seasonModifiers;
	}

	@Override
	public CompoundTag serialize() {
		return null;
	}

	String wo = "NOTHING";
	
	@Override
	public String getworkingOn() {
		return wo;
	}

	@Override
	public void setWorkingOn(String member) {
		this.wo = member;
	}

	public float getSunIntensity() {
		return sun_intensity;
	}

	@Override
	public JsonHolder deserialize(CompoundTag input) {
		return null;
	}
}
