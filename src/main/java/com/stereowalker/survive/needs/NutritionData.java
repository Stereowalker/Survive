package com.stereowalker.survive.needs;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

import com.stereowalker.survive.Survive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class NutritionData extends SurviveData {
	public class Nutrient {
		private MutableInt level = new MutableInt(0);
		private MutableInt timer = new MutableInt(0);
		private MutableFloat stack = new MutableFloat(0);
		
		public Nutrient(int initialLevel) {
			level = new MutableInt(initialLevel);
		}
		
		public void add(float nut) {
			this.stack.add(Mth.clamp(nut, -1000, 3000));
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
	
	private MutableInt carbLevel = new MutableInt(0);
	private MutableInt carbTimer = new MutableInt(0);
	private MutableFloat carbStack = new MutableFloat(0);
	private Nutrient protein = new Nutrient(0);
	private Nutrient fat = new Nutrient(0);
	
	private int maintenanceTicks;

	public NutritionData() {
		this.carbLevel = new MutableInt(2000);
		this.protein = new Nutrient(2000);
		this.fat = new Nutrient(2000);
	}
	
	public Nutrient protein() {
		return protein;
	}
	
	public Nutrient fat() {
		return fat;
	}

	/**
	 * Add carbs.
	 */
	public void addCarbs(float carb) {
		this.carbStack.add(Mth.clamp(carb, -1000, 3000));
	}

	public void removeCarbs(int carbs) {
		this.carbLevel.subtract(carbs);
	}
	
	public void hand(Player player, MutableInt timer, MutableInt level, MutableFloat stack) {
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

	/**
	 * Handles the temperature game logic.
	 */
	@Override
	public void tick(Player player) {
		//Carbs
		hand(player, this.carbTimer, this.carbLevel, this.carbStack);
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
			this.carbLevel = new MutableInt(compound.getInt("carbLevel"));
			this.carbTimer = new MutableInt(compound.getInt("carbTimer"));
			this.carbStack = new MutableFloat(compound.getFloat("carbStack"));
			
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
		compound.putInt("carbLevel", this.carbLevel.getValue());
		if (!reducedData) {
			compound.putInt("carbTimer", this.carbTimer.getValue());
			compound.putFloat("carbStack", this.carbStack.getValue());
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

	/**
	 * Get the player's water level.
	 */
	public int getCarbLevel() {
		return this.carbLevel.intValue();
	}

	@Override
	public void save(LivingEntity player) {
	}

	@Override
	public boolean shouldTick() {
		return Survive.CONFIG.nutrition_enabled;
	}
}
