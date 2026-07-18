package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.FoodUtils.State;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.events.SurviveEvents;
import com.stereowalker.survive.json.ConsummableJsonHolder;
import com.stereowalker.survive.needs.CustomFoodData;
import com.stereowalker.survive.needs.HygieneData;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.needs.NutritionData;
import com.stereowalker.survive.needs.SleepData;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.survive.needs.TemperatureData;
import com.stereowalker.survive.needs.WaterData;
import com.stereowalker.survive.needs.WellbeingData;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IRealisticEntity {
	@Shadow protected FoodData foodData;
	@Shadow private int sleepCounter;
	private TemperatureData temperatureData = new TemperatureData();
	private WellbeingData wellbeingData = new WellbeingData();
	private NutritionData nutritionData = new NutritionData();
	private HygieneData hygieneData = new HygieneData();
	private StaminaData staminaData = new StaminaData(getAttributeValue(SAttributes.MAX_STAMINA.holder()));
	private SleepData sleepData = new SleepData();
	private WaterData waterData = new WaterData();

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level worldIn) {
		super(type, worldIn);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void initInject(CallbackInfo ci) {
		this.foodData = new CustomFoodData(this.foodData);
	}
	
	@Inject(method = "eat", at = @At("HEAD"))
	public void eatInject(Level pLevel, ItemStack pFood, CallbackInfoReturnable<ItemStack> cir) {
		SurviveEvents.eat(this, pFood);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/entity/player/Player;updateIsUnderwater()Z"))
	public void tickInject(CallbackInfo ci) {
		SurviveEntityStats.addStatsOnSpawn((Player)(Object)this);
		//
		if (!this.level().isClientSide && (Player)(Object)this instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer)(Object)this;
			if (Survive.THIRST_CONFIG.enabled) {
				if (player.level().getDifficulty() == Difficulty.PEACEFUL && player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
					if (waterData().needWater() && player.tickCount % 10 == 0) {
						waterData().setWaterLevel(waterData().getWaterLevel() + 1);
					}
				}
			}
		}
		//
		if (!this.level().isClientSide) {
			staminaData().baseTick((Player)(Object)this);
			hygieneData().baseTick((Player)(Object)this);
			this.nutritionData.baseTick((Player)(Object)this);
			temperatureData().baseTick((Player)(Object)this);
			waterData().baseTick((Player)(Object)this);
			this.wellbeingData.baseTick((Player)(Object)this);
			sleepData().baseTick((Player)(Object)this);
		}
	}
	
	@Redirect(method = "canEat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;needsFood()Z"))
	public boolean makeEdible(FoodData foodData) {
		if (foodData instanceof CustomFoodData) {
			return ((CustomFoodData)foodData).canConsumeFood();
		} else {
			return foodData.needsFood();
		}
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"), method = {"jumpFromGround"})
	public void morphExhaustionDuringJump(Player player, float value) {
		bypassFoodExhaustion(value, value*2.5f, Mth.ceil(value*2.5f), "Jumped", this.isSprinting());
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"), method = {"actuallyHurt"})
	public void morphExhaustion(Player player, float value) {
		bypassFoodExhaustion(value, value*2.5f, Mth.ceil(value*2.5f), "Got hurt", false);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"), method = "attack")
	public void morphStaminaDuringAttack(Player player, float value) {
		bypassFoodExhaustion(value, 1.25f, Mth.ceil(value*2.5f), "Player Attacked", true);
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"), method = {"checkMovementStatistics"})
	public void morphExhaustionMovement(Player player, float value) {
		bypassFoodExhaustion(value, value*2.5f, Mth.ceil(value*2.5f), "Movement", player.isSprinting() || player.isSwimming());
	}

	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V"))
//	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/item/ItemStack;)V"))
	public void addNutrients(Level arg0, ItemStack p_213357_2_, CallbackInfoReturnable<ItemStack> cir) {
		SurviveEvents.eatNutrition(this, p_213357_2_);
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	public void readAdditionalSaveData_inject(CompoundTag pCompound, CallbackInfo ci) {
		if (pCompound.contains("surviveData", 10)) {
			CompoundTag surviveData = pCompound.getCompound("surviveData");
			if (surviveData.contains("temperature", 10)) this.temperatureData.read(surviveData.getCompound("temperature"));
			if (surviveData.contains("wellbeing", 10)) this.wellbeingData.read(surviveData.getCompound("wellbeing"));
			if (surviveData.contains("nutrition", 10)) this.nutritionData.read(surviveData.getCompound("nutrition"));
			if (surviveData.contains("hygiene", 10)) this.hygieneData.read(surviveData.getCompound("hygiene"));
			if (surviveData.contains("stamina", 10)) this.staminaData.read(surviveData.getCompound("stamina"));
			if (surviveData.contains("sleep", 10)) this.sleepData.read(surviveData.getCompound("sleep"));
			if (surviveData.contains("water", 10)) this.waterData.read(surviveData.getCompound("water"));
		}
	}
	
	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	public void addAdditionalSaveData_inject(CompoundTag pCompound, CallbackInfo ci) {
		CompoundTag surviveData = new CompoundTag();
		surviveData.put("temperature", this.temperatureData.write(false));
		surviveData.put("wellbeing", this.wellbeingData.write(false));
		surviveData.put("nutrition", this.nutritionData.write(false));
		surviveData.put("hygiene", this.hygieneData.write(false));
		surviveData.put("stamina", this.staminaData.write(false));
		surviveData.put("sleep", this.sleepData.write(false));
		surviveData.put("water", this.waterData.write(false));
		pCompound.put("surviveData", surviveData);
	}

	public StaminaData staminaData() {
		return this.staminaData;
	}
	
	@Override
	public void setStaminaData(StaminaData data) {
		this.staminaData = data;
	}

	public HygieneData hygieneData(){
		return this.hygieneData;
	}
	
	@Override
	public void setHygieneData(HygieneData data) {
		this.hygieneData = data;
	}

	public NutritionData nutritionData(){
		return this.nutritionData;
	}
	
	@Override
	public void setNutritionData(NutritionData data) {
		this.nutritionData = data;
	}

	public TemperatureData temperatureData(){
		return temperatureData;
	}
	
	@Override
	public void setTemperatureData(TemperatureData data) {
		this.temperatureData = data;
	}

	@Override
	public WaterData waterData(){
		return waterData;
	}
	
	@Override
	public void setWaterData(WaterData data) {
		this.waterData = data;
	}

	@Override
	public WellbeingData wellbeingData(){
		return this.wellbeingData;
	}
	
	@Override
	public void setWellbeingData(WellbeingData data) {
		this.wellbeingData = data;
	}

	@Override
	public SleepData sleepData(){
		return this.sleepData;
	}
	
	@Override
	public void setSleepData(SleepData data) {
		this.sleepData = data;
	}

	public CustomFoodData getRealFoodData(){
		if (foodData instanceof CustomFoodData)
			return (CustomFoodData) foodData;
		else return null;
	}

}
