package com.stereowalker.survive.world.item;

import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@RegistryHolder(registry = Item.class)
public class HygieneItems {
	@RegistryObject("bath_sponge")
	public static final Item BATH_SPONGE = new BodyCleaningItem(10, new Item.Properties().durability(200));
	@RegistryObject("white_washcloth")
	public static final Item WHITE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("orange_washcloth")
	public static final Item ORANGE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("magenta_washcloth")
	public static final Item MAGENTA_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("light_blue_washcloth")
	public static final Item LIGHT_BLUE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("yellow_washcloth")
	public static final Item YELLOW_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("lime_washcloth")
	public static final Item LIME_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("pink_washcloth")
	public static final Item PINK_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("gray_washcloth")
	public static final Item GRAY_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("light_gray_washcloth")
	public static final Item LIGHT_GRAY_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("cyan_washcloth")
	public static final Item CYAN_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("purple_washcloth")
	public static final Item PURPLE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("blue_washcloth")
	public static final Item BLUE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("brown_washcloth")
	public static final Item BROWN_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("green_washcloth")
	public static final Item GREEN_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("red_washcloth")
	public static final Item RED_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("black_washcloth")
	public static final Item BLACK_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().durability(50));
	@RegistryObject("wood_ash")
	public static final Item WOOD_ASH = new Item(new Item.Properties());
	@RegistryObject("potash_solution")
	public static final Item POTASH_SOLUTION = new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE));
	@RegistryObject("potash")
	public static final Item POTASH = new BoneMealItem(new Item.Properties());
	@RegistryObject("animal_fat")
	public static final Item ANIMAL_FAT = new Item(new Item.Properties());
	@RegistryObject("soap_mix")
	public static final Item SOAP_MIX = new Item(new Item.Properties());
	@RegistryObject("soap_bottle")
	public static final Item SOAP_BOTTLE = new SoapItem(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE), 2, 20);
}
