package com.stereowalker.survive.util;

import java.util.Map;

import com.google.common.collect.Maps;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.entity.ai.SAttributes;
import com.stereowalker.survive.hooks.SurviveHooks;
import com.stereowalker.unionlib.util.NBTHelper.NbtType;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TemperatureStats {
	private double temperatureLevel = 0;
	private double displayTemperature = 0;
	private int temperatureTimer;
	private double targetTemperature = 0;
	private Map<ResourceLocation,TemperatureModifier> temperatureModifiers = Maps.newHashMap();

	public TemperatureStats() {
		this.temperatureLevel = Survive.DEFAULT_TEMP;
	}

	/**
	 * Add water stats.
	 */
	public void addHeat(float heatLevel, double max) {
		this.temperatureLevel = Math.min(this.temperatureLevel + heatLevel, max);
	}
	
	public void addCold(float coldLevel, double max) {
		this.temperatureLevel = Math.max(this.temperatureLevel - coldLevel, max);
	}
	
	private boolean addTemperature(ServerPlayerEntity player, double temperature) {
		if (Config.enable_temperature) {
			if (player.interactionManager.survivalOrAdventure()) {
				double defaultT = Survive.DEFAULT_TEMP;
				double maxHeat1 = TemperatureUtil.firstHeat(player);
				double maxHeat2 = TemperatureUtil.secondHeat(player);
				double maxHeat3 = TemperatureUtil.maxHeat(player);
				double maxCold1 = TemperatureUtil.firstCold(player);
				double maxCold2 = TemperatureUtil.secondCold(player);
				double maxCold3 = TemperatureUtil.maxCold(player);

				if (this.temperatureLevel > defaultT && this.temperatureLevel <= maxHeat1) {
					if (temperature < 0) this.temperatureLevel = this.temperatureLevel+(temperature*1.5D);
					else this.temperatureLevel = this.temperatureLevel+(temperature);
				}
				else if (this.temperatureLevel > maxHeat1 && this.temperatureLevel <= maxHeat2) {
					if (temperature < 0) this.temperatureLevel = this.temperatureLevel+(temperature);
					else this.temperatureLevel = this.temperatureLevel+(temperature/10.0D);
				}
				else if (this.temperatureLevel > maxHeat2 && this.temperatureLevel <= maxHeat3) {
					if (temperature < 0) this.temperatureLevel = this.temperatureLevel+(temperature/10.0D);
					else this.temperatureLevel = this.temperatureLevel+(temperature/100.0D);
				}
				else if (this.temperatureLevel > maxHeat3) {
					if (temperature < 0) this.temperatureLevel = this.temperatureLevel+(temperature/100.0D);
					else this.temperatureLevel = maxHeat3;
				}

				else if (this.temperatureLevel < defaultT && this.temperatureLevel >= maxCold1) {
					if (temperature > 0) this.temperatureLevel = this.temperatureLevel+(temperature*1.5D);
					else this.temperatureLevel = this.temperatureLevel+(temperature);
				}
				else if (this.temperatureLevel < maxCold1 && this.temperatureLevel >= maxCold2) {
					if (temperature > 0) this.temperatureLevel = this.temperatureLevel+(temperature);
					else this.temperatureLevel = this.temperatureLevel+(temperature/10.0D);
				}
				else if (this.temperatureLevel < maxCold2 && this.temperatureLevel >= maxCold3) {
					if (temperature > 0) this.temperatureLevel = this.temperatureLevel+(temperature/10.0D);
					else this.temperatureLevel = this.temperatureLevel+(temperature/100.0D);
				}
				else if (this.temperatureLevel < maxCold3) {
					if (temperature < 0) this.temperatureLevel = this.temperatureLevel+(temperature/100.0D);
					else this.temperatureLevel = maxCold3;
				}
				else {
					this.temperatureLevel = this.temperatureLevel+temperature;
				}
				return true;
			}
		} else {
			this.temperatureLevel = Survive.DEFAULT_TEMP;
		}
		return false;
	}
	
	public void addModifier(TemperatureModifier modifier) {
		if (!temperatureModifiers.containsKey(modifier.getId())) {
			temperatureModifiers.put(modifier.getId(), modifier);
		}
	}
	
	public TemperatureModifier getOrCreateModifier(ResourceLocation location) {
		if (!temperatureModifiers.containsKey(location)) { 
			addModifier(new TemperatureModifier(location, 0));
		}
		return temperatureModifiers.get(location);
	}
	
	public static void setTemperatureModifier(LivingEntity entity, ResourceLocation id, double value) {
		TemperatureStats temp = SurviveEntityStats.getTemperatureStats(entity);
		double newValue = SurviveHooks.getTemperatureModifer(entity, id, value);
		temp.getOrCreateModifier(id).setMod(newValue);
		SurviveEntityStats.setTemperatureStats(entity, temp);
	}
	
	public static void setTemperatureModifier(LivingEntity entity, String id, double value) {
		setTemperatureModifier(entity, new ResourceLocation(id), value);
	}

	/**
	 * Handles the temperature game logic.
	 */
	public void tick(ServerPlayerEntity player) {
		double calculatedTarget = Survive.DEFAULT_TEMP;
		for (TemperatureModifier modifier : this.temperatureModifiers.values()) {
			calculatedTarget+=modifier.getMod();
//			Survive.debug(modifier+" "+SurviveEvents.isSnowingAt(player.getServerWorld(), player.getPosition()));
//			Survive.debug("Target: "+calculatedTarget);
		}
		this.targetTemperature = calculatedTarget;
		double mod = (this.targetTemperature - this.temperatureLevel) * Config.tempChangeSpeed;
//		Survive.debug("Modifier: "+(this.targetTemperature-Survive.DEFAULT_TEMP)+" Target: "+targetTemperature+" Value: "+temperatureLevel);
		addTemperature(player, mod);
		//For Display Purposes
		double tempLocation = this.temperatureLevel - Survive.DEFAULT_TEMP;
		if (tempLocation > 0) {
			double maxTemp = 0.0D;
			if (player.getAttribute(SAttributes.HEAT_RESISTANCE) != null) {
				maxTemp = player.getAttributeValue(SAttributes.HEAT_RESISTANCE);
			}
			double div = tempLocation / maxTemp;
			this.displayTemperature = MathHelper.clamp(div, 0, 1.0D+(28.0D/63.0D));
		}
		if (tempLocation < 0) {
			double maxTemp = 0.0D;
			if (player.getAttribute(SAttributes.COLD_RESISTANCE) != null) {
				maxTemp = player.getAttributeValue(SAttributes.COLD_RESISTANCE);
			}
			double div = tempLocation / maxTemp;
			this.displayTemperature = MathHelper.clamp(div, -1.0D-(28.0D/63.0D), 0);
		}
	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundNBT compound) {
		if (compound.contains("temperatureLevel", 99)) {
			this.temperatureLevel = compound.getDouble("temperatureLevel");
			this.targetTemperature = compound.getDouble("targetTemperature");
			this.temperatureTimer = compound.getInt("temperatureTickTimer");
			this.displayTemperature = compound.getDouble("displayTemperature");
			
			ListNBT modifiers = compound.getList("modifiers", NbtType.CompoundNBT);
			Map<ResourceLocation,TemperatureModifier> temperatureModifiers = Maps.newHashMap();
			for(int i = 0; i < modifiers.size(); i++) {
				CompoundNBT nbt = modifiers.getCompound(i);
				TemperatureModifier modifier = new TemperatureModifier();
				modifier.read(nbt);
				temperatureModifiers.put(modifier.getId(), modifier);
			}
			this.temperatureModifiers = temperatureModifiers;
		}
	}

	/**
	 * Writes the water data for the player.
	 */
	public void write(CompoundNBT compound) {
		compound.putDouble("temperatureLevel", this.temperatureLevel);
		compound.putDouble("targetTemperature", this.targetTemperature);
		compound.putInt("temperatureTickTimer", this.temperatureTimer);
		compound.putDouble("displayTemperature", this.displayTemperature);
		ListNBT modifiers = new ListNBT();
		for(TemperatureModifier modifier : temperatureModifiers.values()) {
			modifiers.add(modifier.write(new CompoundNBT()));
		}
		compound.put("modifiers", modifiers);
	}

	/**
	 * Get the player's water level.
	 */
	public double getTemperatureLevel() {
		return this.temperatureLevel;
	}
	
	public double getTargetTemperature() {
		return targetTemperature;
	}

	public void setTemperatureLevel(int temperatureLevelIn) {
		this.temperatureLevel = temperatureLevelIn;
	}
	
	@SubscribeEvent
	public static void tickTemperature(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
			if (Config.enable_temperature) {
				TemperatureStats temperatureStats = SurviveEntityStats.getTemperatureStats(player);
				temperatureStats.tick(player);
				SurviveEntityStats.setTemperatureStats(player, temperatureStats);
			}
		}
//		if (event.getEntityLiving() != null && event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ClientPlayerEntity) {
//			ClientPlayerEntity player = (ClientPlayerEntity)event.getEntityLiving();
//		}
	}
}
