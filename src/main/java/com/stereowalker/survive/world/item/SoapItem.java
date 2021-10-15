package com.stereowalker.survive.world.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoapItem extends Item {
	public int soapEfficacy;
	public int soapMaxAmount;

	public SoapItem(Properties properties, int soapEfficacy, int soapMaxAmount) {
		super(properties);
		this.soapEfficacy = soapEfficacy;
		this.soapMaxAmount = soapMaxAmount;
	}

	public static CompoundTag soapTag(int soap) {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("SoapLeft", soap);
		return nbt;
	}

	public static ItemStack addPropertiesToSoap(ItemStack stack, int drinks) {
		stack.setTag(soapTag(drinks));
		return stack;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ItemStack getDefaultInstance() {
		return addPropertiesToSoap(new ItemStack(this), this.soapMaxAmount);
	}
	
	public boolean isBottle() {
		return true;
	}

	public static int getSoapLeft(ItemStack stack) {
		return stack.getOrCreateTag().getInt("SoapLeft");
	}

	public static void setSoapLeft(ItemStack stack, int drinks) {
		if (stack.getItem() instanceof SoapItem)
			stack.getOrCreateTag().putInt("SoapLeft", Mth.clamp(drinks, 0, ((SoapItem)stack.getItem()).soapMaxAmount));
	}

	public static void decrementSoap(ItemStack stack) {
		setSoapLeft(stack, getSoapLeft(stack) - 1);
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return new ItemStack(Items.GLASS_BOTTLE);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(new TranslatableComponent("tooltip.soap_left", getSoapLeft(stack)).withStyle(ChatFormatting.AQUA));
	}

}
