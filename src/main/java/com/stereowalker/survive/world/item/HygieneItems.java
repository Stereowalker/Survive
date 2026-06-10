package com.stereowalker.survive.world.item;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@RegistryHolder(namespace = Survive.MOD_ID)
public class HygieneItems {
	@RegistryObject("used_bath_sponge")
	public static final Item USED_BATH_SPONGE = new BodyCleaningItem(10, VersionHelper.itemPropertyWithId("survive:used_bath_sponge").durability(200));
	@RegistryObject("bath_sponge")
	public static final Item BATH_SPONGE = new ConvertOnUseItem(USED_BATH_SPONGE, VersionHelper.itemPropertyWithId("survive:bath_sponge").stacksTo(16));
	@RegistryObject("white_washcloth")
	public static final Item WHITE_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:white_washcloth").durability(50));
	@RegistryObject("orange_washcloth")
	public static final Item ORANGE_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:orange_washcloth").durability(50));
	@RegistryObject("magenta_washcloth")
	public static final Item MAGENTA_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:magenta_washcloth").durability(50));
	@RegistryObject("light_blue_washcloth")
	public static final Item LIGHT_BLUE_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:light_blue_washcloth").durability(50));
	@RegistryObject("yellow_washcloth")
	public static final Item YELLOW_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:yellow_washcloth").durability(50));
	@RegistryObject("lime_washcloth")
	public static final Item LIME_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:lime_washcloth").durability(50));
	@RegistryObject("pink_washcloth")
	public static final Item PINK_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:pink_washcloth").durability(50));
	@RegistryObject("gray_washcloth")
	public static final Item GRAY_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:gray_washcloth").durability(50));
	@RegistryObject("light_gray_washcloth")
	public static final Item LIGHT_GRAY_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:light_gray_washcloth").durability(50));
	@RegistryObject("cyan_washcloth")
	public static final Item CYAN_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:cyan_washcloth").durability(50));
	@RegistryObject("purple_washcloth")
	public static final Item PURPLE_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:purple_washcloth").durability(50));
	@RegistryObject("blue_washcloth")
	public static final Item BLUE_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:blue_washcloth").durability(50));
	@RegistryObject("brown_washcloth")
	public static final Item BROWN_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:brown_washcloth").durability(50));
	@RegistryObject("green_washcloth")
	public static final Item GREEN_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:green_washcloth").durability(50));
	@RegistryObject("red_washcloth")
	public static final Item RED_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:red_washcloth").durability(50));
	@RegistryObject("black_washcloth")
	public static final Item BLACK_WASHCLOTH = new BodyCleaningItem(2, VersionHelper.itemPropertyWithId("survive:black_washcloth").durability(50));
	@RegistryObject("wood_ash")
	public static final Item WOOD_ASH = new Item(VersionHelper.itemPropertyWithId("survive:wood_ash"));
	@RegistryObject("potash_solution")
	public static final Item POTASH_SOLUTION = new Item(VersionHelper.itemPropertyWithId("survive:potash_solution").craftRemainder(Items.GLASS_BOTTLE));
	@RegistryObject("potash")
	public static final Item POTASH = new BoneMealItem(VersionHelper.itemPropertyWithId("survive:potash"));
	@RegistryObject("animal_fat")
	public static final Item ANIMAL_FAT = new Item(VersionHelper.itemPropertyWithId("survive:animal_fat"));
	@RegistryObject("soap_mix")
	public static final Item SOAP_MIX = new Item(VersionHelper.itemPropertyWithId("survive:soap_mix"));
	@RegistryObject("soap_bottle")
	public static final Item SOAP_BOTTLE = new SoapItem(VersionHelper.itemPropertyWithId("survive:soap_bottle").craftRemainder(Items.GLASS_BOTTLE).component(SDataComponents.SOAP_LEFT, 20), 2, 20);
}
