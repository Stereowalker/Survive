package com.stereowalker.survive.world.item;

import java.util.Optional;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.registries.RegistryHolder;

import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

@RegistryHolder("survive")
public class HygieneItems {
	@RegistryHolder("bath_sponge")
	public static final Item BATH_SPONGE = new BodyCleaningItem(10, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(200));
	@RegistryHolder("white_washcloth")
	public static final Item WHITE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("orange_washcloth")
	public static final Item ORANGE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("magenta_washcloth")
	public static final Item MAGENTA_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("light_blue_washcloth")
	public static final Item LIGHT_BLUE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("yellow_washcloth")
	public static final Item YELLOW_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("lime_washcloth")
	public static final Item LIME_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("pink_washcloth")
	public static final Item PINK_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("gray_washcloth")
	public static final Item GRAY_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("light_gray_washcloth")
	public static final Item LIGHT_GRAY_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("cyan_washcloth")
	public static final Item CYAN_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("purple_washcloth")
	public static final Item PURPLE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("blue_washcloth")
	public static final Item BLUE_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("brown_washcloth")
	public static final Item BROWN_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("green_washcloth")
	public static final Item GREEN_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("red_washcloth")
	public static final Item RED_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("black_washcloth")
	public static final Item BLACK_WASHCLOTH = new BodyCleaningItem(2, new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)).durability(50));
	@RegistryHolder("wood_ash")
	public static final Item WOOD_ASH = new Item(new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)));
	@RegistryHolder("potash_solution")
	public static final Item POTASH_SOLUTION = new Item(new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)));
	@RegistryHolder("potash")
	public static final Item POTASH = new BoneMealItem(new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)));
	@RegistryHolder("animal_fat")
	public static final Item ANIMAL_FAT = new Item(new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)));
	@RegistryHolder("soap_mix")
	public static final Item SOAP_MIX = new Item(new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)));
	@RegistryHolder("soap_bottle")
	public static final Item SOAP_BOTTLE = new SoapItem(new Item.Properties().tab(moduleEnabled(SCreativeModeTab.TAB_MAIN).orElse((CreativeModeTab)null)), 2, 20);

	private static <T> Optional<T> moduleEnabled(T pSecondaryPart) {
		return !Survive.HYGIENE_CONFIG.enabled ? Optional.of(pSecondaryPart) : Optional.empty();
	}
}
