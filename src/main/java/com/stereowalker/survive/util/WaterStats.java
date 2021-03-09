package com.stereowalker.survive.util;

import java.util.List;
import java.util.Random;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.compat.PamsHarvestcraftCompat;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.network.client.CThirstMovementPacket;
import com.stereowalker.survive.potion.SEffects;
import com.stereowalker.survive.util.data.ConsummableData;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber
public class WaterStats {
	private int waterLevel = 20;
	private float waterHydrationLevel;
	private float waterExhaustionLevel;
	private int waterTimer;
	@SuppressWarnings("unused")
	private int prevWaterLevel = 20;

	public WaterStats() {
		this.waterHydrationLevel = 5.0F;
	}

	/**
	 * Add water stats.
	 */
	public void addStats(int waterLevelIn, float waterHydrationModifier) {
		this.waterLevel = Math.min(waterLevelIn + this.waterLevel, 20);
		this.waterHydrationLevel = Math.min(this.waterHydrationLevel + (float)waterLevelIn * waterHydrationModifier * 2.0F, (float)this.waterLevel);
	}

	//TODO: Change This
	public void consume(Item maybeFood, ItemStack stack) {
		if (maybeFood.isFood()) {
			Food food = maybeFood.getFood();
			this.addStats(food.getHealing(), food.getSaturation());
		}

	}

