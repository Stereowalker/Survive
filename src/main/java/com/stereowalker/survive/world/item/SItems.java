package com.stereowalker.survive.world.item;

import com.stereowalker.survive.world.level.block.SBlocks;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateSize;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateType;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;

@RegistryHolder(registry = Item.class)
public class SItems {
	@RegistryObject("wool_hat")
	public static final Item WOOL_HAT = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.HEAD, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("wool_jacket")
	public static final Item WOOL_JACKET = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.CHEST, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("wool_pants")
	public static final Item WOOL_PANTS = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.LEGS, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("wool_boots")
	public static final Item WOOL_BOOTS = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.FEET, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("purified_water_bottle")
	public static final Item PURIFIED_WATER_BOTTLE = new WaterBottleItem(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).craftRemainder(Items.GLASS_BOTTLE).stacksTo(1));
	@RegistryObject("sugar_water_bottle")
	public static final Item SUGAR_WATER_BOTTLE = new WaterBottleItem(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(1));
	@RegistryObject("canteen")
	public static final Item CANTEEN = new EmptyCanteenItem(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(1));
	@RegistryObject("water_canteen")
	public static final Item WATER_CANTEEN = new CanteenItem(new Item.Properties().craftRemainder(CANTEEN).stacksTo(1).tab(SCreativeModeTab.TAB_MAIN), Fluids.FLOWING_WATER, Fluids.WATER);
	@RegistryObject("purified_water_canteen")
	public static final Item PURIFIED_WATER_CANTEEN = new CanteenItem(new Item.Properties().craftRemainder(CANTEEN).stacksTo(1).tab(SCreativeModeTab.TAB_MAIN), SFluids.FLOWING_PURIFIED_WATER, SFluids.PURIFIED_WATER);
	@RegistryObject("water_bowl")
	public static final Item WATER_BOWL = new WaterBowlItem(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(1));
	@RegistryObject("purified_water_bowl")
	public static final Item PURIFIED_WATER_BOWL = new WaterBowlItem(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(1));
	@RegistryObject("ice_cube")
	public static final Item ICE_CUBE = new Item(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("thermometer")
	public static final Item THERMOMETER = new Item((new Item.Properties()).tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("temperature_regulator")
	public static final Item TEMPERATURE_REGULATOR = new BlockItem(SBlocks.TEMPERATURE_REGULATOR, (new Item.Properties()).tab(SCreativeModeTab.TAB_MAIN)) {
		@Override
		public void registerBlocks(java.util.Map<Block, Item> map, Item self) {
			super.registerBlocks(map, self);
			map.put(SBlocks.PLATED_TEMPERATURE_REGULATOR, self);
		}

		@Override
		public void removeFromBlockToItemMap(java.util.Map<Block, Item> map, Item self) {
			super.removeFromBlockToItemMap(map, self);
			map.remove(SBlocks.PLATED_TEMPERATURE_REGULATOR);
		}
	};
	@RegistryObject("large_heating_plate")
	public static final Item LARGE_HEATING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.HEATER, TempRegulationPlateSize.LARGE, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(64));
	@RegistryObject("large_cooling_plate")
	public static final Item LARGE_COOLING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.CHILLER, TempRegulationPlateSize.LARGE, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(64));
	@RegistryObject("medium_heating_plate")
	public static final Item MEDIUM_HEATING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.HEATER, TempRegulationPlateSize.MEDIUM, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(64));
	@RegistryObject("medium_cooling_plate")
	public static final Item MEDIUM_COOLING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.CHILLER, TempRegulationPlateSize.MEDIUM, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(64));
	@RegistryObject("small_heating_plate")
	public static final Item SMALL_HEATING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.HEATER, TempRegulationPlateSize.SMALL, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(64));
	@RegistryObject("small_cooling_plate")
	public static final Item SMALL_COOLING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.CHILLER, TempRegulationPlateSize.SMALL, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(64));
	@RegistryObject("charcoal_filter")
	public static final Item CHARCOAL_FILTER = new Item(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).defaultDurability(10));
	@RegistryObject("purified_water_bucket")
	public static final Item PURIFIED_WATER_BUCKET = new BucketItem(() -> SFluids.PURIFIED_WATER, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("magma_paste")
	public static final Item MAGMA_PASTE = new Item(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("stiffened_honey")
	public static final Item STIFFENED_HONEY = new Item(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("stiffened_honey_helmet")
	public static final Item STIFFENED_HONEY_HELMET = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.HEAD, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("stiffened_honey_chestplate")
	public static final Item STIFFENED_HONEY_CHESTPLATE = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.CHEST, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("stiffened_honey_leggings")
	public static final Item STIFFENED_HONEY_LEGGINGS = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.LEGS, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryObject("stiffened_honey_boots")
	public static final Item STIFFENED_HONEY_BOOTS = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.FEET, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
}
