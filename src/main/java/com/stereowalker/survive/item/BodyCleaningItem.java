package com.stereowalker.survive.item;

import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.HygieneStats;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BodyCleaningItem extends Item {
	private int cleanValue;

	public BodyCleaningItem(int cleanValueIn, Properties properties) {
		super(properties);
		this.cleanValue = cleanValueIn;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		
		HygieneStats stats = SurviveEntityStats.getHygieneStats(playerIn);
		if (playerIn.isWet()) {
			int cleaning = 0;
			if (playerIn.getHeldItem(handIn == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND).getItem() instanceof SoapItem) {
				ItemStack soap = playerIn.getHeldItem(handIn == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
				cleaning = this.cleanValue * ((SoapItem)soap.getItem()).soapEfficacy;
				SoapItem.decrementSoap(soap);
				if (SoapItem.getSoapLeft(soap) <= 0) {
					playerIn.setHeldItem(handIn == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND, soap.getContainerItem());
				}
			} else {
				cleaning = this.cleanValue;
			}
			stats.clean(cleaning);
			playerIn.getHeldItem(handIn).damageItem(1, playerIn, (anim) ->{
				anim.sendBreakAnimation(EquipmentSlotType.MAINHAND);
			});
		} else {
			playerIn.getHeldItem(handIn).damageItem(2, playerIn, (anim) ->{
				anim.sendBreakAnimation(EquipmentSlotType.MAINHAND);
			});
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

}
