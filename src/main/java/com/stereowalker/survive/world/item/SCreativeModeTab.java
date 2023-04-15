package com.stereowalker.survive.world.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class SCreativeModeTab {
	public static final CreativeModeTab TAB_MAIN = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1).title(Component.translatable("itemGroup.survive")).icon(() -> {
		return new ItemStack(SItems.CANTEEN);
	   }).displayItems((x,output,z) ->{
		}).build();
}
