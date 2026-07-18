package com.stereowalker.survive.world.item.crafting.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stereowalker.survive.Survive;

import net.minecraftforge.common.crafting.conditions.ICondition;


public record ModuleEnabledCondition(String module) implements ICondition 
{
    public static final MapCodec<ModuleEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
        Codec.STRING.fieldOf("module").forGetter(ModuleEnabledCondition::module)
    ).apply(b, ModuleEnabledCondition::new));
    
//  @Override
//  public Identifier getID()
//  {
//      return NAME;
//  }

    @Override
    public boolean test(IContext context, DynamicOps<?> ops) {
    	switch (module) {
		case "hygiene":
			return Survive.HYGIENE_CONFIG.enabled;
		default:
			return false;
		}
    }

    @Override
    public String toString() {
    	return "module_enabled(\"" + module + "\")";
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

}
