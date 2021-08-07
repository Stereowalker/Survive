package com.stereowalker.survive.util.data;

import java.util.List;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.registries.SurviveRegistries;
import com.stereowalker.survive.temperature.TemperatureChangeCondition;
import com.stereowalker.survive.temperature.TemperatureChangeInstance;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;

public class ArmorData extends JsonData {
    private static final Marker ARMOR_DATA = MarkerManager.getMarker("ARMOR_DATA");
    
	private ResourceLocation itemID;
	private final List<Pair<String,TemperatureChangeInstance>> temperatureModifier;
	private final float weightModifier;
	
	public ArmorData(CompoundNBT nbt) {
		super(nbt);
		this.itemID = new ResourceLocation(nbt.getString("id"));
		this.weightModifier = nbt.getFloat("weight_modifier");
		this.temperatureModifier = Lists.newArrayList();
		
		nbt.getList("temperature_modifiers", 10).forEach((comp) -> {
			CompoundNBT nbt2 = (CompoundNBT) comp;
			this.temperatureModifier.add(Pair.of(nbt2.getString("condition"), SurviveRegistries.CONDITION.getValue(new ResourceLocation(nbt2.getString("condition"))).createInstance(nbt2.getCompound("contents"))));
		});
	}
	
	public ArmorData(ResourceLocation itemID, JsonObject object) {
		super(object);
		String NOTHING = "nothing";
		
		List<Pair<String,TemperatureChangeInstance>> temperatureModifierIn = Lists.newArrayList();
		float weightModifierIn = 0;
		
		this.itemID = itemID;
		if(object.entrySet().size() != 0) {
			String workingOn = NOTHING;
			try {
				
				if(this.hasMemberAndIsJsonArray("temperature_modifiers", object)) {
					workingOn = "temperature_modifiers";
					for (JsonElement elem : object.get("temperature_modifiers").getAsJsonArray()) {
						if (elem.isJsonObject()) {
							JsonObject object2 = elem.getAsJsonObject();
							TemperatureChangeCondition<?> condition = null;

							if(object2 != null && object2.entrySet().size() != 0) {
								if(object2.has("condition") && object2.get("condition").isJsonPrimitive()) {
									workingOn = "condition";
									condition = SurviveRegistries.CONDITION.getValue(new ResourceLocation(object2.get("condition").getAsString()));
									if (condition != null) {
										temperatureModifierIn.add(Pair.of(object2.get("condition").getAsString(), condition.createInstance(object2)));
									} else {
										Survive.getInstance().getLogger().error("Error loading armor data {} from JSON: The condition {} does not exist", itemID,  new ResourceLocation(object2.get("condition").getAsString()));
									}
									workingOn = NOTHING;
								}
							}
						} else {
							Survive.getInstance().getLogger().error("Error loading armor data {} from JSON: The conditions for {} aren't a json object", itemID,  elem);
						}
					}
					workingOn = NOTHING;
				}
				
				if(object.has("weight_modifier") && object.get("weight_modifier").isJsonPrimitive()) {
					workingOn = "weight_modifier";
					weightModifierIn = object.get("weight_modifier").getAsFloat();
					workingOn = NOTHING;
				}
			} catch (ClassCastException e) {
				Survive.getInstance().getLogger().warn(ARMOR_DATA, "Error loading armor data {} from JSON: Parsing element {}: element was wrong type!", itemID, workingOn);
			} catch (NumberFormatException e) {
				Survive.getInstance().getLogger().warn(ARMOR_DATA, "Error loading armor data {} from JSON: Parsing element {}: element was an invalid number!", itemID, workingOn);
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
	public CompoundNBT serialize() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("id", this.itemID.toString());
		nbt.putFloat("weight_modifier", this.weightModifier);
		ListNBT list = new ListNBT();
		this.temperatureModifier.forEach((mod) -> {
			CompoundNBT entry = new CompoundNBT();
			entry.putString("condition", mod.getFirst());
			entry.put("contents", mod.getSecond().serialize());
			list.add(entry);
		});
		nbt.put("temperature_modifiers", list);
		return nbt;
	}
}
