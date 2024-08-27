package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.survive.FoodUtils.State;
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
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IRealisticEntity {
	@Shadow protected FoodData foodData;
	@Shadow private int sleepCounter;
	private WellbeingData wellbeingData = new WellbeingData();
	private NutritionData nutritionData = new NutritionData();
	private HygieneData hygieneData = new HygieneData();
	private StaminaData staminaData = new StaminaData(getAttributeValue(SAttributes.MAX_STAMINA.holder()));
	private SleepData sleepData = new SleepData();

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level worldIn) {
		super(type, worldIn);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void initInject(CallbackInfo ci) {
		this.foodData = new CustomFoodData(this.foodData);
	}
	
	@Inject(method = "eat", at = @At("HEAD"))
	public void eatInject(Level pLevel, ItemStack pFood, FoodProperties pFoodProperties, CallbackInfoReturnable<ItemStack> cir) {
		if (pFood.has(DataComponents.FOOD) && foodData instanceof CustomFoodData custom) {
			FoodProperties foodproperties = pFood.get(DataComponents.FOOD);
			for (PossibleEffect effect : foodproperties.effects()) {
				if (effect.effect().getEffect() == MobEffects.HUNGER || custom.IsSpoiled() == State.Spoiled) {
					custom.consumeUnclean();
					break;
				}
			}
		}
		this.staminaData().eat(pFood.getItem(), pFood, this);
		this.getWaterData().drink(pFood.getItem(), pFood, this);
		this.getRealFoodData().markAsSpoiled(pFood, this);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/entity/player/Player;updateIsUnderwater()Z"))
	public void tickInject(CallbackInfo ci) {
		SurviveEntityStats.addStatsOnSpawn((Player)(Object)this);
		//
		if (!this.level().isClientSide && (Player)(Object)this instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer)(Object)this;
			if (Survive.THIRST_CONFIG.enabled) {
				if (player.level().getDifficulty() == Difficulty.PEACEFUL && player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
					if (getWaterData().needWater() && player.tickCount % 10 == 0) {
						getWaterData().setWaterLevel(getWaterData().getWaterLevel() + 1);
					}
				}
				getWaterData().save(player);
			}
		}
		//
		if (!this.level().isClientSide) {
			staminaData().baseTick((Player)(Object)this);
			hygieneData().baseTick((Player)(Object)this);
			this.nutritionData.baseTick((Player)(Object)this);
			getTemperatureData().baseTick((Player)(Object)this);
			getWaterData().baseTick((Player)(Object)this);
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

	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
	public void addNutrients(Level arg0, ItemStack p_213357_2_, FoodProperties pFoodProperties, CallbackInfoReturnable<ItemStack> cir) {
		if (Survive.CONFIG.nutrition_enabled) {
			float protein = 1;
			float carbs = 1;
			float fats = 1;
			if (DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(p_213357_2_.getItem()))) {
				ConsummableJsonHolder data = DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(p_213357_2_.getItem()));
				protein = data.getProteinRatio();
				carbs = data.getCarbohydrateRatio();
				fats = data.getFatRatio();
			}
			FoodProperties food = p_213357_2_.get(DataComponents.FOOD);
			float total = protein+carbs+fats;
			this.nutritionData.addCarbs(food.nutrition()*Mth.ceil((carbs/total)*100));
			this.nutritionData.protein().add(food.nutrition()*Mth.ceil((protein/total)*100));
			this.nutritionData.fat().add(food.nutrition()*Mth.ceil((fats/total)*100));
		}
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	public void readAdditionalSaveData_inject(CompoundTag pCompound, CallbackInfo ci) {
		if (pCompound.contains("surviveData", 10)) {
			CompoundTag surviveData = pCompound.getCompound("surviveData");
			if (surviveData.contains("wellbeing", 10)) this.wellbeingData.read(surviveData.getCompound("wellbeing"));
			if (surviveData.contains("nutrition", 10)) this.nutritionData.read(surviveData.getCompound("nutrition"));
			if (surviveData.contains("hygiene", 10)) this.hygieneData.read(surviveData.getCompound("hygiene"));
			if (surviveData.contains("stamina", 10)) this.staminaData.read(surviveData.getCompound("stamina"));
			if (surviveData.contains("sleep", 10)) this.sleepData.read(surviveData.getCompound("sleep"));
		}
	}
	
	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	public void addAdditionalSaveData_inject(CompoundTag pCompound, CallbackInfo ci) {
		CompoundTag surviveData = new CompoundTag();
		surviveData.put("wellbeing", this.wellbeingData.write());
		surviveData.put("nutrition", this.nutritionData.write());
		surviveData.put("hygiene", this.hygieneData.write());
		surviveData.put("stamina", this.staminaData.write());
		surviveData.put("sleep", this.sleepData.write());
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

	public TemperatureData getTemperatureData(){
		return SurviveEntityStats.getTemperatureStats((Player)(Object)this);
	}

	public WaterData getWaterData(){
		return SurviveEntityStats.getWaterStats((Player)(Object)this);
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
