package com.stereowalker.survive.needs;

import java.util.Random;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.api.needs.Stamina;
import com.stereowalker.survive.core.WeightHandler;
import com.stereowalker.survive.damagesource.SDamageSources;
import com.stereowalker.survive.damagesource.SDamageTypes;
import com.stereowalker.survive.network.protocol.game.ServerboundArmorStaminaPacket;
import com.stereowalker.survive.network.protocol.game.ServerboundRelaxPacket;
import com.stereowalker.survive.network.protocol.game.ServerboundStaminaExhaustionPacket;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.api.insert.InsertResultCanceller;
import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.unionlib.util.VersionHelper.VanillaComponents;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;

@EventBusSubscriber
public class StaminaData extends SurviveData implements Stamina {
	private int longStamina;
	private int shortStamina;
	private int energyReserveLevel;
	private float longExhaustion;
	private float shortExhaustion;
	private int energyTimer;
	private int shortTermTimer;
	private int shortRecoveryTimer;
	private boolean isStraining;
	@SuppressWarnings("unused")
	private int prevEnergyLevel;
	private int maxLongStamina;
	private int maxBurstStamina = 10;

	public StaminaData(double maxStamina) {
		this.energyReserveLevel = 6;
		this.longStamina = Mth.floor(maxStamina);
		this.prevEnergyLevel = Mth.floor(maxStamina);
		this.shortStamina = maxBurstStamina;
	}

	/**
	 * Add water stats.
	 */
	public void relax(int energyLevelIn, double maxStamina) {
		int remaining = 0;
		remaining = (this.energyReserveLevel + energyLevelIn) - 6;
		this.energyReserveLevel = Math.min(energyLevelIn + this.energyReserveLevel, 6);
		if (remaining > 0) {
			this.longStamina = Math.min(remaining + this.longStamina, Mth.floor(maxStamina));
		}
	}
	
