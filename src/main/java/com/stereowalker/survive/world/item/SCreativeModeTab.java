package com.stereowalker.survive.world.item;

import com.stereowalker.survive.Survive;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

public class SCreativeModeTab {
	public static final CreativeModeTab TAB_MAIN = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1).title(Component.translatable("itemGroup.survive")).icon(() -> {
		return new ItemStack(SItems.CANTEEN);
	   }).displayItems((x,output,z) ->{
			if (Survive.HYGIENE_CONFIG.enabled) {
				output.accept(HygieneItems.BATH_SPONGE);
				output.accept(HygieneItems.WHITE_WASHCLOTH);
				output.accept(HygieneItems.ORANGE_WASHCLOTH);
				output.accept(HygieneItems.MAGENTA_WASHCLOTH);
				output.accept(HygieneItems.LIGHT_BLUE_WASHCLOTH);
				output.accept(HygieneItems.YELLOW_WASHCLOTH);
				output.accept(HygieneItems.LIME_WASHCLOTH);
				output.accept(HygieneItems.PINK_WASHCLOTH);
				output.accept(HygieneItems.GRAY_WASHCLOTH);
				output.accept(HygieneItems.LIGHT_GRAY_WASHCLOTH);
				output.accept(HygieneItems.CYAN_WASHCLOTH);
				output.accept(HygieneItems.PURPLE_WASHCLOTH);
				output.accept(HygieneItems.BLUE_WASHCLOTH);
				output.accept(HygieneItems.BROWN_WASHCLOTH);
				output.accept(HygieneItems.GREEN_WASHCLOTH);
				output.accept(HygieneItems.RED_WASHCLOTH);
				output.accept(HygieneItems.BLACK_WASHCLOTH);
				output.accept(HygieneItems.WOOD_ASH);
				output.accept(HygieneItems.POTASH_SOLUTION);
				output.accept(HygieneItems.POTASH);
				output.accept(HygieneItems.ANIMAL_FAT);
				output.accept(HygieneItems.SOAP_MIX);
				output.accept(HygieneItems.SOAP_BOTTLE);
			}
			output.accept(SItems.WOOL_HAT);
			output.accept(SItems.WOOL_JACKET);
			output.accept(SItems.WOOL_PANTS);
			output.accept(SItems.WOOL_BOOTS);
			output.accept(SItems.STIFFENED_HONEY_HELMET);
			output.accept(SItems.STIFFENED_HONEY_CHESTPLATE);
			output.accept(SItems.STIFFENED_HONEY_LEGGINGS);
			output.accept(SItems.STIFFENED_HONEY_BOOTS);
			output.accept(SItems.CANTEEN);
			for(Potion potion : BuiltInRegistries.POTION) {
				if (potion != Potions.EMPTY) {
					output.accept(CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), Survive.THIRST_CONFIG.canteen_fill_amount, potion));
				}
			}
			output.accept(SItems.WATER_BOWL);
			output.accept(SItems.PURIFIED_WATER_BOWL);
			output.accept(SItems.ICE_CUBE);
			output.accept(SItems.THERMOMETER);
			output.accept(SItems.TEMPERATURE_REGULATOR);
			output.accept(SItems.LARGE_HEATING_PLATE);
			output.accept(SItems.LARGE_COOLING_PLATE);
			output.accept(SItems.MEDIUM_HEATING_PLATE);
			output.accept(SItems.MEDIUM_COOLING_PLATE);
			output.accept(SItems.SMALL_HEATING_PLATE);
			output.accept(SItems.SMALL_COOLING_PLATE);
			output.accept(SItems.CHARCOAL_FILTER);
			output.accept(SItems.PURIFIED_WATER_BUCKET);
			output.accept(SItems.MAGMA_PASTE);
		}).build();
}
