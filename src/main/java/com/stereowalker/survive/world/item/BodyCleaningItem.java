package com.stereowalker.survive.world.item;

import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.HygieneData;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BodyCleaningItem extends Item {
	private int cleanValue;

	public BodyCleaningItem(int cleanValueIn, Properties properties) {
		super(properties);
		this.cleanValue = cleanValueIn;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		
		HygieneData stats = SurviveEntityStats.getHygieneStats(playerIn);
		if (playerIn.isInWaterOrRain()) {
			int cleaning = 0;
			if (playerIn.getItemInHand(handIn == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND).getItem() instanceof SoapItem) {
				ItemStack soap = playerIn.getItemInHand(handIn == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
				cleaning = this.cleanValue * ((SoapItem)soap.getItem()).soapEfficacy;
				SoapItem.decrementSoap(soap);
				if (SoapItem.getSoapLeft(soap) <= 0) {
					playerIn.setItemInHand(handIn == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, soap.getContainerItem());
				}
			} else {
				cleaning = this.cleanValue;
			}
			stats.clean(cleaning, false);
			playerIn.getItemInHand(handIn).hurtAndBreak(1, playerIn, (anim) ->{
				anim.broadcastBreakEvent(EquipmentSlot.MAINHAND);
			});
		} else {
			playerIn.getItemInHand(handIn).hurtAndBreak(2, playerIn, (anim) ->{
				anim.broadcastBreakEvent(EquipmentSlot.MAINHAND);
			});
		}
		return super.use(worldIn, playerIn, handIn);
	}

}
