package com.stereowalker.survive.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SItemGroup {
	public static final ItemGroup MAIN = (new ItemGroup("survive") {
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(SItems.CANTEEN);
		}	      
	});
}
