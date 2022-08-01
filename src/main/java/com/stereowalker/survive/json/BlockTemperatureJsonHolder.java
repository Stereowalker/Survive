package com.stereowalker.survive.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.IBlockPropertyHandler;
import com.stereowalker.survive.api.IBlockPropertyHandler.PropertyPair;
import com.stereowalker.survive.api.json.JsonHolder;
import com.stereowalker.survive.json.property.BlockPropertyHandlerImpl;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockTemperatureJsonHolder implements JsonHolder {
	private static final Marker BLOCK_TEMPERATURE_DATA = MarkerManager.getMarker("BLOCK_TEMPERATURE_DATA");
	private ResourceLocation itemID;
	private final float temperatureModifier;
	private final int range;
	@Deprecated
	private final boolean usesLevelProperty;
	private final List<Triple<IBlockPropertyHandler<?>,List<PropertyPair<?>>,Map<String,Float>>> statePropertyOverride;

	public BlockTemperatureJsonHolder(ResourceLocation blockID, JsonObject object) {
		String CHANGE_PROPERTY = "blockstate_property_overrides";
		String LEVEL_PROPERTY = "uses_level_property";
		String RANGE = "range";

		float temperatureIn = 0;
		boolean usesLevelPropertyIn = false;
		//
		List<Triple<IBlockPropertyHandler<?>,List<PropertyPair<?>>,Map<String,Float>>> stateChangePropertyIn = Lists.newArrayList();
		//
		int rangeIn = 0;

		this.itemID = blockID;
		if(object.entrySet().size() != 0) {
			stopWorking();
			try {
				if(this.hasMemberAndIsPrimitive("temperature_modifier", object)) {
					temperatureIn = workOnFloat("temperature_modifier", object);
				}

				//TODO: This is the legacy system which will be removed in a future update
				if(this.hasMemberAndIsPrimitive("uses_lit_or_active_property", object)) {
					setWorkingOn("uses_lit_or_active_property");
					Map<String,Float> vals = Maps.newHashMap();
					vals.put("lit", temperatureIn);
					vals.put("active", temperatureIn);
					temperatureIn = 0;
					stateChangePropertyIn.add(Triple.of(IBlockPropertyHandler.SAVED_PROPERTIES.get("boolean"), new ArrayList<PropertyPair<?>>(), vals));
					stopWorking();
				} else if(this.hasMemberAndIsJsonArray(CHANGE_PROPERTY, object)) {
					setWorkingOn(CHANGE_PROPERTY);
					for (JsonElement e : object.get(CHANGE_PROPERTY).getAsJsonArray()) {
						if (IBlockPropertyHandler.SAVED_PROPERTIES.containsKey(e.getAsJsonObject().get("type").getAsString())) {
							JsonObject o = e.getAsJsonObject();
							IBlockPropertyHandler<?> handler = IBlockPropertyHandler.SAVED_PROPERTIES.get(o.get("type").getAsString());
							Map<String,Float> vals = BlockPropertyHandlerImpl.deserialize(handler, o);
							List<PropertyPair<?>> requirements = Lists.newArrayList();
							if (this.hasMemberAndIsJsonArray("requires", o))
								o.get("requires").getAsJsonArray().forEach((eme) -> {
									String type = eme.getAsJsonObject().get("type").getAsString();
									eme.getAsJsonObject().remove("type");
									requirements.add(IBlockPropertyHandler.SAVED_PROPERTIES.get(type).requirements(eme.getAsJsonObject()));
								});
							o.remove("type");
							stateChangePropertyIn.add(Triple.of(handler, requirements, vals));
						} else {

						}
					}
					stopWorking();
				}

				if(object.has(LEVEL_PROPERTY) && object.get(LEVEL_PROPERTY).isJsonPrimitive()) {
					setWorkingOn(LEVEL_PROPERTY);
					usesLevelPropertyIn = object.get(LEVEL_PROPERTY).getAsBoolean();
					stopWorking();
				}

				if(object.has(RANGE) && object.get(RANGE).isJsonPrimitive()) {
					setWorkingOn(RANGE);
					rangeIn = object.get(RANGE).getAsInt();
					stopWorking();
				}
			} catch (ClassCastException e) {
				Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was wrong type!", e, blockID, getworkingOn());
			} catch (NumberFormatException e) {
				Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Parsing element %s: element was an invalid number!", e, blockID, getworkingOn());
			}
		}

		if (rangeIn > 5) {
			Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Range should not be greater that 5", blockID);
			rangeIn = 5;
		}

		if (rangeIn < 0) {
			Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: Range should not be less than 0", blockID);
			rangeIn = 0;
		}

		List<Triple<IBlockPropertyHandler<?>, List<PropertyPair<?>>, Map<String, Float>>> toRemove = Lists.newArrayList();
		for (Triple<IBlockPropertyHandler<?>, List<PropertyPair<?>>, Map<String, Float>> prop : stateChangePropertyIn) {
			if (!ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(prop.getLeft().derivedProperty())) {
				Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data {} from JSON: This block has doesn't have the \"{}\" property, please remove this line", blockID, prop.getLeft().derivedProperty().getName());
				toRemove.add(prop);
			}
		}
		toRemove.forEach((s) -> stateChangePropertyIn.remove(s));

		if (usesLevelPropertyIn 
				&& ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(BlockStateProperties.LEVEL) 
				&& ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(BlockStateProperties.LEVEL_CAULDRON)
				&& ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(BlockStateProperties.LEVEL_COMPOSTER)
				&& ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(BlockStateProperties.LEVEL_FLOWING)) {
			Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: This block does not have the level property, please set this to false", blockID);
			usesLevelPropertyIn = false;
		}

		this.temperatureModifier = temperatureIn;
		this.usesLevelProperty = usesLevelPropertyIn;

		if (stateChangePropertyIn.isEmpty())
			this.statePropertyOverride = null;
		else 
			this.statePropertyOverride = stateChangePropertyIn;
		this.range = rangeIn;

	}

	public ResourceLocation getItemID() {
		return itemID;
	}

	/**
	 * @return the temperatureModifier
	 */
	public float getTemperatureModifier() {
		return temperatureModifier;
	}

	/**
	 * @return
	 */
	public List<Triple<IBlockPropertyHandler<?>,List<PropertyPair<?>>,Map<String,Float>>> getStateChangeProperty() {
		return this.statePropertyOverride;
	}

	/**
	 * @return the range
	 */
	public int getRange() {
		return range;
	}

	/**
	 * @return the usesLevelProperty
	 */
	@Deprecated
	public boolean usesLevelProperty() {
		return usesLevelProperty;
	}

	@Override
	public CompoundTag serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	String wo = "NOTHING";
	@Override
	public String getworkingOn() {return wo;}

	@Override
	public void setWorkingOn(String member) {
		this.wo = member;
	}
}
