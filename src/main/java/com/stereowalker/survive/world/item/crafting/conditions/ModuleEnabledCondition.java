package com.stereowalker.survive.world.item.crafting.conditions;

import com.google.gson.JsonObject;
import com.stereowalker.survive.Survive;

import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ModuleEnabledCondition implements ICondition
{
    private static final ResourceLocation NAME = new ResourceLocation("survive", "module_enabled");
    private final String module;

    public ModuleEnabledCondition(String module)
    {
        this.module = module;
    }

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test()
    {
        switch (module) {
		case "hygiene":
			return Survive.HYGIENE_CONFIG.enabled;
		default:
			return false;
		}
    }

    @Override
    public String toString()
    {
        return "module_enabled(\"" + module + "\")";
    }

    public static class Serializer implements IConditionSerializer<ModuleEnabledCondition>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ModuleEnabledCondition value)
        {
            json.addProperty("module", value.module);
        }

        @Override
        public ModuleEnabledCondition read(JsonObject json)
        {
            return new ModuleEnabledCondition(GsonHelper.getAsString(json, "module"));
        }

        @Override
        public ResourceLocation getID()
        {
            return ModuleEnabledCondition.NAME;
        }
    }
}
