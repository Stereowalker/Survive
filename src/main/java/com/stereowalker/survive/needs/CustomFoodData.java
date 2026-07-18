package com.stereowalker.survive.needs;

import com.stereowalker.survive.FoodUtils;
import com.stereowalker.survive.FoodUtils.State;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.damagesource.SDamageSources;
import com.stereowalker.survive.damagesource.SDamageTypes;
import com.stereowalker.survive.world.effect.SMobEffects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CustomFoodData extends FoodData {
	int uncleanConsumption = 0;
	boolean causeAche = false;
	boolean wellFed = false;
	//Do not serialize
	FoodUtils.State isSpoiled = State.Okay;

	public CustomFoodData(FoodData originalFoodData) {
		this.foodLevel = originalFoodData.foodLevel;
		this.setSaturation(originalFoodData.getSaturationLevel());
		this.exhaustionLevel = originalFoodData.exhaustionLevel;
		this.tickTimer = originalFoodData.tickTimer;
	}
	/**
	 * Add food stats.
	 */
	@Override
	public void eat(int pFoodLevelModifier, float pSaturationLevelModifier) {
		int foodLevel = this.foodLevel;
		float satMod = 1.0f;
		
		if (IsSpoiled() == State.Spoiled) pFoodLevelModifier = pFoodLevelModifier/2;
		
		if (IsSpoiled() == State.Fresh) satMod = 1.5f;
		else if (IsSpoiled() == State.Good) satMod = 1.1f;
		else if (IsSpoiled() == State.Spoiling) satMod = 0.9f;
		else if (IsSpoiled() == State.Spoiled) satMod = 0.5f;
		super.eat(pFoodLevelModifier, pSaturationLevelModifier * satMod);
		int capacity = 20;
		if (ServerConfig.stomachCapacity == StomachCapacity.DOUBLED) capacity = 40;
		else if (ServerConfig.stomachCapacity == StomachCapacity.LIMITED && this.foodLevel < 20) capacity = 40;
		else if (ServerConfig.stomachCapacity == StomachCapacity.LIMITED && this.foodLevel >= 20) capacity = this.foodLevel;
		if (ServerConfig.stomachCapacity != StomachCapacity.VANILLA) {
			this.foodLevel = Math.min(pFoodLevelModifier + foodLevel, capacity);
			if (this.foodLevel == 20 && foodLevel < 20) this.wellFed = true;
			else if (this.foodLevel > 20 && foodLevel < 20 && (pFoodLevelModifier/2) < (this.foodLevel-20)) {
				this.causeAche = true;
			} else if (this.foodLevel > 20 && foodLevel >= 20) this.causeAche = true; 
			else this.causeAche = false;
		}
	}
	
	public void consumeUnclean() {
		uncleanConsumption++;
	}

//	@Override
//	public void eat(Item pItem, ItemStack pStack, LivingEntity entity) {
//		super.eat(pItem, pStack, entity);
//		if (pItem.isEdible()) {
//			FoodProperties foodproperties = pStack.getFoodProperties(entity);
//			for (Pair<MobEffectInstance, Float> effect : foodproperties.getEffects()) {
//				if (effect.getFirst().getEffect() == MobEffects.HUNGER || IsSpoiled() == State.Spoiled) {
//					uncleanConsumption++;
//					break;
//				}
//			}
//		}
//	}
	
	public void markAsSpoiled(ItemStack stack, LivingEntity living) {
		isSpoiled = FoodUtils.foodStatus(stack, living.level());
		if (IsSpoiled() == State.Spoiled) {
			living.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 1200));
			living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1200));
		}
	}

	@Override
	public void tick(ServerPlayer pPlayer) {
		Difficulty difficulty = pPlayer.level().getDifficulty();
		//Well fed
		if (this.wellFed) {
			if (this.foodLevel == 20) {
				pPlayer.addEffect(new MobEffectInstance(SMobEffects.WELL_FED.holder(), 300, 0));
			} else this.wellFed = false;
		}
		
		if (ServerConfig.stomachCapacity != StomachCapacity.VANILLA) {
			int amplifier = -1;
			int duration = 210;
			if (this.foodLevel > 36) amplifier = 4;
			else if (this.foodLevel > 32) amplifier = 3;
			else if (this.foodLevel > 28) amplifier = 2;
			else if (this.foodLevel > 24) amplifier = 1;
			else if (this.foodLevel > 20) amplifier = 0;
			MobEffectInstance upsetStomach = pPlayer.getEffect(SMobEffects.UPSET_STOMACH.holder());
			if (!pPlayer.isSpectator() && !pPlayer.isCreative())
				if (amplifier > 0 && (upsetStomach == null || upsetStomach.getDuration() <= 210 || upsetStomach.getAmplifier() < amplifier))
					pPlayer.addEffect(new MobEffectInstance(SMobEffects.UPSET_STOMACH.holder(), duration, amplifier));
		}
		
		if (Survive.WELLBEING_CONFIG.enabled) {
			//Essentially causes the player to get ill when drinking bad water
			if (uncleanConsumption >= 3) {
				((IRealisticEntity)pPlayer).wellbeingData().setTimer(2400, 6000, "eating bad food");
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
				pPlayer.hurt(SDamageSources.source(pPlayer.level().registryAccess(), SDamageTypes.OVEREAT), 1.0F);
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
		switch (ServerConfig.stomachCapacity) {
			case VANILLA:
				return this.needsFood();
			case LIMITED:
				return this.needsFood();
			case DOUBLED:
				return this.foodLevel < 40;
			default:
				return this.needsFood();
		}
	}
	
	@Override
	public void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
//		if (pCompoundTag.contains("foodLevel", 99)) {
//		}
		this.uncleanConsumption = input.getIntOr("foodUncleanConsumption", 0);
		this.causeAche = input.getBooleanOr("foodCauseAche", false);
		this.wellFed = input.getBooleanOr("foodWellFed", false);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput pCompoundTag) {
		super.addAdditionalSaveData(pCompoundTag);
		pCompoundTag.putFloat("foodUncleanConsumption", this.uncleanConsumption);
		pCompoundTag.putBoolean("foodCauseAche", this.causeAche);
		pCompoundTag.putBoolean("foodWellFed", this.wellFed);
	}
	
	public FoodUtils.State IsSpoiled() {
		return isSpoiled;
	}

	public enum StomachCapacity {DOUBLED, LIMITED, VANILLA}
}
