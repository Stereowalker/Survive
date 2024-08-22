package com.stereowalker.survive.world.item;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.survive.world.level.block.SBlocks;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateSize;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateType;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SItems {
	@RegistryObject("wool_hat")
	public static final Item WOOL_HAT = new ArmorItem(SArmorMaterials.WOOL.holder(), ArmorItem.Type.HELMET, new Item.Properties());
	@RegistryObject("wool_jacket")
	public static final Item WOOL_JACKET = new ArmorItem(SArmorMaterials.WOOL.holder(), ArmorItem.Type.CHESTPLATE, new Item.Properties());
	@RegistryObject("wool_pants")
	public static final Item WOOL_PANTS = new ArmorItem(SArmorMaterials.WOOL.holder(), ArmorItem.Type.LEGGINGS, new Item.Properties());
	@RegistryObject("wool_boots")
	public static final Item WOOL_BOOTS = new ArmorItem(SArmorMaterials.WOOL.holder(), ArmorItem.Type.BOOTS, new Item.Properties());
	@RegistryObject("sugar_water_bottle")
	public static final Item SUGAR_WATER_BOTTLE = new WaterBottleItem(new Item.Properties().stacksTo(1));
	@RegistryObject("canteen")
	public static final Item CANTEEN = new EmptyCanteenItem(false, new Item.Properties().stacksTo(1));
	@RegistryObject("filled_canteen")
	public static final Item FILLED_CANTEEN = new CanteenItem(new Item.Properties().craftRemainder(CANTEEN).stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).component(SDataComponents.DRINKS_LEFT, 0), false);
	@RegistryObject("netherite_canteen")
	public static final Item NETHERITE_CANTEEN = new EmptyCanteenItem(true, new Item.Properties().stacksTo(1).fireResistant());
	@RegistryObject("filled_netherite_canteen")
	public static final Item FILLED_NETHERITE_CANTEEN = new CanteenItem(new Item.Properties().craftRemainder(CANTEEN).stacksTo(1).fireResistant().component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).component(SDataComponents.DRINKS_LEFT, 0), true);
	@RegistryObject("water_bowl")
	public static final Item WATER_BOWL = new WaterBowlItem(new Item.Properties().stacksTo(1));
	@RegistryObject("purified_water_bowl")
	public static final Item PURIFIED_WATER_BOWL = new WaterBowlItem(new Item.Properties().stacksTo(1));
	@RegistryObject("ice_cube")
	public static final Item ICE_CUBE = new Item(new Item.Properties());
	@RegistryObject("thermometer")
	public static final Item THERMOMETER = new Item((new Item.Properties()));
	@RegistryObject("temperature_regulator")
	public static final Item TEMPERATURE_REGULATOR = new BlockItem(SBlocks.TEMPERATURE_REGULATOR, (new Item.Properties())) {
		@Override
		public void registerBlocks(java.util.Map<Block, Item> map, Item self) {
			super.registerBlocks(map, self);
			map.put(SBlocks.PLATED_TEMPERATURE_REGULATOR, self);
		}
	};
	@RegistryObject("large_heating_plate")
	public static final Item LARGE_HEATING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.HEATER, TempRegulationPlateSize.LARGE, new Item.Properties().stacksTo(64));
	@RegistryObject("large_cooling_plate")
	public static final Item LARGE_COOLING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.CHILLER, TempRegulationPlateSize.LARGE, new Item.Properties().stacksTo(64));
	@RegistryObject("medium_heating_plate")
	public static final Item MEDIUM_HEATING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.HEATER, TempRegulationPlateSize.MEDIUM, new Item.Properties().stacksTo(64));
	@RegistryObject("medium_cooling_plate")
	public static final Item MEDIUM_COOLING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.CHILLER, TempRegulationPlateSize.MEDIUM, new Item.Properties().stacksTo(64));
	@RegistryObject("small_heating_plate")
	public static final Item SMALL_HEATING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.HEATER, TempRegulationPlateSize.SMALL, new Item.Properties().stacksTo(64));
	@RegistryObject("small_cooling_plate")
	public static final Item SMALL_COOLING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.CHILLER, TempRegulationPlateSize.SMALL, new Item.Properties().stacksTo(64));
	@RegistryObject("used_charcoal_filter")
	public static final Item USED_CHARCOAL_FILTER = new Item(new Item.Properties().durability(10));
	@RegistryObject("charcoal_filter")
	public static final Item CHARCOAL_FILTER = new Item(new Item.Properties().stacksTo(32));
	@RegistryObject("purified_water_bucket")
	public static final Item PURIFIED_WATER_BUCKET = new BucketItem(() -> SFluids.PURIFIED_WATER, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1));
	@RegistryObject("magma_paste")
	public static final Item MAGMA_PASTE = new Item(new Item.Properties());
	@RegistryObject("stiffened_honey")
	public static final Item STIFFENED_HONEY = new Item(new Item.Properties());
	@RegistryObject("stiffened_honey_helmet")
	public static final Item STIFFENED_HONEY_HELMET = new ArmorItem(SArmorMaterials.STIFFENED_HONEY.holder(), ArmorItem.Type.HELMET, new Item.Properties());
	@RegistryObject("stiffened_honey_chestplate")
	public static final Item STIFFENED_HONEY_CHESTPLATE = new ArmorItem(SArmorMaterials.STIFFENED_HONEY.holder(), ArmorItem.Type.CHESTPLATE, new Item.Properties());
	@RegistryObject("stiffened_honey_leggings")
	public static final Item STIFFENED_HONEY_LEGGINGS = new ArmorItem(SArmorMaterials.STIFFENED_HONEY.holder(), ArmorItem.Type.LEGGINGS, new Item.Properties());
	@RegistryObject("stiffened_honey_boots")
	public static final Item STIFFENED_HONEY_BOOTS = new ArmorItem(SArmorMaterials.STIFFENED_HONEY.holder(), ArmorItem.Type.BOOTS, new Item.Properties());
}
