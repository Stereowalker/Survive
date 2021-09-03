package com.stereowalker.survive.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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

	public static CompoundNBT soapTag(int soap) {
		CompoundNBT nbt = new CompoundNBT();
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
			stack.getOrCreateTag().putInt("SoapLeft", MathHelper.clamp(drinks, 0, ((SoapItem)stack.getItem()).soapMaxAmount));
	}

	public static void decrementSoap(ItemStack stack) {
		setSoapLeft(stack, getSoapLeft(stack) - 1);
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return new ItemStack(Items.GLASS_BOTTLE);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("tooltip.soap_left", getSoapLeft(stack)).mergeStyle(TextFormatting.AQUA));
	}

}
