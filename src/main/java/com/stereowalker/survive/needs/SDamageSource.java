package com.stereowalker.survive.needs;

import net.minecraft.world.damagesource.DamageSource;

public class SDamageSource {
	public static final DamageSource HYPOTHERMIA = (new DamageSource("hypothermia")).bypassArmor().setScalesWithDifficulty();
	public static final DamageSource HYPERTHERMIA = (new DamageSource("hyperthermia")).bypassArmor().setScalesWithDifficulty();
	public static final DamageSource ROAST = (new DamageSource("roast")).bypassArmor();
	public static final DamageSource DEHYDRATE = (new DamageSource("dehydrate")).bypassArmor().bypassMagic();
	public static final DamageSource OVERHYDRATE = (new DamageSource("overhydrate")).bypassArmor().bypassMagic();
	public static final DamageSource OVEREAT = (new DamageSource("overeat")).bypassArmor().bypassMagic();
	public static final DamageSource OVERWORK = (new DamageSource("overwork")).bypassArmor().bypassMagic();
}
