package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin extends AgeableMob implements Npc, Merchant{

	protected AbstractVillagerMixin(EntityType<? extends AgeableMob> type, Level worldIn) {
		super(type, worldIn);
	}
	
	protected void registerGoals() {
	      this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, (entity) -> {
	          return ((IRealisticEntity)entity).hygieneData().needsABath() && Survive.HYGIENE_CONFIG.enabled;
	      }, 6.0F, 1.0D, 1.2D, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
	   }

}
