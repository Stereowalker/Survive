package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.json.ConsummableJsonHolder;
import com.stereowalker.survive.needs.NutritionData;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.survive.world.DataMaps;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level worldIn) {
		super(type, worldIn);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"), method = {"jumpFromGround", "actuallyHurt", "checkMovementStatistics"})
	public void morphExhaustion(Player player, float value) {
		if (Survive.STAMINA_CONFIG.enabled) {
			StaminaData energyStats = SurviveEntityStats.getEnergyStats(player);
			energyStats.addExhaustion(player, value*2.5f, "Jumped, Got hurt or moved");
		}
		else if (Survive.CONFIG.nutrition_enabled) {
			NutritionData nutritionStats = SurviveEntityStats.getNutritionStats(player);
			nutritionStats.removeCarbs(Mth.ceil(value*2.5f));
		}
		else {
			player.causeFoodExhaustion(value);
		}
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"), method = "attack")
	public void morphStaminaDuringAttack(Player player, float value) {
		if (Survive.STAMINA_CONFIG.enabled) {
			StaminaData energyStats = SurviveEntityStats.getEnergyStats(player);
			energyStats.addExhaustion(player, 1.25f, "Player Attacked");
			energyStats.save(player);
		}
		else if (Survive.CONFIG.nutrition_enabled) {
			NutritionData nutritionStats = SurviveEntityStats.getNutritionStats(player);
			nutritionStats.removeCarbs(Mth.ceil(value*2.5f));
		}
		else {
			player.causeFoodExhaustion(value);
		}
	}
	
	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V"))
//	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V"))
	public void addNutrients(Level arg0, ItemStack p_213357_2_, CallbackInfoReturnable<ItemStack> cir) {
		if (Survive.CONFIG.nutrition_enabled) {
			NutritionData nutritionStats = SurviveEntityStats.getNutritionStats((LivingEntity)(Object)this);
			float protein = 1;
			float carbs = 1;
			if (DataMaps.Server.consummableItem.containsKey(p_213357_2_.getItem().getRegistryName())) {
				ConsummableJsonHolder data = DataMaps.Server.consummableItem.get(p_213357_2_.getItem().getRegistryName());
				protein = data.getProteinRatio();
				carbs = data.getCarbohydrateRatio();
			}
			FoodProperties food = p_213357_2_.getItem().getFoodProperties();
			float total = protein+carbs;
			nutritionStats.addCarbs(food.getNutrition()*Mth.ceil((carbs/total)*10));
			nutritionStats.addProtein(food.getNutrition()*Mth.ceil((protein/total)*10));
			nutritionStats.save((LivingEntity)(Object)this);
		}
	}
}
