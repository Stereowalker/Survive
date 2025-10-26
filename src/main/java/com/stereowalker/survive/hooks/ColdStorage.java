package com.stereowalker.survive.hooks;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.stereowalker.survive.FoodUtils;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.unionlib.util.VersionHelper.VanillaComponents;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public interface ColdStorage {
	public float coldness();
	public float maxColdness();
	public void setColdness(float value);
	public long lastAccessed();
	public void setLastAccessed(long lastAccessed);
	public float lossFactor();
	public int slotCount();
	public ItemStack get(int i);
	public void set(int i, ItemStack stack);
	
	default ContainerData data() {
		return new ContainerData() {
			@Override
			public void set(int pIndex, int pValue) {
				if (pIndex == 0) setColdness(pValue);
			}
			
			@Override
			public int getCount() { return 2; }
			
			@Override
			public int get(int pIndex) {
				if (pIndex == 0) return Mth.ceil(coldness());
				if (pIndex == 1) return Mth.ceil(maxColdness());
				return 0;
			}
		};
	}
	
	default void decrementColdness(long by) {
		setColdness(coldness()-(lossFactor()*by));
		if (coldness() < 0) setColdness(0);
	}
	
	default float preservatonEfficiency() {
		return 1f - lossFactor();
	}
	
	default void load2(CompoundTag pTag/*, HolderLookup.Provider pRegistries*/) {
		setColdness(pTag.getFloat("coldness"));
		setLastAccessed(pTag.getLong("lastAccessed"));
    }
	
	default void save(CompoundTag pTag/*, HolderLookup.Provider pRegistries*/) {
		pTag.putFloat("coldness", coldness());
		pTag.putLong("lastAccessed", lastAccessed());
    }
	
	default void coldTick(Level pLevel) {
		if (Survive.FOOD_CONFIG.enabled) {
			Map<Item,Float> COOLNESS = new ImmutableMap.Builder<Item,Float>()
					.put(SItems.ICE_CUBE, 38f)
					.put(Items.ICE, 342f).build();
			long gameTime = pLevel.getGameTime();
			if (lastAccessed() == 0) setLastAccessed(gameTime);
			long timeSinceLastOpened = gameTime - lastAccessed();

			int foodAmount = 0;
			for (int i = 0; i < slotCount(); i++) {
				ItemStack stack = get(i);
				if (VanillaComponents.FOOD.hasData(stack)) {
					FoodUtils.giveLifespanToFood(stack, gameTime);
					foodAmount += stack.getCount();
				}
			}
			
			float coldnessValue = coldness();
			if (foodAmount > 0) {
				for (int i = 0; i < slotCount(); i++) {
					if (coldnessValue >= maxColdness()) break;
					float remainingColdness = maxColdness() - coldnessValue;
					ItemStack stack = get(i);
					if (COOLNESS.containsKey(stack.getItem())) {
						int take = Math.min(Mth.floor(remainingColdness / COOLNESS.get(stack.getItem())), stack.getCount());
						coldnessValue += COOLNESS.get(stack.getItem()) * take;
						stack.shrink(take);
					}
					if (stack.getItem() == Items.PACKED_ICE) {
						int take = Math.min(Mth.floor(remainingColdness / 3078f), stack.getCount());
						coldnessValue += 3078f * take;
						stack.shrink(take);
					}
					if (stack.getItem() == Items.BLUE_ICE) {
						int take = Math.min(Mth.floor(remainingColdness / 27702f), stack.getCount());
						coldnessValue += 27702f * take;
						stack.shrink(take);
					}
				}
				setColdness(coldnessValue);
			}
			
//			System.out.println(lastAccessed()+" Time Diff is "+timeSinceLastOpened+" and this block is "+coldness()+" much cold ");
			setLastAccessed(gameTime);
			if (coldness() > 0) {
				float efficiency = timeSinceLastOpened * preservatonEfficiency();
				if (timeSinceLastOpened <= 2) efficiency = 1;
//				System.out.println("We Lost "+(timeSinceLastOpened - efficiency)+" efficiency of "+(preservatonEfficiency()*100)+"%");
				for (int i = 0; i < slotCount(); i++) {
					ItemStack stack = get(i);
					if (SDataComponents.FOOD_STATUS_D.hasData(stack)) {
						final float eff = efficiency;
						SDataComponents.FOOD_STATUS_D.editData(stack, food_status -> food_status.extendTime(eff));
					}
				}
				decrementColdness(timeSinceLastOpened * foodAmount);
			}
		}
	}
}
