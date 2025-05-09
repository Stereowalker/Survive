package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Cow.class)
public abstract class CowMixin extends Animal {

	protected CowMixin(EntityType<? extends Animal> p_27557_, Level p_27558_) {
		super(p_27557_, p_27558_);
	}
	
	@Inject(method = "registerGoals", at = @At("HEAD"))
	public void reGOal(CallbackInfo ci) {
		this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, (entity) -> {
			return ((IRealisticEntity)entity).hygieneData().shouldBeAvoidedByPigs() && Survive.HYGIENE_CONFIG.enabled;
		}, 6.0F, 1.0D, 1.2D, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
	}
}
