package com.stereowalker.survive.world.item;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.unionlib.registries.RegistryHolder;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;

@RegistryHolder("survive")
public class SItems {
	//Miscellaneous
	@RegistryHolder("wool_hat")
	public static final Item WOOL_HAT = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.HEAD, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("wool_jacket")
	public static final Item WOOL_JACKET = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.CHEST, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("wool_pants")
	public static final Item WOOL_PANTS = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.LEGS, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("wool_boots")
	public static final Item WOOL_BOOTS = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.FEET, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("purified_water_bottle")
	public static final Item PURIFIED_WATER_BOTTLE = new WaterBottleItem(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).craftRemainder(Items.GLASS_BOTTLE).stacksTo(1));
	@RegistryHolder("sugar_water_bottle")
	public static final Item SUGAR_WATER_BOTTLE = new WaterBottleItem(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(1));
	@RegistryHolder("canteen")
	public static final Item CANTEEN = new EmptyCanteenItem(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(1));
	@RegistryHolder("water_canteen")
	public static final Item WATER_CANTEEN = new CanteenItem(new Item.Properties().craftRemainder(CANTEEN).stacksTo(1).tab(SCreativeModeTab.TAB_MAIN), Fluids.FLOWING_WATER, Fluids.WATER);
	@RegistryHolder("purified_water_canteen")
	public static final Item PURIFIED_WATER_CANTEEN = new CanteenItem(new Item.Properties().craftRemainder(CANTEEN).stacksTo(1).tab(SCreativeModeTab.TAB_MAIN), SFluids.FLOWING_PURIFIED_WATER, SFluids.PURIFIED_WATER);
	@RegistryHolder("water_bowl")
	public static final Item WATER_BOWL = new WaterBowlItem(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(1));
	@RegistryHolder("purified_water_bowl")
	public static final Item PURIFIED_WATER_BOWL = new WaterBowlItem(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).stacksTo(1));
	@RegistryHolder("ice_cube")
	public static final Item ICE_CUBE = new Item(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("thermometer")
	public static final Item THERMOMETER = new Item((new Item.Properties()).tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("charcoal_filter")
	public static final Item CHARCOAL_FILTER = new Item(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN).defaultDurability(10));
	@RegistryHolder("purified_water_bucket")
	public static final Item PURIFIED_WATER_BUCKET = new BucketItem(() -> SFluids.PURIFIED_WATER, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("magma_paste")
	public static final Item MAGMA_PASTE = new Item(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("stiffened_honey")
	public static final Item STIFFENED_HONEY = new Item(new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("stiffened_honey_helmet")
	public static final Item STIFFENED_HONEY_HELMET = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.HEAD, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("stiffened_honey_chestplate")
	public static final Item STIFFENED_HONEY_CHESTPLATE = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.CHEST, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("stiffened_honey_leggings")
	public static final Item STIFFENED_HONEY_LEGGINGS = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.LEGS, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	@RegistryHolder("stiffened_honey_boots")
	public static final Item STIFFENED_HONEY_BOOTS = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.FEET, new Item.Properties().tab(SCreativeModeTab.TAB_MAIN));
	
	
	//	private static Item register(Block block) {
	//		return register(new BlockItem(block, new Item.Properties()));
	//	}

//	private static Item register(Block block, ItemGroup itemGroup) {
//		return register(new BlockItem(block, (new Item.Properties()).tab(itemGroup)));
//	}

//	private static Item register(BlockItem p_221543_0_) {
//		return register(p_221543_0_.getBlock(), p_221543_0_);
//	}

	protected static Item register(Block block, Item p_221546_1_) {
		return register(block.getRegistryName().getPath(), p_221546_1_);
	}

	private static Item register(String name, Item item) {
		item.setRegistryName(Survive.getInstance().location(name));
//		SItems.ITEMS.add(item);
		return item;
	}

//	public static void registerAll(IForgeRegistry<Item> registry) {
//		for(Item item : ITEMS) {
//			registry.register(item);
//			Survive.getInstance().debug("Item: \""+item.getRegistryName().toString()+"\" registered");
//		}
//		Survive.getInstance().debug("All Items Registered");
//	}
}
