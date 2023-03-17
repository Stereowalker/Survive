package com.stereowalker.survive.needs;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IRealisticEntity {
	public StaminaData getStaminaData();
	public HygieneData getHygieneData();
	public NutritionData getNutritionData();
	public TemperatureData getTemperatureData();
	public WaterData getWaterData();
	public WellbeingData getWellbeingData();
	public SleepData getSleepData();
	public CustomFoodData getRealFoodData();
	
	private LivingEntity self() {
		return (LivingEntity)this;
	}
	
	public default ItemStack drink(Level pLevel, ItemStack pFood) {
	      this.getWaterData().drink(pFood.getItem(), pFood, self());
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
