package com.stereowalker.survive.needs;

import java.util.Map;

import com.google.common.collect.Maps;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.hooks.SurviveHooks;
import com.stereowalker.survive.world.effect.SEffects;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.util.NBTHelper.NbtType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TemperatureData extends SurviveData {
	private double temperatureLevel = 0;
	private double displayTemperature = 0;
	private int temperatureTimer;
	private double targetTemperature = 0;
	private int hypTimer = 0;
	private Map<ResourceLocation,TemperatureModifier> temperatureModifiers = Maps.newHashMap();

	public TemperatureData() {
		this.hypTimer = Survive.CONFIG.tempGrace;
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

	private boolean addTemperature(ServerPlayer player, double temperature) {
		if (Survive.CONFIG.enable_temperature) {
			if (player.gameMode.isSurvival()) {
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
		TemperatureData temp = SurviveEntityStats.getTemperatureStats(entity);
		double newValue = SurviveHooks.getTemperatureModifer(entity, id, value);
		temp.getOrCreateModifier(id).setMod(newValue);
		temp.save(entity);;
	}

	public static void setTemperatureModifier(LivingEntity entity, String id, double value) {
		setTemperatureModifier(entity, new ResourceLocation(id), value);
	}

	/**
	 * Handles the temperature game logic.
	 */
	public void tick(Player player) {
		double calculatedTarget = Survive.DEFAULT_TEMP;
		for (TemperatureModifier modifier : this.temperatureModifiers.values()) {
			calculatedTarget+=modifier.getMod();
			//			Survive.debug(modifier+" "+SurviveEvents.isSnowingAt(player.getServerWorld(), player.getPosition()));
			//			Survive.debug("Target: "+calculatedTarget);
		}
		this.targetTemperature = calculatedTarget;
		double mod = (this.targetTemperature - this.temperatureLevel) * Survive.CONFIG.tempChangeSpeed;
		//		Survive.debug("Modifier: "+(this.targetTemperature-Survive.DEFAULT_TEMP)+" Target: "+targetTemperature+" Value: "+temperatureLevel);
		if (player instanceof ServerPlayer)
			addTemperature((ServerPlayer) player, mod);
		//For Display Purposes
		double tempLocation = this.temperatureLevel - Survive.DEFAULT_TEMP;
		if (tempLocation > 0) {
			double maxTemp = 0.0D;
			if (player.getAttribute(SAttributes.HEAT_RESISTANCE) != null) {
				maxTemp = player.getAttributeValue(SAttributes.HEAT_RESISTANCE);
			}
			double div = tempLocation / maxTemp;
			this.displayTemperature = Mth.clamp(div, 0, 1.0D+(28.0D/63.0D));
		}
		if (tempLocation < 0) {
			double maxTemp = 0.0D;
			if (player.getAttribute(SAttributes.COLD_RESISTANCE) != null) {
				maxTemp = player.getAttributeValue(SAttributes.COLD_RESISTANCE);
			}
			double div = tempLocation / maxTemp;
			this.displayTemperature = Mth.clamp(div, -1.0D-(28.0D/63.0D), 0);
		}

		if(!(player.isCreative() || player.isSpectator())) {
			double maxHeat1 = TemperatureUtil.firstHeat(player);
			double maxHeat2 = TemperatureUtil.secondHeat(player);
			double maxHeat3 = TemperatureUtil.maxHeat(player);
			double maxCold1 = TemperatureUtil.firstCold(player);
			double maxCold2 = TemperatureUtil.secondCold(player);
			double maxCold3 = TemperatureUtil.maxCold(player);

			if (this.temperatureLevel > maxHeat1 || this.temperatureLevel < maxCold1) {
				if (this.hypTimer > 0) {
					this.hypTimer--;
				} else if (this.hypTimer == 0) {
					if (!player.hasEffect(SEffects.HYPERTHERMIA) && !player.hasEffect(SEffects.HYPOTHERMIA)) {
						if (this.temperatureLevel > maxHeat1 && this.temperatureLevel <= maxHeat2) {
							player.addEffect(new MobEffectInstance(SEffects.HYPERTHERMIA, 100, 0));
						}
						else if (this.temperatureLevel > maxHeat2 && this.temperatureLevel <= maxHeat3) {
							player.addEffect(new MobEffectInstance(SEffects.HYPERTHERMIA, 100, 1));
						}
						else if (this.temperatureLevel > maxHeat3) {
							player.addEffect(new MobEffectInstance(SEffects.HYPERTHERMIA, 100, 2));
						}
						
						if (this.temperatureLevel < maxCold1 && this.temperatureLevel >= maxCold2) {
							player.addEffect(new MobEffectInstance(SEffects.HYPOTHERMIA, 100, 0));
						}
						else if (this.temperatureLevel < maxCold2 && this.temperatureLevel >= maxCold3) {
							player.addEffect(new MobEffectInstance(SEffects.HYPOTHERMIA, 100, 1));
						}
						else if (this.temperatureLevel < maxCold3) {
							player.addEffect(new MobEffectInstance(SEffects.HYPOTHERMIA, 100, 2));
						}
					}
				}
			} else if (this.hypTimer < Survive.CONFIG.tempGrace){
				this.hypTimer++;
			}
		}
	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundTag compound) {
		if (compound.contains("temperatureLevel", 99)) {
			this.temperatureLevel = compound.getDouble("temperatureLevel");
			this.targetTemperature = compound.getDouble("targetTemperature");
			this.temperatureTimer = compound.getInt("temperatureTickTimer");
			this.displayTemperature = compound.getDouble("displayTemperature");
			this.hypTimer = compound.getInt("hypTimer");

			ListTag modifiers = compound.getList("modifiers", NbtType.CompoundNBT);
			Map<ResourceLocation,TemperatureModifier> temperatureModifiers = Maps.newHashMap();
			for(int i = 0; i < modifiers.size(); i++) {
				CompoundTag nbt = modifiers.getCompound(i);
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
	public void write(CompoundTag compound) {
		compound.putDouble("temperatureLevel", this.temperatureLevel);
		compound.putDouble("targetTemperature", this.targetTemperature);
		compound.putInt("temperatureTickTimer", this.temperatureTimer);
		compound.putDouble("displayTemperature", this.displayTemperature);
		compound.putInt("hypTimer", this.hypTimer);
		ListTag modifiers = new ListTag();
		for(TemperatureModifier modifier : temperatureModifiers.values()) {
			modifiers.add(modifier.write(new CompoundTag()));
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

	@Override
	public void save(LivingEntity player) {
		SurviveEntityStats.setTemperatureStats(player, this);
	}

	@Override
	public boolean shouldTick() {
		return Survive.CONFIG.enable_temperature;
	}
}
