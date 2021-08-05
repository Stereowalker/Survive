package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.survive.DataMaps;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.NutritionStats;
import com.stereowalker.survive.util.StaminaStats;
import com.stereowalker.survive.util.data.ConsummableData;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity{

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), method = {"jump", "damageEntity", "addMovementStat"})
	public void morphExhaustion(PlayerEntity player, float value) {
		if (Config.enable_stamina) {
			StaminaStats energyStats = SurviveEntityStats.getEnergyStats(player);
			energyStats.addExhaustion(player, value*2.5f);
			energyStats.save(player);
		}
		else if (Config.nutrition_enabled) {
			NutritionStats nutritionStats = SurviveEntityStats.getNutritionStats(player);
			nutritionStats.removeCarbs(MathHelper.ceil(value*2.5f));
		}
		else {
			player.addExhaustion(value);
		}
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), method = "attackTargetEntityWithCurrentItem")
	public void morphStaminaDuringAttack(PlayerEntity player, float value) {
		if (Config.enable_stamina) {
			StaminaStats energyStats = SurviveEntityStats.getEnergyStats(player);
			energyStats.addExhaustion(player, 1.25f);
			energyStats.save(player);
		}
		else if (Config.nutrition_enabled) {
			NutritionStats nutritionStats = SurviveEntityStats.getNutritionStats(player);
			nutritionStats.removeCarbs(MathHelper.ceil(value*2.5f));
		}
		else {
			player.addExhaustion(value);
		}
	}
	
	@Inject(method = "onFoodEaten", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;consume(Lnet/minecraft/item/Item;Lnet/minecraft/item/ItemStack;)V"))
	public void addNutrients(World arg0, ItemStack p_213357_2_, CallbackInfoReturnable<ItemStack> cir) {
		if (Config.nutrition_enabled) {
			NutritionStats nutritionStats = SurviveEntityStats.getNutritionStats((LivingEntity)(Object)this);
			float protein = 1;
			float carbs = 1;
			if (DataMaps.Server.consummableItem.containsKey(p_213357_2_.getItem().getRegistryName())) {
				ConsummableData data = DataMaps.Server.consummableItem.get(p_213357_2_.getItem().getRegistryName());
				protein = data.getProteinRatio();
				carbs = data.getCarbohydrateRatio();
			}
			Food food = p_213357_2_.getItem().getFood();
			float total = protein+carbs;
			nutritionStats.addCarbs(food.getHealing()*MathHelper.ceil((carbs/total)*10));
			nutritionStats.addProtein(food.getHealing()*MathHelper.ceil((protein/total)*10));
			nutritionStats.save((LivingEntity)(Object)this);
		}
	}
}
