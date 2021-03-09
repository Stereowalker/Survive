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

public class SpongeItem extends Item {

	public SpongeItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		
		HygieneStats stats = SurviveEntityStats.getHygieneStats(playerIn);
		if (playerIn.isWet()) {
			stats.clean(10);
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
