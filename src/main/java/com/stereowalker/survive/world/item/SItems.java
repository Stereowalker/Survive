package com.stereowalker.survive.world.item;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.unionlib.registries.RegistryHolder;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;

@RegistryHolder("survive")
public class SItems {
//	public static final List<Item> ITEMS = new ArrayList<Item>();
	//Miscellaneous
	@RegistryHolder("wool_hat")
	public static final Item WOOL_HAT = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.HEAD, new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("wool_jacket")
	public static final Item WOOL_JACKET = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.CHEST, new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("wool_pants")
	public static final Item WOOL_PANTS = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.LEGS, new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("wool_boots")
	public static final Item WOOL_BOOTS = new ArmorItem(SArmorMaterial.WOOL, EquipmentSlot.FEET, new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("purified_water_bottle")
	public static final Item PURIFIED_WATER_BOTTLE = new WaterBottleItem(new Item.Properties().tab(SItemGroup.MAIN).craftRemainder(Items.GLASS_BOTTLE).stacksTo(1));
	@RegistryHolder("sugar_water_bottle")
	public static final Item SUGAR_WATER_BOTTLE = new WaterBottleItem(new Item.Properties().tab(SItemGroup.MAIN).stacksTo(1));
	@RegistryHolder("canteen")
	public static final Item CANTEEN = new EmptyCanteenItem(new Item.Properties().tab(SItemGroup.MAIN).stacksTo(1));
	@RegistryHolder("water_canteen")
	public static final Item WATER_CANTEEN = new CanteenItem(new Item.Properties().craftRemainder(CANTEEN).stacksTo(1).tab(SItemGroup.MAIN), Fluids.FLOWING_WATER, Fluids.WATER);
	@RegistryHolder("purified_water_canteen")
	public static final Item PURIFIED_WATER_CANTEEN = new CanteenItem(new Item.Properties().craftRemainder(CANTEEN).stacksTo(1).tab(SItemGroup.MAIN), SFluids.FLOWING_PURIFIED_WATER, SFluids.PURIFIED_WATER);
	@RegistryHolder("water_bowl")
	public static final Item WATER_BOWL = new WaterBowlItem(new Item.Properties().tab(SItemGroup.MAIN).stacksTo(1));
	@RegistryHolder("purified_water_bowl")
	public static final Item PURIFIED_WATER_BOWL = new WaterBowlItem(new Item.Properties().tab(SItemGroup.MAIN).stacksTo(1));
	@RegistryHolder("ice_cube")
	public static final Item ICE_CUBE = new Item(new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("charcoal_filter")
	public static final Item CHARCOAL_FILTER = new Item(new Item.Properties().tab(SItemGroup.MAIN).stacksTo(16));
	@RegistryHolder("purified_water_bucket")
	public static final Item PURIFIED_WATER_BUCKET = new BucketItem(() -> SFluids.PURIFIED_WATER, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(SItemGroup.MAIN));
	@RegistryHolder("magma_paste")
	public static final Item MAGMA_PASTE = new Item(new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("stiffened_honey")
	public static final Item STIFFENED_HONEY = new Item(new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("stiffened_honey_helmet")
	public static final Item STIFFENED_HONEY_HELMET = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.HEAD, new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("stiffened_honey_chestplate")
	public static final Item STIFFENED_HONEY_CHESTPLATE = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.CHEST, new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("stiffened_honey_leggings")
	public static final Item STIFFENED_HONEY_LEGGINGS = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.LEGS, new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("stiffened_honey_boots")
	public static final Item STIFFENED_HONEY_BOOTS = new ArmorItem(SArmorMaterial.STIFFENED_HONEY, EquipmentSlot.FEET, new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("bath_sponge")
	public static final Item BATH_SPONGE = new BodyCleaningItem(10, new Item.Properties().tab(SItemGroup.MAIN).durability(200));
	@RegistryHolder("white_washcloth")
	public static final Item WHITE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("orange_washcloth")
	public static final Item ORANGE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("magenta_washcloth")
	public static final Item MAGENTA_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("light_blue_washcloth")
	public static final Item LIGHT_BLUE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("yellow_washcloth")
	public static final Item YELLOW_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("lime_washcloth")
	public static final Item LIME_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("pink_washcloth")
	public static final Item PINK_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("gray_washcloth")
	public static final Item GRAY_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("light_gray_washcloth")
	public static final Item LIGHT_GRAY_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("cyan_washcloth")
	public static final Item CYAN_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("purple_washcloth")
	public static final Item PURPLE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("blue_washcloth")
	public static final Item BLUE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("brown_washcloth")
	public static final Item BROWN_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("green_washcloth")
	public static final Item GREEN_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("red_washcloth")
	public static final Item RED_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("black_washcloth")
	public static final Item BLACK_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(SItemGroup.MAIN).durability(50));
	@RegistryHolder("wood_ash")
	public static final Item WOOD_ASH = new Item(new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("potash_solution")
	public static final Item POTASH_SOLUTION = new Item(new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("potash")
	public static final Item POTASH = new BoneMealItem(new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("animal_fat")
	public static final Item ANIMAL_FAT = new Item(new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("soap_mix")
	public static final Item SOAP_MIX = new Item(new Item.Properties().tab(SItemGroup.MAIN));
	@RegistryHolder("soap_bottle")
	public static final Item SOAP_BOTTLE = new SoapItem(new Item.Properties().tab(SItemGroup.MAIN), 2, 20);
	
	
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
