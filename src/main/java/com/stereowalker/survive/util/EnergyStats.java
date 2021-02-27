package com.stereowalker.survive.util;

import java.util.Random;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.network.client.CEnergyMovementPacket;
import com.stereowalker.survive.network.client.CEnergyTaxPacket;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;

@EventBusSubscriber
public class EnergyStats {
	private int energyLevel = 20;
	private int energyReserveLevel;
	private float energyExhaustionLevel;
	private int energyTimer;
	@SuppressWarnings("unused")
	private int prevEnergyLevel = 20;

	public EnergyStats() {
		this.energyReserveLevel = 6;
	}

	/**
	 * Add water stats.
	 */
	public void addStats(int energyLevelIn) {
		int remaining = 0;;
		remaining = (this.energyReserveLevel + energyLevelIn) - 6;
		this.energyReserveLevel = Math.min(energyLevelIn + this.energyReserveLevel, 6);
		if (remaining > 0) {
			this.energyLevel = Math.min(remaining + this.energyLevel, 20);
		}
	}

	/**
	 * Handles the water game logic.
	 */
	public void tick(PlayerEntity player) {
		Difficulty difficulty = player.world.getDifficulty();
		this.prevEnergyLevel = this.energyLevel;
		if (this.energyExhaustionLevel > 4.0F) {
			this.energyExhaustionLevel -= 4.0F;
			if (difficulty != Difficulty.PEACEFUL) {
				if (this.energyLevel > 0) {
					this.energyLevel = Math.max(this.energyLevel - 1, 0);
				} else if (this.energyReserveLevel > 0) {
					this.energyReserveLevel = Math.max(this.energyReserveLevel - 1, 0);
				}
			}
		}

		boolean flag = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		if (flag && this.energyReserveLevel > 6 && player.shouldHeal() && this.energyLevel >= 19) {
			++this.energyTimer;
			if (this.energyTimer >= 10) {
				player.heal(2.0F);
				this.addExhaustion(6.0F);
				this.energyTimer = 0;
			}
		} else if (flag && this.energyReserveLevel > 6 && player.shouldHeal() && this.energyLevel >= 18) {
			++this.energyTimer;
			if (this.energyTimer >= 80) {
				player.heal(1.0F);
				this.addExhaustion(6.0F);
				this.energyTimer = 0;
			}
		} else if (this.energyLevel <= 0 && this.energyReserveLevel <= 0) {
			++this.energyTimer;
			if (this.energyTimer >= 20) {
				player.attackEntityFrom(SDamageSource.OVERWORK, 3.0F);
//				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
//				}

				this.energyTimer = 0;
			}
		} else {
			this.energyTimer = 0;
		}

	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundNBT compound) {
		if (compound.contains("energyLevel", 99)) {
			this.energyLevel = compound.getInt("energyLevel");
			this.energyTimer = compound.getInt("energyTickTimer");
			this.energyReserveLevel = compound.getInt("energyReserveLevel");
			this.energyExhaustionLevel = compound.getFloat("energyExhaustionLevel");
		}

	}

	/**
	 * Writes the water data for the player.
	 */
	public void write(CompoundNBT compound) {
		compound.putInt("energyLevel", this.energyLevel);
		compound.putInt("energyTickTimer", this.energyTimer);
		compound.putInt("energyReserveLevel", this.energyReserveLevel);
		compound.putFloat("energyExhaustionLevel", this.energyExhaustionLevel);
	}

	/**
	 * Get the player's water level.
	 */
	public int getEnergyLevel() {
		return this.energyLevel;
	}

	/**
	 * Get whether the player must drink water.
	 */
	public boolean isTired() {
		return this.energyLevel < 20;
	}

	public boolean isExhausted() {
		return this.energyLevel <= 0;
	}

