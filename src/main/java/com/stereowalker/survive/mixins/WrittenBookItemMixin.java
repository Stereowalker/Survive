package com.stereowalker.survive.mixins;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;

@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemMixin extends Item{
	public WrittenBookItemMixin(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		if (pIsSelected) {
			CompoundTag writtenbookcontent = pStack.getTag();
			if (writtenbookcontent != null && WrittenBookItem.getGeneration(pStack) == 0 && SDataComponents.STATUS_OWNER_D.hasData(pStack) && pEntity instanceof Player player && player instanceof IRealisticEntity real) {
				if (SDataComponents.STATUS_OWNER_D.getData(pStack).equals(new UUID(0L, 0L))) {
					SDataComponents.STATUS_OWNER_D.setData(pStack, player.getUUID());
				}
				if (SDataComponents.STATUS_OWNER_D.getData(pStack).equals(player.getUUID()) && pLevel.isClientSide) {
					Survive.sendPacket(writtenbookcontent);
				}
			}
		}
	}

}
