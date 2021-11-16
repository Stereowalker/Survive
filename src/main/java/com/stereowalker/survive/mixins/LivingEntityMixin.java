package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.TemperatureData;
import com.stereowalker.survive.needs.TemperatureUtil;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Shadow public abstract ItemStack getItemBySlot(EquipmentSlot pSlot);

	@Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;isInPowderSnow:Z"), method = "aiStep")
	public boolean ff(LivingEntity living) {
		if ((LivingEntity)(Object)this instanceof Player && Survive.TEMPERATURE_CONFIG.useExperimentalTemperatureSystem && Survive.TEMPERATURE_CONFIG.enabled) {
			TemperatureData data = SurviveEntityStats.getTemperatureStats((LivingEntity)(Object)this);
			return data.getTemperatureLevel() < TemperatureUtil.firstCold((Player)(Object)this) || this.isInPowderSnow;
		}
		return this.isInPowderSnow;
	}

	@Overwrite
	public boolean canFreeze() {
		if (this.isSpectator()) {
			return false;
		} else {
			if ((LivingEntity)(Object)this instanceof Player && Survive.TEMPERATURE_CONFIG.useExperimentalTemperatureSystem && Survive.TEMPERATURE_CONFIG.enabled) {
				TemperatureData data = SurviveEntityStats.getTemperatureStats((LivingEntity)(Object)this);
				return data.getTemperatureLevel() < TemperatureUtil.firstCold((Player)(Object)this) && super.canFreeze();
			} else {
				boolean flag = !this.getItemBySlot(EquipmentSlot.HEAD).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.LEGS).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.FEET).is(ItemTags.FREEZE_IMMUNE_WEARABLES);
				return flag && super.canFreeze();
			}
		}
	}

}