	/**
	 * adds input to waterExhaustionLevel to a max of 40
	 */
	private void addExhaustion(float exhaustion) {
		this.energyExhaustionLevel = Math.min(this.energyExhaustionLevel + exhaustion, 40.0F);
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
	public int getReserveLevel() {
		return this.energyReserveLevel;
	}

	public void setEnergyLevel(int energyLevelIn) {
		this.energyLevel = energyLevelIn;
	}

	@OnlyIn(Dist.CLIENT)
	public void setEnergyReserveLevel(int energyReserveLevelIn) {
		this.energyReserveLevel = energyReserveLevelIn;
	}
	
	/////-----------EVENTS-----------/////
	@SubscribeEvent
	public static void breakBlock(BlockEvent.BreakEvent breakEvent) {
		if(!breakEvent.isCanceled()) {
			EnergyStats energyStats = SurviveEntityStats.getEnergyStats(breakEvent.getPlayer());
			if (!ForgeHooks.canHarvestBlock(breakEvent.getState(), breakEvent.getPlayer(), breakEvent.getWorld(), breakEvent.getPos())) {
				energyStats.addExhaustion(breakEvent.getPlayer(), 5.0F);
			} else {
				energyStats.addExhaustion(breakEvent.getPlayer(), 0.5F);
			}
			SurviveEntityStats.setEnergyStats(breakEvent.getPlayer(), energyStats);
		}
	}
	
	@SubscribeEvent
	public static void clickBlock(PlayerInteractEvent.RightClickBlock clickBlock) {
		if(!clickBlock.isCanceled() && clickBlock.getPlayer() instanceof PlayerEntity && clickBlock.getCancellationResult().isSuccessOrConsume()) {
			EnergyStats energyStats = SurviveEntityStats.getEnergyStats(clickBlock.getPlayer());
			energyStats.addExhaustion(clickBlock.getPlayer(), 0.5F);
			SurviveEntityStats.setEnergyStats(clickBlock.getPlayer(), energyStats);
		}
	}
	
	@SubscribeEvent
	public static void clickItem(PlayerInteractEvent.RightClickItem clickItem) {
		if(!clickItem.isCanceled() && clickItem.getPlayer() instanceof PlayerEntity && clickItem.getCancellationResult().isSuccessOrConsume()) {
			EnergyStats energyStats = SurviveEntityStats.getEnergyStats(clickItem.getPlayer());
			energyStats.addExhaustion(clickItem.getPlayer(), 0.5F);
			SurviveEntityStats.setEnergyStats(clickItem.getPlayer(), energyStats);
		}
	}
	
	@SubscribeEvent
	public static void rightClickEmpty(PlayerInteractEvent.RightClickEmpty clickItem) {
		if(!clickItem.isCanceled() && clickItem.getPlayer() instanceof ClientPlayerEntity && clickItem.getCancellationResult().isSuccessOrConsume()) {
			ClientPlayerEntity player = (ClientPlayerEntity)clickItem.getPlayer();
			Survive.CHANNEL.sendTo(new CEnergyTaxPacket(0.125F, player.getUniqueID()), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
		}
	}
	
	@SubscribeEvent
	public static void leftClickEmpty(PlayerInteractEvent.LeftClickEmpty clickItem) {
		if(!clickItem.isCanceled() && clickItem.getPlayer() instanceof ClientPlayerEntity && clickItem.getCancellationResult().isSuccessOrConsume()) {
			ClientPlayerEntity player = (ClientPlayerEntity)clickItem.getPlayer();
			Survive.CHANNEL.sendTo(new CEnergyTaxPacket(0.125F, player.getUniqueID()), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
		}
	}
	
	@SubscribeEvent
	public static void attackEntity(AttackEntityEvent entityEvent) {
		if(!entityEvent.isCanceled()) {
			EnergyStats energyStats = SurviveEntityStats.getEnergyStats(entityEvent.getPlayer());
			energyStats.addExhaustion(entityEvent.getPlayer(), 0.5F);
			SurviveEntityStats.setEnergyStats(entityEvent.getPlayer(), energyStats);
		}
	}
	
	@SubscribeEvent
	public static void passivelyIncreaseEnergy(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
			Random rand = new Random();
			if (Config.enable_stamina) {
				EnergyStats energyStats = SurviveEntityStats.getEnergyStats(player);
				if (player.isSleeping() && player.ticksExisted%20 == 19) {
					energyStats.addStats(1);
				}
				if (player.ticksExisted%600 == 599) {
					if (rand.nextInt(2)==0)energyStats.addStats(1);
				}
				if (player.world.getDifficulty() == Difficulty.PEACEFUL && player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
					if (energyStats.isTired() && player.ticksExisted % 10 == 0) {
						energyStats.setEnergyLevel(energyStats.getEnergyLevel() + 1);
					}
				}
				energyStats.tick(player);
				SurviveEntityStats.setEnergyStats(player, energyStats);
			}
		}
		if (event.getEntityLiving() != null && event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ClientPlayerEntity) {
			ClientPlayerEntity player = (ClientPlayerEntity)event.getEntityLiving();
			if (player.ticksExisted%90 == 89) {
				if (player.world.getDifficulty() != Difficulty.PEACEFUL) {
					new CEnergyMovementPacket(player.movementInput.moveForward, player.movementInput.moveStrafe, player.movementInput.jump).send();
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void replenishEnergyOnSleep(SleepFinishedTimeEvent event) {
		for (PlayerEntity player : event.getWorld().getPlayers()) {
			EnergyStats energyStats = SurviveEntityStats.getEnergyStats(player);
			energyStats.addStats(20);
			SurviveEntityStats.setEnergyStats(player, energyStats);
		}
	}
	
	@SubscribeEvent
	public static void eatFood(LivingEntityUseItemEvent.Finish event) {
		if (event.getResultStack().isFood() && Survive.consummableItemMap.containsKey(event.getItem().getItem().getRegistryName())) {
			if (event.getEntityLiving() != null && !event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
				EnergyStats energyStats = SurviveEntityStats.getEnergyStats(player);
				energyStats.addStats(Survive.consummableItemMap.get(event.getItem().getItem().getRegistryName()).getEnergyAmount());
				SurviveEntityStats.setEnergyStats(player, energyStats);
			}
		}
	}
}
