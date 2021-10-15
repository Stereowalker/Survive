package com.stereowalker.survive.world.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SItemGroup {
	public static final CreativeModeTab MAIN = (new CreativeModeTab("survive") {
		@OnlyIn(Dist.CLIENT)
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(SItems.CANTEEN);
		}	      
	});
}
