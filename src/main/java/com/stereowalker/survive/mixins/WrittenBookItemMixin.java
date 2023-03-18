package com.stereowalker.survive.mixins;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.network.protocol.game.ServerboundPlayerStatusBookPacket;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemMixin extends Item{
	private static final String TAG_STATUS_OWNER = "status_owner";

	public WrittenBookItemMixin(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		if (pIsSelected) {
			CompoundTag compoundtag = pStack.getTag();
			if (compoundtag != null && WrittenBookItem.getGeneration(pStack) == 0 && compoundtag.contains(TAG_STATUS_OWNER) && pEntity instanceof Player player && player instanceof IRealisticEntity real) {
				if (compoundtag.getString(TAG_STATUS_OWNER).isEmpty()) {
					compoundtag.putString(TAG_STATUS_OWNER, player.getStringUUID());
				}
				else if (compoundtag.getString(TAG_STATUS_OWNER).equals(player.getStringUUID()) && pLevel.isClientSide) {
					sendPacket(compoundtag);
				}
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private void sendPacket(CompoundTag tag) {
		new ServerboundPlayerStatusBookPacket(tag, !Survive.TEMPERATURE_CONFIG.displayTempInFahrenheit, 
				net.minecraft.client.resources.language.I18n.get("book.patient.sleep", "%1$s"),
				net.minecraft.client.resources.language.I18n.get("book.patient.temperature", "%1$s")).send();
	}

	@Inject(method = "appendHoverText", at = @At("TAIL"))
	public void appendHoverText_inject(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag, CallbackInfo ci) {
		CompoundTag compoundtag = pStack.getTag();
		if (compoundtag != null && compoundtag.contains(TAG_STATUS_OWNER)) {
			String s = "";
			if (pLevel != null && !compoundtag.getString(TAG_STATUS_OWNER).isEmpty()) {
				s = pLevel.getPlayerByUUID(UUID.fromString(compoundtag.getString(TAG_STATUS_OWNER))).getName().getString();
			}
			if (!StringUtil.isNullOrEmpty(s)) 
				pTooltip.add(Component.translatable("book.forPatient", s).withStyle(ChatFormatting.GREEN));
			else
				pTooltip.add(Component.translatable("book.noPatient").withStyle(ChatFormatting.GREEN));
		}
	}

}
