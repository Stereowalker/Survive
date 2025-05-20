package com.stereowalker.survive.world.item;

import java.util.List;

import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class SoapItem extends Item {
	public int soapEfficacy;
	public int soapMaxAmount;

	public SoapItem(Properties properties, int soapEfficacy, int soapMaxAmount) {
		super(properties);
		this.soapEfficacy = soapEfficacy;
		this.soapMaxAmount = soapMaxAmount;
	}

	public static ItemStack addPropertiesToSoap(ItemStack stack, int drinks) {
		SDataComponents.SOAP_LEFT_D.setData(stack, drinks);
		return stack;
	}

	@Override
	public ItemStack getDefaultInstance() {
		return addPropertiesToSoap(new ItemStack(this), this.soapMaxAmount);
	}
	
	public boolean isBottle() {
		return true;
	}

	public static int getSoapLeft(ItemStack stack) {
		return SDataComponents.SOAP_LEFT_D.getData(stack);
	}

	public static void setSoapLeft(ItemStack stack, int drinks) {
		if (stack.getItem() instanceof SoapItem)
			SDataComponents.SOAP_LEFT_D.setData(stack, Mth.clamp(drinks, 0, ((SoapItem)stack.getItem()).soapMaxAmount));
	}

	public static void decrementSoap(ItemStack stack) {
		setSoapLeft(stack, SDataComponents.SOAP_LEFT_D.getData(stack) - 1);
	}

	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
		pTooltipComponents.add(Component.translatable("tooltip.soap_left", SDataComponents.SOAP_LEFT_D.getData(pStack)).withStyle(ChatFormatting.AQUA));
	}

}
