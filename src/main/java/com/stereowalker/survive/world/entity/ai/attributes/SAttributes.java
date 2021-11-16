package com.stereowalker.survive.world.entity.ai.attributes;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.IForgeRegistry;

public class SAttributes {
	public static List<Attribute> ATTRIBUTE = new ArrayList<Attribute>();
	
	public static final Attribute COLD_RESISTANCE = registerTemp("generic.cold_resistance", new RangedAttribute("attribute.name.survive.coldResistance", 2.0D, 0.0D, 1024.0D)).setSyncable(true);
	public static final Attribute HEAT_RESISTANCE = registerTemp("generic.heat_resistance", new RangedAttribute("attribute.name.survive.heatResistance", 2.0D, 0.0D, 1024.0D)).setSyncable(true);
	
	public static Attribute registerTemp(String name, Attribute attribute) {
		if (Survive.TEMPERATURE_CONFIG.enabled) {
			return register(name, attribute);
		} else {
			return attribute;
		}
	}
	
	public static Attribute register(String name, Attribute attribute) {
		attribute.setRegistryName(Survive.getInstance().location(name));
		ATTRIBUTE.add(attribute);
		return attribute;
	}
	
	public static void registerAll(IForgeRegistry<Attribute> registry) {
		for(Attribute attribute : ATTRIBUTE) {
			registry.register(attribute);
			Survive.getInstance().debug("Attribute: \""+attribute.getRegistryName().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Attributes Registered");
	}
	
}
