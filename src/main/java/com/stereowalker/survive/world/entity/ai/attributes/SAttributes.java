package com.stereowalker.survive.world.entity.ai.attributes;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

@RegistryHolder(registry = Attribute.class)
public class SAttributes {
	@RegistryObject("generic.max_stamina")
	public static final Attribute MAX_STAMINA = new RangedAttribute("attribute.name.survive.max_stamina", Survive.STAMINA_CONFIG.max_stamina, 1.0D, 1024.0D).setSyncable(true);
	@RegistryObject("generic.cold_resistance")
	public static final Attribute COLD_RESISTANCE = new RangedAttribute("attribute.name.survive.coldResistance", 2.0D, 0.0D, 1024.0D).setSyncable(true);
	@RegistryObject("generic.heat_resistance")
	public static final Attribute HEAT_RESISTANCE = new RangedAttribute("attribute.name.survive.heatResistance", 2.0D, 0.0D, 1024.0D).setSyncable(true);
}
