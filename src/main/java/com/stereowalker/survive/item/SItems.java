package com.stereowalker.survive.item;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.fluid.SFluids;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.registries.IForgeRegistry;

public class SItems {
	public static final List<Item> ITEMS = new ArrayList<Item>();
	//Miscellaneous
	public static final Item WOOL_HAT = register("wool_hat", new ArmorItem(SArmorMaterial.WOOL, EquipmentSlotType.HEAD, new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item WOOL_JACKET = register("wool_jacket", new ArmorItem(SArmorMaterial.WOOL, EquipmentSlotType.CHEST, new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item WOOL_PANTS = register("wool_pants", new ArmorItem(SArmorMaterial.WOOL, EquipmentSlotType.LEGS, new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item WOOL_BOOTS = register("wool_boots", new ArmorItem(SArmorMaterial.WOOL, EquipmentSlotType.FEET, new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item PURIFIED_WATER_BOTTLE = register("purified_water_bottle", new WaterBottleItem(new Item.Properties().group(SItemGroup.MAIN).containerItem(Items.GLASS_BOTTLE).maxStackSize(1)));
	public static final Item SUGAR_WATER_BOTTLE = register("sugar_water_bottle", new WaterBottleItem(new Item.Properties().group(SItemGroup.MAIN).maxStackSize(1)));
	public static final Item CANTEEN = register("canteen", new EmptyCanteenItem(new Item.Properties().group(SItemGroup.MAIN).maxStackSize(1)));
	public static final Item WATER_CANTEEN = register("water_canteen", new CanteenItem(new Item.Properties().containerItem(CANTEEN).maxStackSize(1).group(SItemGroup.MAIN)));
	public static final Item PURIFIED_WATER_CANTEEN = register("purified_water_canteen", new CanteenItem(new Item.Properties().containerItem(CANTEEN).maxStackSize(1).group(SItemGroup.MAIN)));
	public static final Item WATER_BOWL = register("water_bowl", new WaterBowlItem(new Item.Properties().group(SItemGroup.MAIN).maxStackSize(1)));
	public static final Item PURIFIED_WATER_BOWL = register("purified_water_bowl", new WaterBowlItem(new Item.Properties().group(SItemGroup.MAIN).maxStackSize(1)));
	public static final Item ICE_CUBE = register("ice_cube", new Item(new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item CHARCOAL_FILTER = register("charcoal_filter", new Item(new Item.Properties().group(SItemGroup.MAIN).maxStackSize(16)));
	public static final Item PURIFIED_WATER_BUCKET = register("purified_water_bucket", new BucketItem(() -> SFluids.PURIFIED_WATER, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(SItemGroup.MAIN)));
	public static final Item MAGMA_PASTE = register("magma_paste", new Item(new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item STIFFENED_HONEY = register("stiffened_honey", new Item(new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item STIFFENED_HONEY_HELMET = register("stiffened_honey_helmet", new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlotType.HEAD, new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item STIFFENED_HONEY_CHESTPLATE = register("stiffened_honey_chestplate", new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlotType.CHEST, new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item STIFFENED_HONEY_LEGGINGS = register("stiffened_honey_leggings", new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlotType.LEGS, new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item STIFFENED_HONEY_BOOTS = register("stiffened_honey_boots", new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlotType.FEET, new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item BATH_SPONGE = register("bath_sponge", new BodyCleaningItem(10, new Item.Properties().group(SItemGroup.MAIN).maxDamage(200)));
	public static final Item WHITE_WASHCLOTH = register("white_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item ORANGE_WASHCLOTH = register("orange_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item MAGENTA_WASHCLOTH = register("magenta_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item LIGHT_BLUE_WASHCLOTH = register("light_blue_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item YELLOW_WASHCLOTH = register("yellow_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item LIME_WASHCLOTH = register("lime_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item PINK_WASHCLOTH = register("pink_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item GRAY_WASHCLOTH = register("gray_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item LIGHT_GRAY_WASHCLOTH = register("light_gray_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item CYAN_WASHCLOTH = register("cyan_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item PURPLE_WASHCLOTH = register("purple_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item BLUE_WASHCLOTH = register("blue_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item BROWN_WASHCLOTH = register("brown_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item GREEN_WASHCLOTH = register("green_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item RED_WASHCLOTH = register("red_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item BLACK_WASHCLOTH = register("black_washcloth", new BodyCleaningItem(2, new Item.Properties().group(SItemGroup.MAIN).maxDamage(50)));
	public static final Item WOOD_ASH = register("wood_ash", new Item(new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item POTASH_SOLUTION = register("potash_solution", new Item(new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item POTASH = register("potash", new Item(new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item ANIMAL_FAT = register("animal_fat", new Item(new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item SOAP_MIX = register("soap_mix", new Item(new Item.Properties().group(SItemGroup.MAIN)));
	public static final Item SOAP_BOTTLE = register("soap_bottle", new SoapItem(new Item.Properties().group(SItemGroup.MAIN), 2, 20));
	
	
	//	private static Item register(Block block) {
	//		return register(new BlockItem(block, new Item.Properties()));
	//	}

//	private static Item register(Block block, ItemGroup itemGroup) {
//		return register(new BlockItem(block, (new Item.Properties()).group(itemGroup)));
//	}

//	private static Item register(BlockItem p_221543_0_) {
//		return register(p_221543_0_.getBlock(), p_221543_0_);
//	}

	protected static Item register(Block block, Item p_221546_1_) {
		return register(block.getRegistryName().getPath(), p_221546_1_);
	}

	private static Item register(String name, Item item) {
		item.setRegistryName(Survive.getInstance().location(name));
		SItems.ITEMS.add(item);
		return item;
	}

	public static void registerAll(IForgeRegistry<Item> registry) {
		for(Item item : ITEMS) {
			registry.register(item);
			Survive.getInstance().debug("Item: \""+item.getRegistryName().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Items Registered");
	}
}
