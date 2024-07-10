package com.stereowalker.survive.world.item;

import java.util.List;

import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

	public static ItemStack addPropertiesToSoap(ItemStack stack, int drinks) {
		stack.set(SDataComponents.SOAP_LEFT, drinks);
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
		return stack.get(SDataComponents.SOAP_LEFT);
	}

	public static void setSoapLeft(ItemStack stack, int drinks) {
		if (stack.getItem() instanceof SoapItem)
			stack.set(SDataComponents.SOAP_LEFT, Mth.clamp(drinks, 0, ((SoapItem)stack.getItem()).soapMaxAmount));
	}

	public static void decrementSoap(ItemStack stack) {
		setSoapLeft(stack, stack.get(SDataComponents.SOAP_LEFT) - 1);
	}

	@Override
	public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
		pTooltipComponents.add(Component.translatable("tooltip.soap_left", pStack.get(SDataComponents.SOAP_LEFT)).withStyle(ChatFormatting.AQUA));
	}

}
