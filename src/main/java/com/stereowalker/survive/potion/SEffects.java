package com.stereowalker.survive.potion;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.entity.ai.SAttributes;
import com.stereowalker.unionlib.entity.ai.UAttributes;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.registries.IForgeRegistry;

public class SEffects {
	public static List<Effect> EFFECT = new ArrayList<Effect>();
	
	public static final Effect HYPOTHERMIA = register("hypothermia", new SEffect(EffectType.HARMFUL, 5750248));
	public static final Effect HYPERTHERMIA = register("hyperthermia", new SEffect(EffectType.HARMFUL, 16750592));
	public static final Effect THIRST = register("thirst", new SEffect(EffectType.HARMFUL, 5797459));
	public static final Effect HEAT_RESISTANCE = register("heat_resistance", new SEffect(EffectType.BENEFICIAL, 12221756)).addAttributesModifier(SAttributes.HEAT_RESISTANCE, "795606d6-4ac6-4ae7-8311-63ccdb293eb3", 20.0D, AttributeModifier.Operation.ADDITION);
	public static final Effect COLD_RESISTANCE = register("cold_resistance", new SEffect(EffectType.BENEFICIAL, 5750248)).addAttributesModifier(SAttributes.COLD_RESISTANCE, "5cebe402-4f28-4d41-8539-2496f900ef90", 20.0D, AttributeModifier.Operation.ADDITION);
	public static final Effect TIREDNESS = register("tiredness", new SEffect(EffectType.BENEFICIAL, 0xaa6666))
			.addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160891", (double)-0.005F, AttributeModifier.Operation.ADDITION)
			.addAttributesModifier(UAttributes.DIG_SPEED, "55FCED67-E92A-486E-9800-B47F202C4387", (double)-0.2F, AttributeModifier.Operation.ADDITION)
			.addAttributesModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "5cebe402-4f28-4d41-8539-2496f900ef99", -0.4D, AttributeModifier.Operation.ADDITION);
	
	public static final Effect CHILLED = register("chilled", new SEffect(EffectType.BENEFICIAL, 5750248));
	public static final Effect HEATED = register("heated", new SEffect(EffectType.BENEFICIAL, 16750592));
	public static final Effect ENERGIZED = register("energized", new SEffect(EffectType.BENEFICIAL, 16750592));
	
	public static void registerAll(IForgeRegistry<Effect> registry) {
		for(Effect effect : EFFECT) {
			registry.register(effect);
			Survive.debug("Effect: \""+effect.getRegistryName().toString()+"\" registered");
		}
		Survive.debug("All Effects Registered");
	}
	
	public static Effect register(String name, Effect effect) {
		effect.setRegistryName(Survive.location(name));
		EFFECT.add(effect);
		return effect;
	}
}
