package com.stereowalker.survive.mixins.client;

import java.util.List;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;

@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemClientMixin {
	
	@Inject(method = "appendHoverText", at = @At("TAIL"))
	public void appendHoverText_inject(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag, CallbackInfo ci) {
		if (pStack.has(SDataComponents.STATUS_OWNER) && !pStack.get(SDataComponents.STATUS_OWNER).equals(new UUID(0L, 0L))) {
			String s = "";
			Level pLevel = Minecraft.getInstance().level;
			if (pLevel != null) {
				s = pLevel.getPlayerByUUID(pStack.get(SDataComponents.STATUS_OWNER)).getName().getString();
			}
			if (!StringUtil.isNullOrEmpty(s)) 
				pTooltipComponents.add(Component.translatable("book.forPatient", s).withStyle(ChatFormatting.GREEN));
			else
				pTooltipComponents.add(Component.translatable("book.noPatient").withStyle(ChatFormatting.GREEN));
		}
	}

}
