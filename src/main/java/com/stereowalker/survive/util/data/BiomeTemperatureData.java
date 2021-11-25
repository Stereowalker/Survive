package com.stereowalker.survive.util.data;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.registries.SurviveRegistries;
import com.stereowalker.survive.world.seasons.Season;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class BiomeTemperatureData extends JsonData {
    private static final Marker BLOCK_TEMPERATURE_DATA = MarkerManager.getMarker("BLOCK_TEMPERATURE_DATA");
    
	private ResourceLocation biomeID;
	private final float temperature;
	private final Pair<Float, Float> altitude_level_modifier;
	private final Map<Season,Float> seasonModifiers;
	
	public BiomeTemperatureData(ResourceLocation biomeID, JsonObject object) {
		super(object);
		String NOTHING = "nothing";
		String TEMPERATURE = "temperature";
		String ALTITUDE_LEVEL_MODIFIER = "altitude_level_modifier";
		String SEASON_MODIFIER = "season_modifier";
		
		float temperatureIn = 0;
		Pair<Float, Float> altitude_level_modifierIn = Pair.of(1.0f, 1.0f);
		Map<Season,Float> seasonModifiersIn = Maps.newHashMap();
		
		this.biomeID = biomeID;
		if(object.entrySet().size() != 0) {
			String workingOn = NOTHING;
			try {
				if(this.hasMemberAndIsPrimitive(TEMPERATURE, object)) {
					workingOn = TEMPERATURE;
					temperatureIn = object.get(TEMPERATURE).getAsFloat();
					workingOn = NOTHING;
				}
				if(this.hasMemberAndIsObject(SEASON_MODIFIER, object)) {
					workingOn = SEASON_MODIFIER;
					for (Entry<String, JsonElement> elem : object.get(SEASON_MODIFIER).getAsJsonObject().entrySet()) {
						Season season = null;
						workingOn = elem.getKey();
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
					workingOn = NOTHING;
				}
				if (this.hasMemberAndIsObject(ALTITUDE_LEVEL_MODIFIER, object)) {
					workingOn = ALTITUDE_LEVEL_MODIFIER;
					JsonObject sea = object.getAsJsonObject(ALTITUDE_LEVEL_MODIFIER);
					if(sea.entrySet().size() != 0) {
						workingOn = NOTHING;
						try {
							if(this.hasMemberAndIsPrimitive("upper", sea)) {
								workingOn = "upper";
								altitude_level_modifierIn = Pair.of(sea.get("upper").getAsFloat(), altitude_level_modifierIn.getSecond());
								workingOn = NOTHING;
							}
							if (this.hasMemberAndIsPrimitive("lower", object)) {
								workingOn = "lower";
								altitude_level_modifierIn = Pair.of(altitude_level_modifierIn.getFirst(), sea.get("lower").getAsFloat());
								workingOn = NOTHING;
							}
						} catch (ClassCastException e) {
							Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was wrong type!", e, biomeID, workingOn);
						} catch (NumberFormatException e) {
							Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was an invalid number!", e, biomeID, workingOn);
						}
					}
					workingOn = NOTHING;
				}
			} catch (ClassCastException e) {
				Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was wrong type!", e, biomeID, workingOn);
			} catch (NumberFormatException e) {
				Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was an invalid number!", e, biomeID, workingOn);
			}
		}
		
		for (Season season : SurviveRegistries.SEASON) {
			if (!seasonModifiersIn.containsKey(season)) {
				seasonModifiersIn.put(season, season.getModifier());
			}
		}
		this.seasonModifiers = seasonModifiersIn;
		this.temperature = temperatureIn;
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

	public Pair<Float, Float> getAltitudeLevelModifier() {
		return altitude_level_modifier;
	}

	public Map<Season,Float> getSeasonModifiers() {
		return seasonModifiers;
	}

	@Override
	public CompoundNBT serialize() {
		// TODO Auto-generated method stub
		return null;
	}
}
