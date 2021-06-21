package com.stereowalker.survive.util;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class NutritionStats extends SurviveStats {
	private int carbLevel = 0;
	private int carbTimer = 0;
	private float carbStack = 0;
	private int proteinLevel = 0;
	private int proteinTimer = 0;
	private float proteinStack = 0;

	public NutritionStats() {
		this.carbLevel = 200;
		this.proteinLevel = 200;
	}

	/**
	 * Add carbs.
	 */
	public void addCarbs(float carb) {
		System.out.println("Carbs gained "+carb);
		this.carbStack = MathHelper.clamp(carb, -100, 300);
	}

	public void removeCarbs(int carbs) {
		this.carbLevel-=carbs;
	}

	/**
	 * Add protein.
	 */
	public void addProtein(float carb) {
		this.proteinStack = MathHelper.clamp(carb, -100, 300);
	}

	public void removeProtein(int protein) {
		this.proteinLevel-=protein;
	}

	/**
	 * Handles the temperature game logic.
	 */
	public void tick(PlayerEntity player) {
		System.out.println(this.carbStack);
		//Carbs
		if (this.carbStack >= 1 && this.carbLevel < 300) {
			this.carbTimer++;
			if (this.carbTimer > 80 && player.getFoodStats().getFoodLevel() > 3) {
				this.carbStack -= 1;
				this.carbLevel += 1;
				player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel()-1);
				this.carbTimer = 0;
			}
		} else {
			this.carbTimer = 0;
		}
		//Protein
		if (this.proteinStack >= 1 && this.proteinLevel < 300) {
			this.proteinTimer++;
			if (this.proteinTimer > 80 && player.getFoodStats().getFoodLevel() > 3) {
				this.proteinStack -= 1;
				this.proteinLevel += 1;
				player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel()-1);
			}
		} else {
			this.proteinTimer = 0;
		}
	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundNBT compound) {
		if (compound.contains("carbLevel", 99)) {
			this.carbLevel = compound.getInt("carbLevel");
			this.carbTimer = compound.getInt("carbTimer");
			this.carbStack = compound.getFloat("carbStack");
			this.proteinLevel = compound.getInt("proteinLevel");
			this.proteinTimer = compound.getInt("proteinTimer");
			this.proteinStack = compound.getFloat("proteinStack");
		}
	}

	/**
	 * Writes the water data for the player.
	 */
	public void write(CompoundNBT compound) {
		compound.putInt("carbLevel", this.carbLevel);
		compound.putInt("carbTimer", this.carbTimer);
		compound.putFloat("carbStack", this.carbStack);
		compound.putInt("proteinLevel", this.proteinLevel);
		compound.putInt("proteinTimer", this.proteinTimer);
		compound.putFloat("proteinStack", this.proteinStack);
	}

	/**
	 * Get the player's water level.
	 */
	public int getCarbLevel() {
		return this.carbLevel;
	}

	/**
	 * Get the player's water level.
	 */
	public int getProteinLevel() {
		return this.proteinLevel;
	}

	@Override
	public void save(LivingEntity player) {
		SurviveEntityStats.setNutritionStats(player, this);
	}

	@Override
	public boolean shouldTick() {
		return Config.nutrition_enabled;
	}
}
