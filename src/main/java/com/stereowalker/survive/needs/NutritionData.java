package com.stereowalker.survive.needs;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

import com.stereowalker.survive.Survive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class NutritionData extends SurviveData {
	public class Nutrient {
		private MutableInt level = new MutableInt(0);
		private MutableInt timer = new MutableInt(0);
		private MutableFloat stack = new MutableFloat(0);
		
		public Nutrient(int initialLevel, float initialStacks) {
			level = new MutableInt(initialLevel);
			stack = new MutableFloat(initialStacks);
		}
		
		public void add(float nut) {
			this.stack.add(Mth.clamp(nut, -1000, 3000));
			if (stack.floatValue() > 3000) stack.setValue(3000);
		}

		public void remove(int nut) {
			this.level.subtract(nut);
		}

		public int level() {
			return this.level.intValue();
		}
		
		public void tick(Player player) {
			if (stack.getValue() >= 300 && level.getValue() < 3000) {
				timer.increment();
				if (timer.getValue() > 200 && player.getFoodData().getFoodLevel() > 3) {
					stack.subtract(300);
					level.add(300);
					player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()-1);
					timer.setValue(0);
				}
			} else {
				timer.setValue(0);
			}
		}
	}
	
	private Nutrient carb = new Nutrient(0, 0);
	private Nutrient protein = new Nutrient(0, 0);
	private Nutrient fat = new Nutrient(0, 0);
	
	private int maintenanceTicks;

	public NutritionData() {
		this.carb = new Nutrient(1000, 1000);
		this.protein = new Nutrient(1000, 1000);
		this.fat = new Nutrient(1000, 1000);
	}
	
	public Nutrient carbs() {
		return carb;
	}
	
	public Nutrient protein() {
		return protein;
	}
	
	public Nutrient fat() {
		return fat;
	}

	/**
	 * Handles the temperature game logic.
	 */
	@Override
	public void tick(Player player) {
		carb.tick(player);
		protein.tick(player);
		fat.tick(player);
		
		float proteinMod = 1;
		if (protein.level() > 2000) proteinMod = Survive.CONFIG.idle_protein_tick_rate_high;
		else if (protein.level() < 1000) proteinMod = Survive.CONFIG.idle_protein_tick_rate_low;
		
		maintenanceTicks++;
		if (maintenanceTicks > Survive.CONFIG.idle_protein_tick_rate * proteinMod) {
			protein.remove(1);
			maintenanceTicks = 0;
		}
	}

	/**
	 * Reads the water data for the player.
	 */
	public void read(CompoundTag compound) {
		if (compound.contains("carbLevel", 99)) {
			this.carb.level = new MutableInt(compound.getInt("carbLevel"));
			this.carb.timer = new MutableInt(compound.getInt("carbTimer"));
			this.carb.stack = new MutableFloat(compound.getFloat("carbStack"));
			
			this.protein.level = new MutableInt(compound.getInt("proteinLevel"));
			this.protein.timer = new MutableInt(compound.getInt("proteinTimer"));
			this.protein.stack = new MutableFloat(compound.getFloat("proteinStack"));
			
			this.fat.level = new MutableInt(compound.getInt("fatLevel"));
			this.fat.timer = new MutableInt(compound.getInt("fatTimer"));
			this.fat.stack = new MutableFloat(compound.getFloat("fatStack"));
			
			this.maintenanceTicks = compound.getInt("maintenanceTicks");
		}
	}

	/**
	 * Writes the water data for the player.
	 */
	public void write(CompoundTag compound, boolean reducedData) {
		compound.putInt("carbLevel", this.carb.level.getValue());
		if (!reducedData) {
			compound.putInt("carbTimer", this.carb.timer.getValue());
			compound.putFloat("carbStack", this.carb.stack.getValue());
		}
		
		compound.putInt("proteinLevel", this.protein.level());
		if (!reducedData) {
			compound.putInt("proteinTimer", this.protein.timer.getValue());
			compound.putFloat("proteinStack", this.protein.stack.getValue());
		}
		
		compound.putInt("fatLevel", this.fat.level());
		if (!reducedData) {
			compound.putInt("fatTimer", this.fat.timer.getValue());
			compound.putFloat("fatStack", this.fat.stack.getValue());
		}
		
		if (!reducedData) {
			compound.putInt("maintenanceTicks", this.maintenanceTicks);
		}
	}

	@Override
	public void save(LivingEntity player) {
	}

	@Override
	public boolean shouldTick() {
		return Survive.CONFIG.nutrition_enabled;
	}
}
