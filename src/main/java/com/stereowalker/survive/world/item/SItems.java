package com.stereowalker.survive.world.item;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.survive.world.level.block.SBlocks;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateSize;
import com.stereowalker.survive.world.level.block.state.properties.TempRegulationPlateType;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;
import com.stereowalker.unionlib.util.VersionHelper;
import com.stereowalker.unionlib.world.item.equipment.ArmorMatReference.ArmorSegment;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SItems {
	@RegistryObject("wool_hat")
	public static final Item WOOL_HAT = VersionHelper.createGenericArmor(SArmorMaterials.WOOL, ArmorSegment.HEAD, VersionHelper.itemPropertyWithId("survive:wool_hat"));
	@RegistryObject("wool_jacket")
	public static final Item WOOL_JACKET = VersionHelper.createGenericArmor(SArmorMaterials.WOOL, ArmorSegment.CHEST, VersionHelper.itemPropertyWithId("survive:wool_jacket"));
	@RegistryObject("wool_pants")
	public static final Item WOOL_PANTS = VersionHelper.createGenericArmor(SArmorMaterials.WOOL, ArmorSegment.LEGS, VersionHelper.itemPropertyWithId("survive:wool_pants"));
	@RegistryObject("wool_boots")
	public static final Item WOOL_BOOTS = VersionHelper.createGenericArmor(SArmorMaterials.WOOL, ArmorSegment.FEET, VersionHelper.itemPropertyWithId("survive:wool_boots"));
	@RegistryObject("sugar_water_bottle")
	public static final Item SUGAR_WATER_BOTTLE = new WaterBottleItem(VersionHelper.itemPropertyWithId("survive:sugar_water_bottle").stacksTo(1));
	@RegistryObject("canteen")
	public static final Item CANTEEN = new EmptyCanteenItem(false, VersionHelper.itemPropertyWithId("survive:canteen").stacksTo(1));
	@RegistryObject("filled_canteen")
	public static final Item FILLED_CANTEEN = new CanteenItem(VersionHelper.itemPropertyWithId("survive:filled_canteen").craftRemainder(CANTEEN).stacksTo(1).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).component(SDataComponents.DRINKS_LEFT, 0), false);
	@RegistryObject("netherite_canteen")
	public static final Item NETHERITE_CANTEEN = new EmptyCanteenItem(true, VersionHelper.itemPropertyWithId("survive:netherite_canteen").stacksTo(1).fireResistant());
	@RegistryObject("filled_netherite_canteen")
	public static final Item FILLED_NETHERITE_CANTEEN = new CanteenItem(VersionHelper.itemPropertyWithId("survive:filled_netherite_canteen").craftRemainder(CANTEEN).stacksTo(1).fireResistant().component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).component(SDataComponents.DRINKS_LEFT, 0), true);
	@RegistryObject("water_bowl")
	public static final Item WATER_BOWL = new WaterBowlItem(VersionHelper.itemPropertyWithId("survive:water_bowl").stacksTo(1));
	@RegistryObject("purified_water_bowl")
	public static final Item PURIFIED_WATER_BOWL = new WaterBowlItem(VersionHelper.itemPropertyWithId("survive:purified_water_bowl").stacksTo(1));
	@RegistryObject("ice_cube")
	public static final Item ICE_CUBE = new Item(VersionHelper.itemPropertyWithId("survive:ice_cube"));
	@RegistryObject("thermometer")
	public static final Item THERMOMETER = new Item((VersionHelper.itemPropertyWithId("survive:thermometer")));
	@RegistryObject("temperature_regulator")
	public static final Item TEMPERATURE_REGULATOR = new BlockItem(SBlocks.TEMPERATURE_REGULATOR, (VersionHelper.itemPropertyWithId("survive:temperature_regulator"))) {
		@Override
		public void registerBlocks(java.util.Map<Block, Item> map, Item self) {
			super.registerBlocks(map, self);
			map.put(SBlocks.PLATED_TEMPERATURE_REGULATOR, self);
		}
	};
	@RegistryObject("large_heating_plate")
	public static final Item LARGE_HEATING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.HEATER, TempRegulationPlateSize.LARGE, VersionHelper.itemPropertyWithId("survive:large_heating_plate").stacksTo(64));
	@RegistryObject("large_cooling_plate")
	public static final Item LARGE_COOLING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.CHILLER, TempRegulationPlateSize.LARGE, VersionHelper.itemPropertyWithId("survive:large_cooling_plate").stacksTo(64));
	@RegistryObject("medium_heating_plate")
	public static final Item MEDIUM_HEATING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.HEATER, TempRegulationPlateSize.MEDIUM, VersionHelper.itemPropertyWithId("survive:medium_heating_plate").stacksTo(64));
	@RegistryObject("medium_cooling_plate")
	public static final Item MEDIUM_COOLING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.CHILLER, TempRegulationPlateSize.MEDIUM, VersionHelper.itemPropertyWithId("survive:medium_cooling_plate").stacksTo(64));
	@RegistryObject("small_heating_plate")
	public static final Item SMALL_HEATING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.HEATER, TempRegulationPlateSize.SMALL, VersionHelper.itemPropertyWithId("survive:small_heating_plate").stacksTo(64));
	@RegistryObject("small_cooling_plate")
	public static final Item SMALL_COOLING_PLATE = new TemperatureRegulatorPlateItem(TempRegulationPlateType.CHILLER, TempRegulationPlateSize.SMALL, VersionHelper.itemPropertyWithId("survive:small_cooling_plate").stacksTo(64));
	@RegistryObject("used_charcoal_filter")
	public static final Item USED_CHARCOAL_FILTER = new Item(VersionHelper.itemPropertyWithId("survive:used_charcoal_filter").durability(10));
	@RegistryObject("charcoal_filter")
	public static final Item CHARCOAL_FILTER = new Item(VersionHelper.itemPropertyWithId("survive:charcoal_filter").stacksTo(32));
	@RegistryObject("purified_water_bucket")
	public static final Item PURIFIED_WATER_BUCKET = new BucketItem(() -> SFluids.PURIFIED_WATER, (VersionHelper.itemPropertyWithId("survive:purified_water_bucket")).craftRemainder(Items.BUCKET).stacksTo(1));
	@RegistryObject("magma_paste")
	public static final Item MAGMA_PASTE = new Item(VersionHelper.itemPropertyWithId("survive:magma_paste"));
	@RegistryObject("stiffened_honey")
	public static final Item STIFFENED_HONEY = new Item(VersionHelper.itemPropertyWithId("survive:stiffened_honey"));
	@RegistryObject("stiffened_honey_helmet")
	public static final Item STIFFENED_HONEY_HELMET = VersionHelper.createGenericArmor(SArmorMaterials.STIFFENED_HONEY, ArmorSegment.HEAD, VersionHelper.itemPropertyWithId("survive:stiffened_honey_helmet"));
	@RegistryObject("stiffened_honey_chestplate")
	public static final Item STIFFENED_HONEY_CHESTPLATE = VersionHelper.createGenericArmor(SArmorMaterials.STIFFENED_HONEY, ArmorSegment.CHEST, VersionHelper.itemPropertyWithId("survive:stiffened_honey_chestplate"));
	@RegistryObject("stiffened_honey_leggings")
	public static final Item STIFFENED_HONEY_LEGGINGS = VersionHelper.createGenericArmor(SArmorMaterials.STIFFENED_HONEY, ArmorSegment.LEGS, VersionHelper.itemPropertyWithId("survive:stiffened_honey_leggings"));
	@RegistryObject("stiffened_honey_boots")
	public static final Item STIFFENED_HONEY_BOOTS = VersionHelper.createGenericArmor(SArmorMaterials.STIFFENED_HONEY, ArmorSegment.FEET, VersionHelper.itemPropertyWithId("survive:stiffened_honey_boots"));
	@RegistryObject("sea_salt")
	public static final Item SEA_SALT = new Item(VersionHelper.itemPropertyWithId("survive:sea_salt"));
	@RegistryObject("salt_box")
	public static final Item SALT_BOX = new BlockItem(SBlocks.SALT_BOX, (VersionHelper.itemPropertyWithId("survive:salt_box")));
	@RegistryObject("realistic_campfire")
	public static final Item REALISTIC_CAMPFIRE = new BlockItem(SBlocks.REALISTIC_CAMPFIRE, VersionHelper.itemPropertyWithId("survive:realistic_campfire").component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    
}
