package com.stereowalker.survive.world.effect;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.SurviveUUIDS;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.entity.ai.UAttributes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.IForgeRegistry;

public class SEffects {
	public static List<MobEffect> EFFECT = new ArrayList<MobEffect>();
	
	@Deprecated
	public static final MobEffect DEPRECIATED_HYPOTHERMIA = register("depreciated_hypothermia", new SEffect(MobEffectCategory.HARMFUL, 5750248));
	@Deprecated
	public static final MobEffect DEPRECIATED_HYPERTHERMIA = register("depreciated_hyperthermia", new SEffect(MobEffectCategory.HARMFUL, 16750592));
	public static final MobEffect THIRST = register("thirst", new SEffect(MobEffectCategory.HARMFUL, 5797459));
	public static final MobEffect HEAT_RESISTANCE = register("heat_resistance", new SEffect(MobEffectCategory.BENEFICIAL, 12221756)).addAttributeModifier(SAttributes.HEAT_RESISTANCE, "795606d6-4ac6-4ae7-8311-63ccdb293eb3", 20.0D, AttributeModifier.Operation.ADDITION);
	public static final MobEffect COLD_RESISTANCE = register("cold_resistance", new SEffect(MobEffectCategory.BENEFICIAL, 5750248)).addAttributeModifier(SAttributes.COLD_RESISTANCE, "5cebe402-4f28-4d41-8539-2496f900ef90", 20.0D, AttributeModifier.Operation.ADDITION);
	public static final MobEffect TIREDNESS = register("tiredness", new SEffect(MobEffectCategory.BENEFICIAL, 0xaa6666))
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160891", (double)-0.005F, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(UAttributes.DIG_SPEED, "55FCED67-E92A-486E-9800-B47F202C4387", (double)-0.2F, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "5cebe402-4f28-4d41-8539-2496f900ef99", -0.4D, AttributeModifier.Operation.ADDITION);
	
	public static final MobEffect CHILLED = register("chilled", new SEffect(MobEffectCategory.BENEFICIAL, 5750248));
	public static final MobEffect HEATED = register("heated", new SEffect(MobEffectCategory.BENEFICIAL, 16750592));
	public static final MobEffect ENERGIZED = register("energized", new SEffect(MobEffectCategory.BENEFICIAL, 16750592));
	public static final MobEffect SLOWNESS_ILLNESS = register("slowness_illness", (new UnwellMobEffect(MobEffectCategory.HARMFUL, 5926017)).addAttributeModifier(Attributes.MOVEMENT_SPEED, SurviveUUIDS.UNWELL_SLOWNESS, (double)-0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL));
	public static final MobEffect WEAKNESS_ILLNESS = register("weakness_illness", (new UnwellAttackDamageMobEffect(MobEffectCategory.HARMFUL, 4738376, -4.0D)).addAttributeModifier(Attributes.ATTACK_DAMAGE, SurviveUUIDS.UNWELL_WEAKNESS, 0.0D, AttributeModifier.Operation.ADDITION));
	public static final MobEffect HYPOTHERMIA = register("hypothermia", new UnwellMobEffect(MobEffectCategory.HARMFUL, 5750248));
	public static final MobEffect HYPERTHERMIA = register("hyperthermia", new UnwellMobEffect(MobEffectCategory.HARMFUL, 16750592));
	
	public static void registerAll(IForgeRegistry<MobEffect> registry) {
		for(MobEffect effect : EFFECT) {
			registry.register(effect);
			Survive.getInstance().debug("MobEffect: \""+effect.getRegistryName().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Effects Registered");
	}
	
	public static MobEffect register(String name, MobEffect effect) {
		effect.setRegistryName(Survive.getInstance().location(name));
		EFFECT.add(effect);
		return effect;
	}
}
