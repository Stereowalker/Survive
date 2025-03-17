package com.stereowalker.survive.needs;

import com.stereowalker.survive.Survive;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IRealisticEntity {
	public StaminaData staminaData();
	public void setStaminaData(StaminaData data);
	public HygieneData hygieneData();
	public void setHygieneData(HygieneData data);
	public NutritionData nutritionData();
	public void setNutritionData(NutritionData data);
	public TemperatureData temperatureData();
	public void setTemperatureData(TemperatureData data);
	public WaterData waterData();
	public void setWaterData(WaterData data);
	public WellbeingData wellbeingData();
	public void setWellbeingData(WellbeingData data);
	public SleepData sleepData();
	public void setSleepData(SleepData data);
	public CustomFoodData getRealFoodData();
	
	private LivingEntity self() {
		return (LivingEntity)this;
	}

	/**
	 * increases exhaustion level by supplied amount
	 */
	public default void addStaminaExhaustion(float exhaustion, String reason, boolean causeStrain) {
		if ((self() instanceof Player player && !player.getAbilities().invulnerable) || !(self() instanceof Player)) {
			if (!self().level().isClientSide) {
				staminaData().addExhaustion(exhaustion, causeStrain);
			}

		}
	}
	
	public default void bypassFoodExhaustion(float food, float stamina, int nutrition, String reason, boolean causeStrain) {
		if (self() instanceof Player player){
			if (Survive.STAMINA_CONFIG.enabled) {
				addStaminaExhaustion(stamina, reason, causeStrain);
			}
			else if (Survive.CONFIG.nutrition_enabled) {
				this.nutritionData().removeCarbs(nutrition*10);
			}
			else {
				player.causeFoodExhaustion(food);
			}
		}
	}
	
	public default ItemStack drink(Level pLevel, ItemStack pFood) {
	      this.waterData().drink(pFood.getItem(), pFood, self());
	      return pFood;
//	      this.awardStat(Stats.ITEM_USED.get(pFood.getItem()));
//	      pLevel.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, pLevel.random.nextFloat() * 0.1F + 0.9F);
//	      if (this instanceof ServerPlayer) {
//	         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)this, pFood);
//	      }
//
//	      return super.eat(pLevel, pFood);
	   }
}
