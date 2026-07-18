package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
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
import com.stereowalker.unionlib.util.EntityHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
	
	@Inject(method = "tick", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/entity/player/Player;updateIsUnderwater()Z"))
	public void tickInject(CallbackInfo ci) {
		SurviveEntityStats.addStatsOnSpawn((Player)(Object)this);
		//
		if (!this.level().isClientSide() && (Player)(Object)this instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer)(Object)this;
			if (Survive.THIRST_CONFIG.enabled) {
				if (player.level().getDifficulty() == Difficulty.PEACEFUL && player.level().getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION)) {
					if (waterData().needWater() && player.tickCount % 10 == 0) {
						waterData().setWaterLevel(waterData().getWaterLevel() + 1);
					}
				}
			}
		}
		//
		if (!this.level().isClientSide()) {
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

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"), method = {"actuallyHurt"})
	public void morphExhaustion(Player player, float value) {
		bypassFoodExhaustion(value, value*2.5f, Mth.ceil(value*2.5f), "Got hurt", false);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"), method = "attack")
	public void morphStaminaDuringAttack(Player player, float value) {
		bypassFoodExhaustion(value, 1.25f, Mth.ceil(value*2.5f), "Player Attacked", true);
	}

	@Inject(method = "hasEnoughFoodToDoExhaustiveManoeuvres", at = @At(value = "HEAD"), cancellable = true)
	public void tickInject(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.isPassenger() || !staminaData().isShortOfBreath() || EntityHelper.mayFly((Player)(Object)this));
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	public void readAdditionalSaveData_inject(ValueInput pCompound, CallbackInfo ci) {
//		if (pCompound.contains("surviveData", 10)) {
			ValueInput surviveData = pCompound.childOrEmpty("surviveData");
			if (surviveData.child("temperature").isPresent()) this.temperatureData.read(surviveData.childOrEmpty("temperature"));
			if (surviveData.child("wellbeing").isPresent()) this.wellbeingData.read(surviveData.childOrEmpty("wellbeing"));
			if (surviveData.child("nutrition").isPresent()) this.nutritionData.read(surviveData.childOrEmpty("nutrition"));
			if (surviveData.child("hygiene").isPresent()) this.hygieneData.read(surviveData.childOrEmpty("hygiene"));
			if (surviveData.child("stamina").isPresent()) this.staminaData.read(surviveData.childOrEmpty("stamina"));
			if (surviveData.child("sleep").isPresent()) this.sleepData.read(surviveData.childOrEmpty("sleep"));
			if (surviveData.child("water").isPresent()) this.waterData.read(surviveData.childOrEmpty("water"));
//		}
	}
	
	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	public void addAdditionalSaveData_inject(ValueOutput pCompound, CallbackInfo ci) {
		CompoundTag surviveData = new CompoundTag();
		surviveData.put("temperature", this.temperatureData.write(false));
		surviveData.put("wellbeing", this.wellbeingData.write(false));
		surviveData.put("nutrition", this.nutritionData.write(false));
		surviveData.put("hygiene", this.hygieneData.write(false));
		surviveData.put("stamina", this.staminaData.write(false));
		surviveData.put("sleep", this.sleepData.write(false));
		surviveData.put("water", this.waterData.write(false));
		pCompound.store("surviveData", ExtraCodecs.NBT, surviveData);
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
