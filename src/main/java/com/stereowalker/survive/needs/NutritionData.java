package com.stereowalker.survive.needs;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class NutritionData extends SurviveData {
	private MutableInt carbLevel = new MutableInt(0);
	private MutableInt carbTimer = new MutableInt(0);
	private MutableFloat carbStack = new MutableFloat(0);
	private MutableInt proteinLevel = new MutableInt(0);
	private MutableInt proteinTimer = new MutableInt(0);
	private MutableFloat proteinStack = new MutableFloat(0);

	public NutritionData() {
		this.carbLevel = new MutableInt(200);
		this.proteinLevel = new MutableInt(200);
	}

	/**
	 * Add carbs.
	 */
	public void addCarbs(float carb) {
		this.carbStack.add(Mth.clamp(carb, -100, 300));
	}

	public void removeCarbs(int carbs) {
		this.carbLevel.subtract(carbs);
	}

	/**
	 * Add protein.
	 */
	public void addProtein(float carb) {
		this.proteinStack.add(Mth.clamp(carb, -100, 300));
	}

	public void removeProtein(int protein) {
		this.proteinLevel.subtract(protein);
	}
	
	public void hand(Player player, MutableInt timer, MutableInt level, MutableFloat stack) {
		if (stack.getValue() >= 10 && level.getValue() < 300) {
			timer.increment();
			if (timer.getValue() > 100 && player.getFoodData().getFoodLevel() > 3) {
				stack.subtract(10);
				level.add(10);
				player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()-1);
				timer.setValue(0);
			}
		} else {
			timer.setValue(0);
		}
	}

	/**
	 * Handles the temperature game logic.
	 */
	@Override
	public void tick(Player player) {
		//Carbs
		hand(player, this.carbTimer, this.carbLevel, this.carbStack);
		//Protein
		hand(player, this.proteinTimer, this.proteinLevel, this.proteinStack);
	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundTag compound) {
		if (compound.contains("carbLevel", 99)) {
			this.carbLevel = new MutableInt(compound.getInt("carbLevel"));
			this.carbTimer = new MutableInt(compound.getInt("carbTimer"));
			this.carbStack = new MutableFloat(compound.getFloat("carbStack"));
			
			this.proteinLevel = new MutableInt(compound.getInt("proteinLevel"));
			this.proteinTimer = new MutableInt(compound.getInt("proteinTimer"));
			this.proteinStack = new MutableFloat(compound.getFloat("proteinStack"));
		}
	}

	/**
	 * Writes the water data for the player.
	 */
	public void write(CompoundTag compound) {
		compound.putInt("carbLevel", this.carbLevel.getValue());
		compound.putInt("carbTimer", this.carbTimer.getValue());
		compound.putFloat("carbStack", this.carbStack.getValue());
		
		compound.putInt("proteinLevel", this.proteinLevel.getValue());
		compound.putInt("proteinTimer", this.proteinTimer.getValue());
		compound.putFloat("proteinStack", this.proteinStack.getValue());
	}

	/**
	 * Get the player's water level.
	 */
	public int getCarbLevel() {
		return this.carbLevel.intValue();
	}

	/**
	 * Get the player's water level.
	 */
	public int getProteinLevel() {
		return this.proteinLevel.intValue();
	}

	@Override
	public void save(LivingEntity player) {
		SurviveEntityStats.setNutritionStats(player, this);
	}

	@Override
	public boolean shouldTick() {
		return Survive.CONFIG.nutrition_enabled;
	}
}
