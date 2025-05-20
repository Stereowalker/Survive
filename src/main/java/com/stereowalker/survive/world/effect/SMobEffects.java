package com.stereowalker.survive.world.effect;

import java.util.UUID;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.SurviveUUIDS;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.core.registries.Housing;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;
import com.stereowalker.unionlib.world.entity.ai.UAttributes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SMobEffects {
	@RegistryObject("thirst")
	public static final Housing<MobEffect> THIRST = Housing.create(() -> new ThirstMobEffect(MobEffectCategory.HARMFUL, 5797459));
	@RegistryObject("heat_resistance")
	public static final Housing<MobEffect> HEAT_RESISTANCE = Housing.create(() -> new HeatedAndChilledMobEffect(MobEffectCategory.BENEFICIAL, 12221756).addAttributeModifier(SAttributes.HEAT_RESISTANCE.holder().value(), "795606d6-4ac6-4ae7-8311-63ccdb293eb3", 20.0D, AttributeModifier.Operation.ADDITION));
	@RegistryObject("cold_resistance")
	public static final Housing<MobEffect> COLD_RESISTANCE = Housing.create(() -> new HeatedAndChilledMobEffect(MobEffectCategory.BENEFICIAL, 5750248).addAttributeModifier(SAttributes.COLD_RESISTANCE.holder().value(), SurviveUUIDS.COLD_RESISTANCE, 20.0D, AttributeModifier.Operation.ADDITION));
	@RegistryObject("tiredness")
	public static final Housing<MobEffect> TIREDNESS = Housing.create(() -> new TirednessMobEffect(MobEffectCategory.BENEFICIAL, 0xaa6666)
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160891", (double)-0.005F, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(UAttributes.MCBackport.BLOCK_BREAK_SPEED.holder().value(), "55FCED67-E92A-486E-9800-B47F202C4387", (double)-0.2F, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "5cebe402-4f28-4d41-8539-2496f900ef99", -0.4D, AttributeModifier.Operation.ADDITION));
	@RegistryObject("chilled")
	public static final Housing<MobEffect> CHILLED = Housing.create(() -> new HeatedAndChilledMobEffect(MobEffectCategory.BENEFICIAL, 5750248));
	@RegistryObject("heated")
	public static final Housing<MobEffect> HEATED = Housing.create(() -> new HeatedAndChilledMobEffect(MobEffectCategory.BENEFICIAL, 16750592));
	@RegistryObject("energized")
	public static final Housing<MobEffect> ENERGIZED = Housing.create(() -> new EnergizedMobEffect(MobEffectCategory.BENEFICIAL, 16750592));
	@RegistryObject("fatigue")
	public static final Housing<MobEffect> FATIGUE = Housing.create(() -> new EnergizedMobEffect(MobEffectCategory.HARMFUL, 16750592)
			.addAttributeModifier(Attributes.ATTACK_SPEED, UUID.nameUUIDFromBytes("survive:fatigue_atk".getBytes()).toString(), -0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(UAttributes.MCBackport.BLOCK_BREAK_SPEED.holder().value(), UUID.nameUUIDFromBytes("survive:fatigue_blk".getBytes()).toString(), -0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL));
	@RegistryObject("slowness_illness")
	public static final Housing<MobEffect> SLOWNESS_ILLNESS = Housing.create(() -> (new UnwellMobEffect(MobEffectCategory.HARMFUL, 5926017)).addAttributeModifier(Attributes.MOVEMENT_SPEED, SurviveUUIDS.UNWELL_SLOWNESS, (double)-0.1125F, AttributeModifier.Operation.MULTIPLY_TOTAL));
	@RegistryObject("weakness_illness")
	public static final Housing<MobEffect> WEAKNESS_ILLNESS = Housing.create(() -> (new UnwellMobEffect(MobEffectCategory.HARMFUL, 4738376)).addAttributeModifier(Attributes.ATTACK_DAMAGE, SurviveUUIDS.UNWELL_WEAKNESS, -3.0D, AttributeModifier.Operation.ADDITION));
	@RegistryObject("hypothermia")
	public static final Housing<MobEffect> HYPOTHERMIA = Housing.create(() -> new HypothermiaMobEffect(MobEffectCategory.HARMFUL, 5750248));
	@RegistryObject("hyperthermia")
	public static final Housing<MobEffect> HYPERTHERMIA = Housing.create(() -> new HyperthermiaMobEffect(MobEffectCategory.HARMFUL, 16750592));
	@RegistryObject("upset_stomach")
	public static final Housing<MobEffect> UPSET_STOMACH =Housing.create(() ->  (new HeatedAndChilledMobEffect(MobEffectCategory.HARMFUL, 5926017)).addAttributeModifier(Attributes.MOVEMENT_SPEED, SurviveUUIDS.UPSET___STOMACH, (double)-0.045F, AttributeModifier.Operation.MULTIPLY_TOTAL));
	@RegistryObject("well_fed")
	public static final Housing<MobEffect> WELL_FED = Housing.create(() -> (new HeatedAndChilledMobEffect(MobEffectCategory.HARMFUL, 5926017)).addAttributeModifier(Attributes.MOVEMENT_SPEED, SurviveUUIDS.WELL_FED_SPEED_, (double)0.005F, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.ATTACK_DAMAGE, SurviveUUIDS.WELL_FED_DAMAGE, (double)0.005F, AttributeModifier.Operation.MULTIPLY_TOTAL));
}
