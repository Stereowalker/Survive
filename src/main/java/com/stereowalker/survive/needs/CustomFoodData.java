package com.stereowalker.survive.needs;

import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.world.effect.SMobEffects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CustomFoodData extends FoodData {
	int uncleanConsumption = 0;
	boolean causeAche = false;
	boolean wellFed = false;

	public CustomFoodData(FoodData originalFoodData) {
		this.foodLevel = originalFoodData.foodLevel;
		this.setSaturation(originalFoodData.getSaturationLevel());
		this.setExhaustion(originalFoodData.getExhaustionLevel());
		this.tickTimer = originalFoodData.tickTimer;
	}
	/**
	 * Add food stats.
	 */
	@Override
	public void eat(int pFoodLevelModifier, float pSaturationLevelModifier) {
		int foodLevel = this.foodLevel;
		super.eat(pFoodLevelModifier, pSaturationLevelModifier);
		if (ServerConfig.expandedStomachCapacity) {
			this.foodLevel = Math.min(pFoodLevelModifier + foodLevel, ServerConfig.stomachCapacity());
			if (this.foodLevel == 20 && foodLevel < 20) {
				this.wellFed = true;
			}
			else if (this.foodLevel > 20 && foodLevel < 20 && (pFoodLevelModifier/2) < (this.foodLevel-20)) {
				this.causeAche = true;
			} else if (this.foodLevel > 20 && foodLevel >= 20){
				this.causeAche = true;
			} else {
				this.causeAche = false;
			}
		}
	}

	@Override
	public void eat(Item pItem, ItemStack pStack, LivingEntity entity) {
		super.eat(pItem, pStack, entity);
		if (pItem.isEdible()) {
			FoodProperties foodproperties = pStack.getFoodProperties(entity);
			for (Pair<MobEffectInstance, Float> effect : foodproperties.getEffects()) {
				if (effect.getFirst().getEffect() == MobEffects.HUNGER) {
					uncleanConsumption++;
					break;
				}
			}
		}
	}

	@Override
	public void tick(Player pPlayer) {
		Difficulty difficulty = pPlayer.level.getDifficulty();
		//Well fed
		if (this.wellFed) {
			if (this.foodLevel == 20) {
				pPlayer.addEffect(new MobEffectInstance(SMobEffects.WELL_FED, 300, 0));
			} else {
				this.wellFed = false;
			}
		}
		
		int amplifier = -1;
		int duration = 210;
		if (this.foodLevel > 36) amplifier = 4;
		else if (this.foodLevel > 32) amplifier = 3;
		else if (this.foodLevel > 28) amplifier = 2;
		else if (this.foodLevel > 24) amplifier = 1;
		else if (this.foodLevel > 20) amplifier = 0;
		MobEffectInstance upsetStomach = pPlayer.getEffect(SMobEffects.UPSET_STOMACH);
		if (!pPlayer.isSpectator() && !pPlayer.isCreative())
			if (amplifier > 0 && (upsetStomach == null || upsetStomach.getDuration() <= 210 || upsetStomach.getAmplifier() < amplifier))
				pPlayer.addEffect(new MobEffectInstance(SMobEffects.UPSET_STOMACH, duration, amplifier));
		
		if (Survive.WELLBEING_CONFIG.enabled) {
			//Essentially causes the player to get ill when drinking bad water
			if (uncleanConsumption >= 3) {
				((IRealisticEntity)pPlayer).getWellbeingData().setTimer(2400, 6000, "Eating bad food");
				uncleanConsumption = 0;
			}
		}

		if (this.foodLevel >= 40 && (pPlayer.getHealth() > 10.0F || difficulty == Difficulty.HARD || pPlayer.getHealth() > 1.0F && difficulty == Difficulty.NORMAL)) {
			int tickTimer2 = this.tickTimer;
			//This sorts of bypasses the food ticks because it the base game will not do anything to the player if the ticker is 0
			//If mods do actually make patches to the ticker, and they too check the tick timer, then they also will not do anything. 
			//I'm leaving this here in case anybody is curious as to why this mod is preventing their own from doing anything.
			//The reason this is implemented the way it is is to prevent the game from healing the player while the above conditions are met
			this.tickTimer = 0;
			super.tick(pPlayer);
			this.tickTimer = tickTimer2+1;
			if (this.tickTimer >= 10) {
				pPlayer.hurt(SDamageSource.OVEREAT, 1.0F);
				this.tickTimer = 0;
			}
		} else {
			super.tick(pPlayer);
		}
	}

	/**
	 * Get whether the player can eat food.
	 */
	public boolean canConsumeFood() {
		return this.needsFood() || this.getFoodLevel() < ServerConfig.stomachCapacity();
	}

	@Override
	public void readAdditionalSaveData(CompoundTag pCompoundTag) {
		super.readAdditionalSaveData(pCompoundTag);
		if (pCompoundTag.contains("foodLevel", 99)) {
			this.uncleanConsumption = pCompoundTag.getInt("foodUncleanConsumption");
			this.causeAche = pCompoundTag.getBoolean("foodCauseAche");
			this.wellFed = pCompoundTag.getBoolean("foodWellFed");
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag pCompoundTag) {
		super.addAdditionalSaveData(pCompoundTag);
		pCompoundTag.putFloat("foodUncleanConsumption", this.uncleanConsumption);
		pCompoundTag.putBoolean("foodCauseAche", this.causeAche);
		pCompoundTag.putBoolean("foodWellFed", this.wellFed);
	}
}
