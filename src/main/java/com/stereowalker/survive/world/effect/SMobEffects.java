package com.stereowalker.survive.world.effect;

import com.stereowalker.survive.world.SurviveUUIDS;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;
import com.stereowalker.unionlib.world.entity.ai.UAttributes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

@RegistryHolder(registry = MobEffect.class)
public class SMobEffects {
	@Deprecated
	@RegistryObject("depreciated_hypothermia")
	public static final MobEffect DEPRECIATED_HYPOTHERMIA = new SMobEffect(MobEffectCategory.HARMFUL, 5750248);
	@Deprecated
	@RegistryObject("depreciated_hyperthermia")
	public static final MobEffect DEPRECIATED_HYPERTHERMIA = new SMobEffect(MobEffectCategory.HARMFUL, 16750592);
	@RegistryObject("thirst")
	public static final MobEffect THIRST = new SMobEffect(MobEffectCategory.HARMFUL, 5797459);
	@RegistryObject("heat_resistance")
	public static final MobEffect HEAT_RESISTANCE = new SMobEffect(MobEffectCategory.BENEFICIAL, 12221756).addAttributeModifier(SAttributes.HEAT_RESISTANCE, "795606d6-4ac6-4ae7-8311-63ccdb293eb3", 20.0D, AttributeModifier.Operation.ADDITION);
	@RegistryObject("cold_resistance")
	public static final MobEffect COLD_RESISTANCE = new SMobEffect(MobEffectCategory.BENEFICIAL, 5750248).addAttributeModifier(SAttributes.COLD_RESISTANCE, SurviveUUIDS.COLD_RESISTANCE, 20.0D, AttributeModifier.Operation.ADDITION);
	@RegistryObject("tiredness")
	public static final MobEffect TIREDNESS = new SMobEffect(MobEffectCategory.BENEFICIAL, 0xaa6666)
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160891", (double)-0.005F, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(UAttributes.DIG_SPEED, "55FCED67-E92A-486E-9800-B47F202C4387", (double)-0.2F, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "5cebe402-4f28-4d41-8539-2496f900ef99", -0.4D, AttributeModifier.Operation.ADDITION);
	@RegistryObject("chilled")
	public static final MobEffect CHILLED = new SMobEffect(MobEffectCategory.BENEFICIAL, 5750248);
	@RegistryObject("heated")
	public static final MobEffect HEATED = new SMobEffect(MobEffectCategory.BENEFICIAL, 16750592);
	@RegistryObject("energized")
	public static final MobEffect ENERGIZED = new SMobEffect(MobEffectCategory.BENEFICIAL, 16750592);
	@RegistryObject("slowness_illness")
	public static final MobEffect SLOWNESS_ILLNESS = (new UnwellMobEffect(MobEffectCategory.HARMFUL, 5926017)).addAttributeModifier(Attributes.MOVEMENT_SPEED, SurviveUUIDS.UNWELL_SLOWNESS, (double)-0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL);
	@RegistryObject("weakness_illness")
	public static final MobEffect WEAKNESS_ILLNESS = (new UnwellAttackDamageMobEffect(MobEffectCategory.HARMFUL, 4738376, -4.0D)).addAttributeModifier(Attributes.ATTACK_DAMAGE, SurviveUUIDS.UNWELL_WEAKNESS, 0.0D, AttributeModifier.Operation.ADDITION);
	@RegistryObject("hypothermia")
	public static final MobEffect HYPOTHERMIA = new UnwellMobEffect(MobEffectCategory.HARMFUL, 5750248);
	@RegistryObject("hyperthermia")
	public static final MobEffect HYPERTHERMIA = new UnwellMobEffect(MobEffectCategory.HARMFUL, 16750592);
	@RegistryObject("upset_stomach")
	public static final MobEffect UPSET_STOMACH = (new SMobEffect(MobEffectCategory.HARMFUL, 5926017)).addAttributeModifier(Attributes.MOVEMENT_SPEED, SurviveUUIDS.UPSET___STOMACH, (double)-0.05F, AttributeModifier.Operation.MULTIPLY_TOTAL);
}
