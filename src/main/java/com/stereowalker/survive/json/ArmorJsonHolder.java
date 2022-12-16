package com.stereowalker.survive.json;

import java.util.List;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.json.JsonHolder;
import com.stereowalker.survive.core.registries.SurviveRegistries;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeCondition;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeInstance;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

public class ArmorJsonHolder implements JsonHolder {
    private static final Marker ARMOR_DATA = MarkerManager.getMarker("ARMOR_DATA");
    
	private ResourceLocation itemID;
	private final List<Pair<String,TemperatureChangeInstance>> temperatureModifier;
	private final float weightModifier;
	
	public ArmorJsonHolder(CompoundTag nbt) {
		this.itemID = new ResourceLocation(nbt.getString("id"));
		this.weightModifier = nbt.getFloat("weight_modifier");
		this.temperatureModifier = Lists.newArrayList();
		
		nbt.getList("temperature_modifiers", 10).forEach((comp) -> {
			CompoundTag nbt2 = (CompoundTag) comp;
			this.temperatureModifier.add(Pair.of(nbt2.getString("condition"), SurviveRegistries.CONDITION.getValue(new ResourceLocation(nbt2.getString("condition"))).createInstance(nbt2.getCompound("contents"))));
		});
	}
	
	public ArmorJsonHolder(ResourceLocation itemID, JsonObject object) {
		List<Pair<String,TemperatureChangeInstance>> temperatureModifierIn = Lists.newArrayList();
		float weightModifierIn = 0;
		
		this.itemID = itemID;
		if(object.entrySet().size() != 0) {
			try {
				if(this.hasMemberAndIsJsonArray("temperature_modifiers", object)) {
					setWorkingOn("temperature_modifiers");
					for (JsonElement elem : object.get("temperature_modifiers").getAsJsonArray()) {
						if (elem.isJsonObject()) {
							JsonObject object2 = elem.getAsJsonObject();
							TemperatureChangeCondition<?> condition = null;

							if(object2 != null && object2.entrySet().size() != 0) {
								if(object2.has("condition") && object2.get("condition").isJsonPrimitive()) {
									setWorkingOn("condition");
									condition = SurviveRegistries.CONDITION.getValue(new ResourceLocation(object2.get("condition").getAsString()));
									if (condition != null) {
										temperatureModifierIn.add(Pair.of(object2.get("condition").getAsString(), condition.createInstance(object2)));
									} else {
										Survive.getInstance().getLogger().error("Error loading armor data {} from JSON: The condition {} does not exist", itemID,  new ResourceLocation(object2.get("condition").getAsString()));
									}
									stopWorking();
								}
							}
						} else {
							Survive.getInstance().getLogger().error("Error loading armor data {} from JSON: The conditions for {} aren't a json object", itemID,  elem);
						}
					}
					stopWorking();
				}
				
				if(object.has("weight_modifier") && object.get("weight_modifier").isJsonPrimitive()) {
					weightModifierIn = workOnFloat("weight_modifier", object);
				}
			} catch (ClassCastException e) {
				Survive.getInstance().getLogger().warn(ARMOR_DATA, "Error loading armor data {} from JSON: Parsing element {}: element was wrong type!", itemID, getworkingOn());
			} catch (NumberFormatException e) {
				Survive.getInstance().getLogger().warn(ARMOR_DATA, "Error loading armor data {} from JSON: Parsing element {}: element was an invalid number!", itemID, getworkingOn());
			}
		}
		
		if (weightModifierIn < 0) {
			Survive.getInstance().getLogger().warn(ARMOR_DATA, "Error loading armor data {} from JSON: Parsing element {}: weight is less than zero, please fix this!", itemID, "weight_modifier");
			weightModifierIn = 0;
		}
		
		this.temperatureModifier = temperatureModifierIn;
		this.weightModifier = weightModifierIn;
	}

	public ResourceLocation getItemID() {
		return itemID;
	}

	/**
	 * @return the temperatureModifier
	 */
	public List<Pair<String,TemperatureChangeInstance>> getTemperatureModifier() {
		return temperatureModifier;
	}

	/**
	 * @return the weightModifier
	 */
	public float getWeightModifier() {
		return weightModifier;
	}

	@Override
	public CompoundTag serialize() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("id", this.itemID.toString());
		nbt.putFloat("weight_modifier", this.weightModifier);
		ListTag list = new ListTag();
		this.temperatureModifier.forEach((mod) -> {
			CompoundTag entry = new CompoundTag();
			entry.putString("condition", mod.getFirst());
			entry.put("contents", mod.getSecond().serialize());
			list.add(entry);
		});
		nbt.put("temperature_modifiers", list);
		return nbt;
	}

	@Override
	public ArmorJsonHolder deserialize(CompoundTag input) {
		return new ArmorJsonHolder(input);
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
}
