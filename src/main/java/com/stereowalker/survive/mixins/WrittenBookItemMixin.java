package com.stereowalker.survive.mixins;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;

@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemMixin extends Item{
	private static final String TAG_STATUS_OWNER = "status_owner";

	public WrittenBookItemMixin(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		if (pIsSelected) {
			WrittenBookContent writtenbookcontent = pStack.get(DataComponents.WRITTEN_BOOK_CONTENT);
			if (writtenbookcontent.generation() == 0 && pStack.has(SDataComponents.STATUS_OWNER) && pEntity instanceof Player player && player instanceof IRealisticEntity real) {
				if (pStack.get(SDataComponents.STATUS_OWNER).equals(new UUID(0L, 0L))) {
					pStack.set(SDataComponents.STATUS_OWNER, player.getUUID());
				}
				if (pStack.get(SDataComponents.STATUS_OWNER).equals(player.getUUID()) && pLevel.isClientSide) {
					Survive.sendPacket(writtenbookcontent);
				}
			}
		}
	}

}
