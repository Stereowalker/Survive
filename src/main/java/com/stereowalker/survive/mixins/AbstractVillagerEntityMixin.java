package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.INPC;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.World;

@Mixin(AbstractVillagerEntity.class)
public abstract class AbstractVillagerEntityMixin extends AgeableEntity implements INPC, IMerchant{

	protected AbstractVillagerEntityMixin(EntityType<? extends AgeableEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	protected void registerGoals() {
	      this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, PlayerEntity.class, (p_200828_0_) -> {
	          return SurviveEntityStats.getHygieneStats(p_200828_0_).needsABath() && Config.enable_Hygiene;
	      }, 6.0F, 0.9D, 1.1D, EntityPredicates.CAN_AI_TARGET::test));
	   }

}