	/**
	 * Handles the water game logic.
	 */
	public void tick(PlayerEntity player) {
		Difficulty difficulty = player.world.getDifficulty();
		this.prevWaterLevel = this.waterLevel;
		if (this.waterExhaustionLevel > 4.0F) {
			this.waterExhaustionLevel -= 4.0F;
			if (this.waterHydrationLevel > 0.0F) {
				this.waterHydrationLevel = Math.max(this.waterHydrationLevel - 1.0F, 0.0F);
			} else if (difficulty != Difficulty.PEACEFUL) {
				this.waterLevel = Math.max(this.waterLevel - 1, 0);
			}
		}

		boolean flag = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		if (flag && this.waterHydrationLevel > 0.0F && player.shouldHeal() && this.waterLevel >= 20) {
			++this.waterTimer;
			if (this.waterTimer >= 10) {
				float f = Math.min(this.waterHydrationLevel, 6.0F);
				player.heal(f / 12.0F);
				this.addExhaustion(f);
				this.waterTimer = 0;
			}
		} else if (flag && this.waterLevel >= 18 && player.shouldHeal()) {
			++this.waterTimer;
			if (this.waterTimer >= 80) {
				player.heal(0.5F);
				this.addExhaustion(6.0F);
				this.waterTimer = 0;
			}
		} else if (this.waterLevel <= 0) {
			++this.waterTimer;
			if (this.waterTimer >= 80) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.attackEntityFrom(SDamageSource.DEHYDRATE, 1.0F);
				}

				this.waterTimer = 0;
			}
		} else {
			this.waterTimer = 0;
		}

	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundNBT compound) {
		if (compound.contains("waterLevel", 99)) {
			this.waterLevel = compound.getInt("waterLevel");
			this.waterTimer = compound.getInt("waterTickTimer");
			this.waterHydrationLevel = compound.getFloat("waterHydrationLevel");
			this.waterExhaustionLevel = compound.getFloat("waterExhaustionLevel");
		}

	}

	/**
	 * Writes the water data for the player.
	 */
	public void write(CompoundNBT compound) {
		compound.putInt("waterLevel", this.waterLevel);
		compound.putInt("waterTickTimer", this.waterTimer);
		compound.putFloat("waterHydrationLevel", this.waterHydrationLevel);
		compound.putFloat("waterExhaustionLevel", this.waterExhaustionLevel);
	}

	/**
	 * Get the player's water level.
	 */
	public int getWaterLevel() {
		return this.waterLevel;
	}

	/**
	 * Get whether the player must drink water.
	 */
	public boolean needWater() {
		return this.waterLevel < 20;
	}

	/**
	 * adds input to waterExhaustionLevel to a max of 40
	 */
	private void addExhaustion(float exhaustion) {
		this.waterExhaustionLevel = Math.min(this.waterExhaustionLevel + exhaustion, 40.0F);
	}

	/**
	 * increases exhaustion level by supplied amount
	 */
	public void addExhaustion(PlayerEntity player, float exhaustion) {
		if (!player.abilities.disableDamage) {
			if (!player.world.isRemote) {
				this.addExhaustion(exhaustion);
			}

		}
	}

	/**
	 * Get the player's water hydration level.
	 */
	public float getHydrationLevel() {
		return this.waterHydrationLevel;
	}

	public void setWaterLevel(int waterLevelIn) {
		this.waterLevel = waterLevelIn;
	}

	@OnlyIn(Dist.CLIENT)
	public void setWaterHydrationLevel(float waterHydrationLevelIn) {
		this.waterHydrationLevel = waterHydrationLevelIn;
	}

	/////-----------EVENTS-----------/////

	@SubscribeEvent
	public static void regulateThirst(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
			if (Config.enable_thirst) {
				WaterStats stats = SurviveEntityStats.getWaterStats(player);
				if (Config.idle_thirst_tick_rate > -1) {
					if (player.ticksExisted%Config.idle_thirst_tick_rate == Config.idle_thirst_tick_rate-1) {
						stats.addExhaustion(player, Config.idle_thirst_exhaustion);
					}
				}
				if (player.world.getDifficulty() == Difficulty.PEACEFUL && player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
					if (stats.needWater() && player.ticksExisted % 10 == 0) {
						stats.setWaterLevel(stats.getWaterLevel() + 1);
					}
				}
				stats.tick(player);
				SurviveEntityStats.setWaterStats(player, stats);
			}
		}
		if (event.getEntityLiving() != null && event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ClientPlayerEntity) {
			ClientPlayerEntity player = (ClientPlayerEntity)event.getEntityLiving();
			if (player.ticksExisted%290 == 288) {
				if (player.world.getDifficulty() != Difficulty.PEACEFUL) {
					Survive.getInstance().CHANNEL.sendTo(new CThirstMovementPacket(player.movementInput.moveForward, player.movementInput.moveStrafe, player.movementInput.jump, player.getUniqueID()), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
				}
			}
		}
	}
	
	public static float getHydrationFromList(ItemStack stack, List<String> list) {
		for (String containerList : list) {
			String[] container = containerList.split(",");
			if (RegistryHelper.matchesRegisteredEntry(container[0], stack.getItem())) {
				return Float.parseFloat(container[2]);
			}
		}
		return 0;
	}
	
	
	public static int getThirstFill(ItemStack stack) {
		int amount = 0;
			
			if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) == Potions.WATER)
				amount+=5;
			if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) != Potions.WATER && PotionUtils.getPotionFromItem(stack) != Potions.EMPTY)
				amount+=4;
		return amount;
	}
	
	public static float getHydrationFill(ItemStack stack) {
		float amount = 0.0f;
			amount+=getHydrationFromList(stack, PamsHarvestcraftCompat.normalPamHCDrinks());
			amount+=getHydrationFromList(stack, PamsHarvestcraftCompat.uncleanPamHCDrinks());
			amount+=getHydrationFromList(stack, PamsHarvestcraftCompat.chilledPamHCDrinks());
			amount+=getHydrationFromList(stack, PamsHarvestcraftCompat.heatedPamHCDrinks());
			amount+=getHydrationFromList(stack, PamsHarvestcraftCompat.stimulatingPamHCDrinks());
			
			if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) == Potions.WATER) {
				amount+=2.0F;}
			if (stack.getItem() == Items.POTION && PotionUtils.getPotionFromItem(stack) != Potions.WATER && PotionUtils.getPotionFromItem(stack) != Potions.EMPTY)
				amount+=1.0F;
		return amount;
	}

	@SubscribeEvent
	public static void drinkWaterFromBottle(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntityLiving() != null && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
			WaterStats stats = SurviveEntityStats.getWaterStats(player);
			
			if (event.getItem().getItem() == Items.POTION && Survive.potionDrinkMap.containsKey(PotionUtils.getPotionFromItem(event.getItem()).getRegistryName())) {
				ConsummableData drinkData = Survive.potionDrinkMap.get(PotionUtils.getPotionFromItem(event.getItem()).getRegistryName());
				stats.addStats(drinkData.getThirstAmount(), drinkData.getHydrationAmount());
				applyThirst(event.getEntityLiving(), drinkData.getThirstChance());
				if (drinkData.isHeated())event.getEntityLiving().addPotionEffect(new EffectInstance(SEffects.HEATED, 30*20));
				if (drinkData.isChilled())event.getEntityLiving().addPotionEffect(new EffectInstance(SEffects.CHILLED, 30*20));
				if (drinkData.isEnergizing())event.getEntityLiving().addPotionEffect(new EffectInstance(SEffects.ENERGIZED, 60*20*5));
			}
			else if (Survive.consummableItemMap.containsKey(event.getItem().getItem().getRegistryName())) {
				ConsummableData drinkData = Survive.consummableItemMap.get(event.getItem().getItem().getRegistryName());
				stats.addStats(drinkData.getThirstAmount(), drinkData.getHydrationAmount());
				applyThirst(event.getEntityLiving(), drinkData.getThirstChance());
				if (drinkData.isHeated())event.getEntityLiving().addPotionEffect(new EffectInstance(SEffects.HEATED, 30*20));
				if (drinkData.isChilled())event.getEntityLiving().addPotionEffect(new EffectInstance(SEffects.CHILLED, 30*20));
				if (drinkData.isEnergizing())event.getEntityLiving().addPotionEffect(new EffectInstance(SEffects.ENERGIZED, 60*20*5));
			}
			
			
			SurviveEntityStats.setWaterStats(player, stats);
		}
	}

	public static void applyThirst(LivingEntity entity, float probabiltiy) {
		if (probabiltiy > 0) {
			Random rand = new Random();
			if (rand.nextFloat() < probabiltiy) {
				entity.addPotionEffect(new EffectInstance(SEffects.THIRST, 30*20));
			}
		}
	}
}
