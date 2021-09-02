package com.stereowalker.survive.util;

import net.minecraft.util.DamageSource;

public class SDamageSource {
	public static final DamageSource HYPOTHERMIA = (new DamageSource("hypothermia")).setDamageBypassesArmor().setDifficultyScaled();
	public static final DamageSource HYPERTHERMIA = (new DamageSource("hyperthermia")).setDamageBypassesArmor().setDifficultyScaled();
	public static final DamageSource DEHYDRATE = (new DamageSource("dehydrate")).setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource OVERWORK = (new DamageSource("overwork")).setDamageBypassesArmor().setDamageIsAbsolute();
}
