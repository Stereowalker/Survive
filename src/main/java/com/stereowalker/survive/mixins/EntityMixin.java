package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.stereowalker.survive.needs.IRoastedEntity;
import com.stereowalker.survive.world.entity.EntityData;
import com.stereowalker.unionlib.network.syncher.IRevisedDataEntity;

import net.minecraft.commands.CommandSource;
import net.minecraft.core.TypedInstance;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityAccess, CommandSource, IRoastedEntity, TypedInstance<EntityType<?>> {

	@Shadow public boolean isInPowderSnow;
	@Shadow public int tickCount;
	@Shadow public Level level() {return null;}
	@Shadow public EntityType<?> getType() {return null;}
	@Shadow public boolean isSpectator() {return false;}
	@Shadow public boolean canFreeze() {return false;}
	@Shadow public boolean isFullyFrozen() {return false;}
	@Shadow public DamageSources damageSources() {return null;}
	
	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getUUID()Ljava/util/UUID;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void saveWithoutId_inject(ValueOutput pCompound, CallbackInfo ci) {
		int i = this.getTicksRoasted();
		if (i > 0) {
			pCompound.putInt("TicksRoasted", this.getTicksRoasted());
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setTicksFrozen(I)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void load_inject(ValueInput pCompound, CallbackInfo ci) {
		this.setTicksRoasted(pCompound.getIntOr("TicksRoasted", 0));
	}

	@Override
	public int getTicksRoasted() {
		IRevisedDataEntity rde = (IRevisedDataEntity)this;
		return rde.getRevisedEntityData().get(EntityData.DATA_TICKS_ROASTED);
	}

	@Override
	public void setTicksRoasted(int pTicksFrozen) {
		IRevisedDataEntity rde = (IRevisedDataEntity)this;
		rde.getRevisedEntityData().set(EntityData.DATA_TICKS_ROASTED, pTicksFrozen);
	}

	@Override
	public float getPercentRoasted() {
		int i = this.getTicksRequiredToRoast();
		return (float)Math.min(this.getTicksRoasted(), i) / (float)i;
	}

	@Override
	public boolean isFullyRoasted() {
		return this.getTicksRoasted() >= this.getTicksRequiredToRoast();
	}
	
	@Override
	public boolean canRoast() {
		return !this.is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES);
	}

}
