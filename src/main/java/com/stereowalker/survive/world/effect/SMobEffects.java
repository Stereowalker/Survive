package com.stereowalker.survive.world.effect;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.SurviveUUIDS;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.core.registries.Housing;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SMobEffects {
	@RegistryObject("thirst")
	public static final Housing<MobEffect> THIRST = Housing.create(() -> new ThirstMobEffect(MobEffectCategory.HARMFUL, 5797459));
	@RegistryObject("heat_resistance")
	public static final Housing<MobEffect> HEAT_RESISTANCE = Housing.create(() -> new HeatedAndChilledMobEffect(MobEffectCategory.BENEFICIAL, 12221756).addAttributeModifier(SAttributes.HEAT_RESISTANCE.holder(), "795606d6-4ac6-4ae7-8311-63ccdb293eb3", 20.0D, AttributeModifier.Operation.ADD_VALUE));
	@RegistryObject("cold_resistance")
	public static final Housing<MobEffect> COLD_RESISTANCE = Housing.create(() -> new HeatedAndChilledMobEffect(MobEffectCategory.BENEFICIAL, 5750248).addAttributeModifier(SAttributes.COLD_RESISTANCE.holder(), SurviveUUIDS.COLD_RESISTANCE, 20.0D, AttributeModifier.Operation.ADD_VALUE));
	@RegistryObject("tiredness")
	public static final Housing<MobEffect> TIREDNESS = Housing.create(() -> new TirednessMobEffect(MobEffectCategory.BENEFICIAL, 0xaa6666)
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160891", (double)-0.005F, AttributeModifier.Operation.ADD_VALUE)
			.addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4387", (double)-0.2F, AttributeModifier.Operation.ADD_VALUE)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "5cebe402-4f28-4d41-8539-2496f900ef99", -0.4D, AttributeModifier.Operation.ADD_VALUE));
	@RegistryObject("chilled")
	public static final Housing<MobEffect> CHILLED = Housing.create(() -> new HeatedAndChilledMobEffect(MobEffectCategory.BENEFICIAL, 5750248));
	@RegistryObject("heated")
	public static final Housing<MobEffect> HEATED = Housing.create(() -> new HeatedAndChilledMobEffect(MobEffectCategory.BENEFICIAL, 16750592));
	@RegistryObject("energized")
	public static final Housing<MobEffect> ENERGIZED = Housing.create(() -> new EnergizedMobEffect(MobEffectCategory.BENEFICIAL, 16750592));
	@RegistryObject("slowness_illness")
	public static final Housing<MobEffect> SLOWNESS_ILLNESS = Housing.create(() -> (new UnwellMobEffect(MobEffectCategory.HARMFUL, 5926017)).addAttributeModifier(Attributes.MOVEMENT_SPEED, SurviveUUIDS.UNWELL_SLOWNESS, (double)-0.15F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
	@RegistryObject("weakness_illness")
	public static final Housing<MobEffect> WEAKNESS_ILLNESS = Housing.create(() -> (new UnwellMobEffect(MobEffectCategory.HARMFUL, 4738376)).addAttributeModifier(Attributes.ATTACK_DAMAGE, SurviveUUIDS.UNWELL_WEAKNESS, -4.0D, AttributeModifier.Operation.ADD_VALUE));
	@RegistryObject("hypothermia")
	public static final Housing<MobEffect> HYPOTHERMIA = Housing.create(() -> new HypothermiaMobEffect(MobEffectCategory.HARMFUL, 5750248));
	@RegistryObject("hyperthermia")
	public static final Housing<MobEffect> HYPERTHERMIA = Housing.create(() -> new HyperthermiaMobEffect(MobEffectCategory.HARMFUL, 16750592));
	@RegistryObject("upset_stomach")
	public static final Housing<MobEffect> UPSET_STOMACH =Housing.create(() ->  (new HeatedAndChilledMobEffect(MobEffectCategory.HARMFUL, 5926017)).addAttributeModifier(Attributes.MOVEMENT_SPEED, SurviveUUIDS.UPSET___STOMACH, (double)-0.045F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
	@RegistryObject("well_fed")
	public static final Housing<MobEffect> WELL_FED = Housing.create(() -> (new HeatedAndChilledMobEffect(MobEffectCategory.HARMFUL, 5926017)).addAttributeModifier(Attributes.MOVEMENT_SPEED, SurviveUUIDS.WELL_FED_SPEED_, (double)0.005F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).addAttributeModifier(Attributes.ATTACK_DAMAGE, SurviveUUIDS.WELL_FED_DAMAGE, (double)0.005F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
}
