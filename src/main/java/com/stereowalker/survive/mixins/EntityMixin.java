package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.stereowalker.survive.needs.IRoastedEntity;

import net.minecraft.commands.CommandSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity, IRoastedEntity {

	public EntityMixin(EntityType<?> pEntityType, Level pLevel) {
		super(Entity.class);
	}

	@Shadow @Final protected SynchedEntityData entityData;
	@Shadow public boolean isInPowderSnow;
	@Shadow public Level level;
	@Shadow public int tickCount;
	@Shadow public EntityType<?> getType() {return null;}
	@Shadow public boolean isSpectator() {return false;}
	@Shadow public boolean canFreeze() {return false;}
	@Shadow public boolean isFullyFrozen() {return false;}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void init_inject(CallbackInfo info) {
		this.entityData.define(DATA_TICKS_ROASTED, 0);
	}

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getUUID()Ljava/util/UUID;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void saveWithoutId_inject(CompoundTag pCompound, CallbackInfoReturnable<CompoundTag> cir) {
		int i = this.getTicksRoasted();
		if (i > 0) {
			pCompound.putInt("TicksRoasted", this.getTicksRoasted());
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setTicksFrozen(I)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void load_inject(CompoundTag pCompound, CallbackInfo ci) {
		this.setTicksRoasted(pCompound.getInt("TicksRoasted"));
	}

	@Override
	public int getTicksRoasted() {
		return this.entityData.get(DATA_TICKS_ROASTED);
	}

	@Override
	public void setTicksRoasted(int pTicksFrozen) {
		this.entityData.set(DATA_TICKS_ROASTED, pTicksFrozen);
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
		return !EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES.contains(this.getType());
	}

}
