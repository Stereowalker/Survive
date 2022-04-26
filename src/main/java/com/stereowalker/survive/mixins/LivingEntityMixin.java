package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.SDamageSource;
import com.stereowalker.survive.needs.TemperatureData;
import com.stereowalker.survive.needs.TemperatureUtil;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {

	public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Shadow public abstract ItemStack getItemBySlot(EquipmentSlot pSlot);
	@Shadow public boolean hurt(DamageSource pSource, float pAmount) {return false;}

	@Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	public boolean aiStep_hurt_redirect(LivingEntity living, DamageSource pSource, float pAmount) {
		float amount = pAmount;
		
		if ((LivingEntity)(Object)this instanceof Player && !Survive.TEMPERATURE_CONFIG.useLegacyTemperatureSystem && Survive.TEMPERATURE_CONFIG.enabled) {
			double maxCold1 = TemperatureUtil.firstCold((Player)(Object)this);
			double maxCold2 = TemperatureUtil.secondCold((Player)(Object)this);
			double maxCold3 = TemperatureUtil.maxCold((Player)(Object)this);
			TemperatureData data = SurviveEntityStats.getTemperatureStats((LivingEntity)(Object)this);
			if (data.getTemperatureLevel() < maxCold1 && data.getTemperatureLevel() >= maxCold2) {
				amount *= 1;
			}
			else if (data.getTemperatureLevel() < maxCold2 && data.getTemperatureLevel() >= maxCold3) {
				amount *= 10;
			}
			else if (data.getTemperatureLevel() < maxCold3) {
				amount *= 100;
			}
		}
		
		return living.hurt(DamageSource.FREEZE, amount);
	}

	@Redirect(method = "aiStep", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;isInPowderSnow:Z"))
	public boolean aiStep_isInPowderSnow_redirect(LivingEntity living) {
		if ((LivingEntity)(Object)this instanceof Player && !Survive.TEMPERATURE_CONFIG.useLegacyTemperatureSystem && Survive.TEMPERATURE_CONFIG.enabled) {
			TemperatureData data = SurviveEntityStats.getTemperatureStats((LivingEntity)(Object)this);
			return data.getTemperatureLevel() < TemperatureUtil.firstCold((Player)(Object)this) || this.isInPowderSnow;
		}
		return this.isInPowderSnow;
	}

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getTicksFrozen()I"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void aiStep_inject_1(CallbackInfo ci) {
		int i = this.getTicksRoasted();
		if (this.canRoast() && (LivingEntity)(Object)this instanceof Player && !Survive.TEMPERATURE_CONFIG.useLegacyTemperatureSystem && Survive.TEMPERATURE_CONFIG.enabled) {
			this.setTicksRoasted(Math.min(this.getTicksRequiredToRoast(), i + 1));
		} else {
			this.setTicksRoasted(Math.max(0, i - 2));
		}
	}

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;tryAddFrost()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void aiStep_inject_2(CallbackInfo ci) {
		if (!this.level.isClientSide && this.tickCount % 40 == 0 && this.isFullyRoasted() && this.canRoast()) {
			int j = this.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES) ? 5 : 1;
			
			float amount = (float)j;
			
			if ((LivingEntity)(Object)this instanceof Player && !Survive.TEMPERATURE_CONFIG.useLegacyTemperatureSystem && Survive.TEMPERATURE_CONFIG.enabled) {
				double maxHeat1 = TemperatureUtil.firstHeat((Player)(Object)this);
				double maxHeat2 = TemperatureUtil.secondHeat((Player)(Object)this);
				double maxHeat3 = TemperatureUtil.maxHeat((Player)(Object)this);
				TemperatureData data = SurviveEntityStats.getTemperatureStats((LivingEntity)(Object)this);
				if (data.getTemperatureLevel() > maxHeat1 && data.getTemperatureLevel() <= maxHeat2) {
					amount *= 1;
				}
				else if (data.getTemperatureLevel() > maxHeat2 && data.getTemperatureLevel() <= maxHeat3) {
					amount *= 10;
				}
				else if (data.getTemperatureLevel() > maxHeat3) {
					amount *= 100;
				}
			}
			
			this.hurt(SDamageSource.ROAST, amount);
		}
	}

	@Overwrite
	public boolean canFreeze() {
		if (this.isSpectator()) {
			return false;
		} else {
			if ((LivingEntity)(Object)this instanceof Player && !Survive.TEMPERATURE_CONFIG.useLegacyTemperatureSystem && Survive.TEMPERATURE_CONFIG.enabled) {
				TemperatureData data = SurviveEntityStats.getTemperatureStats((LivingEntity)(Object)this);
				return data.getTemperatureLevel() < TemperatureUtil.firstCold((Player)(Object)this) && super.canFreeze();
			} else {
				boolean flag = !this.getItemBySlot(EquipmentSlot.HEAD).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.LEGS).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.FEET).is(ItemTags.FREEZE_IMMUNE_WEARABLES);
				return flag && super.canFreeze();
			}
		}
	}

	@Override
	public boolean canRoast() {
		if (this.isSpectator()) {
			return false;
		} else {
			if ((LivingEntity)(Object)this instanceof Player && !Survive.TEMPERATURE_CONFIG.useLegacyTemperatureSystem && Survive.TEMPERATURE_CONFIG.enabled) {
				TemperatureData data = SurviveEntityStats.getTemperatureStats((LivingEntity)(Object)this);
				return data.getTemperatureLevel() > TemperatureUtil.firstHeat((Player)(Object)this) && super.canRoast();
			} else {
				boolean flag = !this.getItemBySlot(EquipmentSlot.HEAD).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.LEGS).is(ItemTags.FREEZE_IMMUNE_WEARABLES) && !this.getItemBySlot(EquipmentSlot.FEET).is(ItemTags.FREEZE_IMMUNE_WEARABLES);
				return flag && super.canRoast();
			}
		}
	}

}