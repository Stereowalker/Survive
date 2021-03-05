package com.stereowalker.survive.entity.ai;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class SAttributes {
	public static final IAttribute COLD_RESISTANCE = (new RangedAttribute((IAttribute)null, "attribute.name.survive.coldResistance", 2.0D, 0.0D, 1024.0D)).setShouldWatch(true);
	public static final IAttribute HEAT_RESISTANCE = (new RangedAttribute((IAttribute)null, "attribute.name.survive.heatResistance", 2.0D, 0.0D, 1024.0D)).setShouldWatch(true);
}
