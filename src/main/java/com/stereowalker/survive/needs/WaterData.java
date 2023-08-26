package com.stereowalker.survive.needs;

import java.util.Random;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.ServerConfig;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.damagesource.SDamageSources;
import com.stereowalker.survive.damagesource.SDamageTypes;
import com.stereowalker.survive.json.BiomeJsonHolder;
import com.stereowalker.survive.json.ConsummableJsonHolder;
import com.stereowalker.survive.needs.CustomFoodData.StomachCapacity;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WaterData extends SurviveData {
	private int tempDropTicks = 0;
	private int waterLevel = 20;
	private float waterHydrationLevel;
	private float waterExhaustionLevel;
	private int waterTimer;
	@SuppressWarnings("unused")
	private int prevWaterLevel = 20;
	private int uncleanConsumption = 0;
	private int uncleanStacks = 0;

	public WaterData() {
		this.waterHydrationLevel = 4.0F;
	}

	/**
	 * Add water stats.
	 */
	public void drink(int waterLevelIn, float waterHydrationModifier, int uncleanStack, boolean isUnclean) {
		int capacity = 20;
		if (ServerConfig.stomachCapacity == StomachCapacity.DOUBLED) capacity = 40;
		else if (ServerConfig.stomachCapacity == StomachCapacity.LIMITED && this.waterLevel < 20) capacity = 40;
		else if (ServerConfig.stomachCapacity == StomachCapacity.LIMITED && this.waterLevel >= 20) capacity = this.waterLevel;
		this.waterLevel = Math.min(waterLevelIn + this.waterLevel, capacity);
		if (this.waterHydrationLevel >= waterHydrationModifier) {
			this.waterHydrationLevel = waterHydrationModifier;
		} else if (this.waterHydrationLevel < waterHydrationModifier) {
			this.waterHydrationLevel = Mth.clamp(this.waterHydrationLevel + (waterHydrationModifier * Survive.THIRST_CONFIG.hydration_restoration), 1.0f, waterHydrationModifier);
		}
		if (isUnclean) {
			uncleanStacks += uncleanStack;
			uncleanConsumption++;
		}
	}

	public void drink(Item pItem, ItemStack pStack, LivingEntity entity) {
		if (entity != null && entity instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer)entity;
			Potion potion = PotionUtils.getPotion(pStack);
			float biomef = -1;
			int stacks = 0;
			if (pStack.getTag() != null && pStack.getTag().contains("biome_source")) {
				ResourceLocation biomeSource = new ResourceLocation(pStack.getTag().getString("biome_source"));
				if (DataMaps.Server.biome.containsKey(biomeSource)) {
					BiomeJsonHolder biomeData = DataMaps.Server.biome.get(biomeSource);
					biomef = biomeData.getThirstChance();
					stacks = biomeData.getUnwellIntensity();
				}	
			}
			if (potion != Potions.EMPTY && DataMaps.Server.potionDrink.containsKey(RegistryHelper.potions().getKey(potion))) {
				ConsummableJsonHolder drinkData = DataMaps.Server.potionDrink.get(RegistryHelper.potions().getKey(potion));
				drink(drinkData.getThirstAmount(), drinkData.getHydrationAmount(), stacks, applyThirst(entity, biomef != -1 ? biomef : drinkData.getThirstChance()));
				if (drinkData.isHeated())entity.addEffect(new MobEffectInstance(SMobEffects.HEATED, 30*20));
				if (drinkData.isChilled())entity.addEffect(new MobEffectInstance(SMobEffects.CHILLED, 30*20));
				if (drinkData.isEnergizing())entity.addEffect(new MobEffectInstance(SMobEffects.ENERGIZED, 60*20*5));
			}
			else if (DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(pItem))) {
				ConsummableJsonHolder drinkData = DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(pItem));
				drink(drinkData.getThirstAmount(), drinkData.getHydrationAmount(), stacks, applyThirst(entity, biomef != -1 ? biomef : drinkData.getThirstChance()));
				if (drinkData.isHeated())entity.addEffect(new MobEffectInstance(SMobEffects.HEATED, 30*20));
				if (drinkData.isChilled())entity.addEffect(new MobEffectInstance(SMobEffects.CHILLED, 30*20));
				if (drinkData.isEnergizing())entity.addEffect(new MobEffectInstance(SMobEffects.ENERGIZED, 60*20*5));
			}

			save(player);
		}

	}

	/**
	 * Handles the water game logic.
	 */
	//TODO: Figure out something else that hydration can do apart from healing
	public void tick(Player player) {
		this.waterHydrationLevel = Mth.clamp(this.waterHydrationLevel, 1.0f, 4.0f);
		if (Survive.THIRST_CONFIG.idle_thirst_tick_rate > -1) {
			if (player.tickCount%Survive.THIRST_CONFIG.idle_thirst_tick_rate == Survive.THIRST_CONFIG.idle_thirst_tick_rate-1) {
				addExhaustion(player, Survive.THIRST_CONFIG.idle_thirst_exhaustion);
			}
		}
		
		Difficulty difficulty = player.level().getDifficulty();
		this.prevWaterLevel = this.waterLevel;
		
		if (ServerConfig.stomachCapacity != StomachCapacity.VANILLA) {
			int amplifier = -1;
			int duration = 210;
			if (this.waterLevel > 36) amplifier = 4;
			else if (this.waterLevel > 32) amplifier = 3;
			else if (this.waterLevel > 28) amplifier = 2;
			else if (this.waterLevel > 24) amplifier = 1;
			else if (this.waterLevel > 20) amplifier = 0;
			MobEffectInstance upsetStomach = player.getEffect(SMobEffects.UPSET_STOMACH);
			if (!player.isSpectator() && !player.isCreative())
				if (amplifier > 0 && (upsetStomach == null || upsetStomach.getDuration() <= 210 || upsetStomach.getAmplifier() < amplifier))
					player.addEffect(new MobEffectInstance(SMobEffects.UPSET_STOMACH, duration, amplifier));
		}
		
		if (this.waterExhaustionLevel > 4.0F) {
			this.waterExhaustionLevel -= 4.0F;
			if (this.waterHydrationLevel > 2.8F)
				this.waterHydrationLevel = Math.max(this.waterHydrationLevel - 0.1F, 2.8F);
			if (difficulty != Difficulty.PEACEFUL)
				this.waterLevel = Math.max(this.waterLevel - (5 - Mth.ceil(this.waterHydrationLevel)), 0);
		}

		boolean flag = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
		if (this.waterLevel >= 40) {
			++this.waterTimer;
			if (this.waterTimer >= 10) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.hurt(SDamageSources.source(player.level().registryAccess(), SDamageTypes.OVERHYDRATE), 1.0F);
				}

				this.waterTimer = 0;
			}
		} else if (flag && this.waterLevel >= 16 && ((IRealisticEntity)player).getTemperatureData().getTemperatureLevel() > Survive.DEFAULT_TEMP) {
			++this.waterTimer;
			if (this.waterTimer >= 80) {
				this.tempDropTicks++;
				this.addExhaustion(6.0F);
				this.waterTimer = 0;
			}
		} else if (this.waterLevel <= 0) {
			++this.waterTimer;
			if (this.waterTimer >= 80) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.hurt(SDamageSources.source(player.level().registryAccess(), SDamageTypes.DEHYDRATE), 1.0F);
				}

				this.waterTimer = 0;
			}
		} else {
			this.waterTimer = 0;
		}

		if (Survive.WELLBEING_CONFIG.enabled) {
			//Essentially causes the player to get ill when drinking bad water
			if (uncleanConsumption >= 3) {
				((IRealisticEntity)player).getWellbeingData().setTimer(2400, 6000, Mth.ceil((float)uncleanStacks/(float)uncleanConsumption), "drinking unpurified water");
				uncleanConsumption = 0;
			}
		}

	}
	
	public void applyTempDrop(Player player) {
		this.tempDropTicks--;
		save(player);
	}
	

	public boolean shouldTempDrop() {
		return this.tempDropTicks > 0;
	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundTag compound) {
		if (compound.contains("waterLevel", 99)) {
			this.tempDropTicks = compound.getInt("tempDropTicks");
			this.waterLevel = compound.getInt("waterLevel");
			this.waterTimer = compound.getInt("waterTickTimer");
			this.waterHydrationLevel = compound.getFloat("waterHydrationLevel");
			this.waterExhaustionLevel = compound.getFloat("waterExhaustionLevel");
			this.uncleanConsumption = compound.getInt("uncleanComsumption");
			this.uncleanStacks = compound.getInt("uncleanStacks");
		}

	}

	/**
	 * Writes the water data for the player.
	 */
	public void write(CompoundTag compound) {
		compound.putInt("tempDropTicks", this.tempDropTicks);
		compound.putInt("waterLevel", this.waterLevel);
		compound.putInt("waterTickTimer", this.waterTimer);
		compound.putFloat("waterHydrationLevel", this.waterHydrationLevel);
		compound.putFloat("waterExhaustionLevel", this.waterExhaustionLevel);
		compound.putInt("uncleanComsumption", this.uncleanConsumption);
		compound.putInt("uncleanStacks", this.uncleanStacks);
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
		switch (ServerConfig.stomachCapacity) {
			case VANILLA:
				return this.waterLevel < 20;
			case LIMITED:
				return this.waterLevel < 20;
			case DOUBLED:
				return this.waterLevel < 40;
			default:
				return this.waterLevel < 20;
		}
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
	public void addExhaustion(Player player, float exhaustion) {
		if (!player.getAbilities().invulnerable) {
			if (!player.level().isClientSide) {
				this.addExhaustion(exhaustion);
				save(player);
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

	@Override
	public void save(LivingEntity player) {
		SurviveEntityStats.setWaterStats(player, this);
	}

	@Override
	public boolean shouldTick() {
		return Survive.THIRST_CONFIG.enabled;
	}

	/////-----------EVENTS-----------/////

	public static boolean applyThirst(LivingEntity entity, float probabiltiy) {
		if (probabiltiy > 0) {
			Random rand = new Random();
			if (rand.nextFloat() < probabiltiy) {
				entity.addEffect(new MobEffectInstance(SMobEffects.THIRST, 30*20));
				return true;
			}
		}
		return false;
	}
}