	public void eat(Item pItem, ItemStack pStack, LivingEntity entity) {
		if (VanillaComponents.FOOD.hasData(pStack) && DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(pItem))) {
			if (entity instanceof ServerPlayer && !entity.level().isClientSide) {
				ServerPlayer player = (ServerPlayer)entity;
				relax(DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(pItem)).getEnergyAmount(), player.getAttributeValue(SAttributes.MAX_STAMINA.holder()));
			}
		}
	}

	@Override
	public void clientTick(Player player) {
		if (player.isPassenger() && player.tickCount%400 == 399) {
			if (player.getVehicle().getDeltaMovement().x == 0 && player.getVehicle().getDeltaMovement().z == 0)
				new ServerboundRelaxPacket(1).send();
		}
		if (player.tickCount%90 == 89) {
			if (player.level().getDifficulty() != Difficulty.PEACEFUL) {
				new ServerboundArmorStaminaPacket().send();
			}
		}
	}

	/**
	 * Handles the stamina game logic.
	 */
	public void tick(Player player) {
		IRealisticEntity realPlayer = (IRealisticEntity)player;
		//Sets the maximum stamina
		this.maxLongStamina = Mth.floor(player.getAttributeValue(SAttributes.MAX_STAMINA.holder()));
		//Forces the player awake if their energy is too low and it's day
		if (player.isSleeping() && player.level().isDay() && this.longStamina < this.maxLongStamina/2) {
			player.sleepCounter = 0;
		}
		
		Difficulty difficulty = player.level().getDifficulty();
		int energyToRegen = 1 + (player.hasEffect(SMobEffects.WELL_FED.holder()) ? new Random().nextInt(2) : 0);
		this.prevEnergyLevel = this.longStamina;
		
		//LTS exhaustion
		if (this.longExhaustion > 20.0F) {
			this.longExhaustion -= 20.0F;
			if (difficulty != Difficulty.PEACEFUL) {
				if (this.longStamina > 0) {
					this.longStamina = Math.max(this.longStamina - 1, 0);
				} else if (this.energyReserveLevel > 0) {
					this.energyReserveLevel = Math.max(this.energyReserveLevel - 1, 0);
				}
			}
		}
		
		
		//Handles short term stamina
		if (!isStraining && shortTermTimer > 0) { //
			isStraining = true;
			maxBurstStamina = 10;
			if (Survive.CONFIG.nutrition_enabled) {
				int carb = realPlayer.nutritionData().carbs().level();
				if (carb > 2000)
					maxBurstStamina = Mth.lerpInt(((carb - 2000) / 1000f), 12, 20);
				else if (carb > 1000)
					maxBurstStamina = Mth.lerpInt(((carb - 1000) / 1000f), 7, 12);
				else if (carb > 0)
					maxBurstStamina = Mth.lerpInt((carb / 2000f), 3, 7);
				else
					maxBurstStamina = Mth.lerpInt(((1000 + carb) / 1000f), 1, 3);
			}
			shortExhaustion = 0;
			this.shortStamina = maxBurstStamina;
		} else if (isStraining && shortTermTimer > 0) {
			this.shortTermTimer--;
			if (this.shortExhaustion > 5.0F) {
				this.shortExhaustion -= 5.0F;
				if (difficulty != Difficulty.PEACEFUL && this.shortStamina > 0) {
					this.shortStamina = Math.max(this.shortStamina - 1, 0);
				}
			}
			if (this.shortStamina <= 0) this.shortTermTimer = 0;
		} else if (!isStraining && shortTermTimer <= 0) {
			this.shortRecoveryTimer--;
		} else if (isStraining && shortTermTimer <= 0) {
			isStraining = false;
			this.shortRecoveryTimer = maxBurstStamina - shortStamina;
			addExhaustion(this.shortRecoveryTimer * 7.8f, false);
			realPlayer.nutritionData().carbs().remove(this.shortRecoveryTimer*10);
			this.shortRecoveryTimer *= 13;
			player.addEffect(new MobEffectInstance(SMobEffects.FATIGUE.holder(), this.shortRecoveryTimer, 1, false, true));
		}
		//End of short term stamina

		if (this.isTired() && Survive.CONFIG.nutrition_enabled && ((IRealisticEntity)player).nutritionData().fat().level() >= 20) {
			++this.energyTimer;
			if (Survive.STAMINA_CONFIG.stamina_recovery_ticks == 0 || this.energyTimer >= Survive.STAMINA_CONFIG.stamina_recovery_ticks) {
				this.relax(energyToRegen, this.maxLongStamina);
				realPlayer.nutritionData().fat().remove(20);
				this.energyTimer = 0;
			}
		}
		else if (this.isTired() && !Survive.CONFIG.nutrition_enabled && player.getFoodData().getFoodLevel() > Survive.STAMINA_CONFIG.min_food && WeightHandler.getTotalArmorWeight(player)/Survive.STAMINA_CONFIG.max_weight < 1.0F) {
			++this.energyTimer;
			if (Survive.STAMINA_CONFIG.stamina_recovery_ticks == 0 || this.energyTimer >= Survive.STAMINA_CONFIG.stamina_recovery_ticks) {
				this.relax(energyToRegen, this.maxLongStamina);
				player.getFoodData().addExhaustion(2.0F);
				this.energyTimer = 0;
			}
		}
		else if (player.isSleeping()) {
			++this.energyTimer;
			if (this.energyTimer >= Math.floor((float)Survive.STAMINA_CONFIG.sleepTime/(float)(maxLongStamina+6))) {
				this.relax(energyToRegen, this.maxLongStamina);
				this.energyTimer = 0;
			}
		} else if (this.longStamina <= 0 && this.energyReserveLevel <= 0) {
			++this.energyTimer;
			if (this.energyTimer >= 20) {
				player.hurt(SDamageSources.source(player.level().registryAccess(), SDamageTypes.OVERWORK), 3.0F);
				//				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
				//				}

				this.energyTimer = 0;
			}
		} else {
			this.energyTimer = 0;
		}
		if (difficulty == Difficulty.PEACEFUL && player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
			if (this.isTired() && player.tickCount % 10 == 0) {
				this.setEnergyLevel(this.getLTS() + 1);
			}
		}
	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundTag compound) {
		if (compound.contains("longStamina", 99)) {
			this.maxBurstStamina = compound.getInt("maxBurstStamina");
			this.maxLongStamina = compound.getInt("maxLongStamina");
			this.longStamina = compound.getInt("longStamina");
			this.longExhaustion = compound.getFloat("longExhaustion");
			this.shortStamina = compound.getInt("shortStamina");
			this.shortExhaustion = compound.getFloat("shortExhaustion");
			this.shortTermTimer = compound.getInt("shortTermTimer");
			this.shortRecoveryTimer = compound.getInt("shortRecoveryTimer");
			this.isStraining = compound.getBoolean("isStraining");
			this.energyTimer = compound.getInt("energyTickTimer");
			this.energyReserveLevel = compound.getInt("energyReserveLevel");
		}

	}

	/**
	 * Writes the water data for the player.
	 */
	public void write(CompoundTag compound, boolean reducedData) {
		compound.putInt("maxBurstStamina", this.maxBurstStamina);
		compound.putInt("maxLongStamina", this.maxLongStamina);
		compound.putInt("longStamina", this.longStamina);
		compound.putInt("shortStamina", this.shortStamina);
		compound.putBoolean("isStraining", this.isStraining);
		compound.putInt("energyReserveLevel", this.energyReserveLevel);
		if (!reducedData) {
			compound.putFloat("shortExhaustion", this.shortExhaustion);
			compound.putFloat("longExhaustion", this.longExhaustion);
			compound.putInt("energyTickTimer", this.energyTimer);
			compound.putInt("shortTermTimer", this.shortTermTimer);
		}
		compound.putInt("shortRecoveryTimer", this.shortRecoveryTimer); //Dev note, this is only out here because parcool only does stuff on the client
	}

	/**
	 * Get the player's stamina level.
	 */
	public int getLTS() {
		return this.longStamina;
	}
	

	public int getMaxLTS() {
		return this.maxLongStamina;
	}
	
	@Override
	public int getBurstStamina() {
		return this.shortStamina;
	}
	
	/**
	 * Get the player's stamina level.
	 */
	public int getSTSRecovery() {
		return maxBurstStamina - Mth.floor(this.shortRecoveryTimer / 13f);
	}

	/**
	 * Get whether the player must drink water.
	 */
	public boolean isTired() {
		return this.longStamina < maxLongStamina;
	}
	
	public boolean isExerting() {
		return this.isStraining;
	}
	
	@Override
	public boolean isShortOfBreath() {
		return this.shortRecoveryTimer > 0;
	}

	@Override
	public boolean isDeadTired() {
		return this.longStamina <= 0;
	}

	/**
	 * adds input to waterExhaustionLevel to a max of 40
	 */
	public void addExhaustion(float exhaustion, boolean strain) {
		if (strain && !this.isShortOfBreath()) {
			this.shortExhaustion = Math.min(this.shortExhaustion + exhaustion, 40.0F);
			this.shortTermTimer = 100;
		}
		else
			this.longExhaustion = Math.min(this.longExhaustion + exhaustion, 40.0F);
	}

	/**
	 * Get the player's water hydration level.
	 */
	public int getReserveLevel() {
		return this.energyReserveLevel;
	}

	public void setEnergyLevel(int energyLevelIn) {
		this.longStamina = energyLevelIn;
	}

	public void setEnergyReserveLevel(int energyReserveLevelIn) {
		this.energyReserveLevel = energyReserveLevelIn;
	}

	@Override
	public void save(LivingEntity player) {
	}

	@Override
	public boolean shouldTick() {
		return Survive.STAMINA_CONFIG.enabled;
	}

	/////-----------EVENTS-----------/////

	public static void clickItem(Player player, Level level, InteractionHand hand, InsertResultCanceller<InteractionResultHolder<ItemStack>> cancel) {
		if(!cancel.wasCancelled() && player instanceof Player && cancel.cancelResult().getResult().consumesAction()) {

		}
	}

	@SubscribeEvent
	public static void rightClickEmpty(PlayerInteractEvent.RightClickEmpty clickItem) {
		if (/* !clickItem.isCanceled() && */clickItem
				.getEntity() instanceof LocalPlayer/* && clickItem.getCancellationResult().consumesAction() */) {
			new ServerboundStaminaExhaustionPacket(0.3125F).send();
		}
	}

	@SubscribeEvent
	public static void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty clickItem) {
		if (/* !clickItem.isCanceled() && */clickItem
				.getEntity() instanceof LocalPlayer/* && clickItem.getCancellationResult().consumesAction() */) {
			new ServerboundStaminaExhaustionPacket(0.3125F).send();
		}
	}
}
