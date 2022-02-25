package com.stereowalker.survive.json;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.json.JsonHolder;
import com.stereowalker.unionlib.state.properties.UBlockStateProperties;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockTemperatureJsonHolder implements JsonHolder {
    private static final Marker BLOCK_TEMPERATURE_DATA = MarkerManager.getMarker("BLOCK_TEMPERATURE_DATA");
    
	private ResourceLocation itemID;
	private final float temperatureModifier;
	private final int range;
	private final boolean usesLitOrActiveProperty;
	private final boolean usesLevelProperty;
	
	public BlockTemperatureJsonHolder(ResourceLocation blockID, JsonObject object) {
		String TEMPERATURE_MODIFIER = "temperature_modifier";
		String LIT_PROPERTY = "uses_lit_or_active_property";
		String LEVEL_PROPERTY = "uses_level_property";
		String RANGE = "range";
		
		float temperatureModifierIn = 0;
		boolean usesLitOrActivePropertyIn = false;
		boolean usesLevelPropertyIn = false;
		int rangeIn = 0;
		
		this.itemID = blockID;
		if(object.entrySet().size() != 0) {
			stopWorking();
			try {
				if(object.has(TEMPERATURE_MODIFIER) && object.get(TEMPERATURE_MODIFIER).isJsonPrimitive()) {
					setWorkingOn(TEMPERATURE_MODIFIER);
					temperatureModifierIn = object.get(TEMPERATURE_MODIFIER).getAsFloat();
					stopWorking();
				}
				
				if(object.has(LIT_PROPERTY) && object.get(LIT_PROPERTY).isJsonPrimitive()) {
					setWorkingOn(LIT_PROPERTY);
					usesLitOrActivePropertyIn = object.get(LIT_PROPERTY).getAsBoolean();
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
		
		if (usesLitOrActivePropertyIn && ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(BlockStateProperties.LIT) && ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(UBlockStateProperties.ACTIVE)) {
			Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: This block has neither the lit property nor the active property, please set this to false", blockID);
			usesLitOrActivePropertyIn = false;
		}
		
		if (usesLevelPropertyIn 
				&& ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(BlockStateProperties.LEVEL) 
				&& ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(BlockStateProperties.LEVEL_CAULDRON)
				&& ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(BlockStateProperties.LEVEL_COMPOSTER)
				&& ForgeRegistries.BLOCKS.getValue(blockID).defaultBlockState().hasProperty(BlockStateProperties.LEVEL_FLOWING)) {
			Survive.getInstance().getLogger().warn(BLOCK_TEMPERATURE_DATA, "Loading block temperature data $s from JSON: This block does not have the level property, please set this to false", blockID);
			usesLitOrActivePropertyIn = false;
		}
		
		this.temperatureModifier = temperatureModifierIn;
		this.usesLitOrActiveProperty = usesLitOrActivePropertyIn;
		this.usesLevelProperty = usesLevelPropertyIn;
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
	 * @return the usesLitOrActiveProperty
	 */
	public boolean usesLitOrActiveProperty() {
		return usesLitOrActiveProperty;
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
