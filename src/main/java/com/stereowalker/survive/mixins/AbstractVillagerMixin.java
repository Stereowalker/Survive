package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin extends AgeableMob implements Npc, Merchant{

	protected AbstractVillagerMixin(EntityType<? extends AgeableMob> type, Level worldIn) {
		super(type, worldIn);
	}
	
	protected void registerGoals() {
	      this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, (p_200828_0_) -> {
	          return SurviveEntityStats.getHygieneStats(p_200828_0_).needsABath() && Survive.CONFIG.enable_hygiene;
	      }, 6.0F, 1.0D, 1.2D, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
	   }

}
