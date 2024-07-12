package com.stereowalker.survive.mixins;

import java.util.List;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.network.protocol.game.ServerboundPlayerStatusBookPacket;
import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemMixin extends Item{
	private static final String TAG_STATUS_OWNER = "status_owner";

	public WrittenBookItemMixin(Properties pProperties) {
		super(pProperties);
	}

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pPlayer.openItemGui(itemstack, pHand);
        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		if (pIsSelected) {
			WrittenBookContent writtenbookcontent = pStack.get(DataComponents.WRITTEN_BOOK_CONTENT);
			if (writtenbookcontent.generation() == 0 && pStack.has(SDataComponents.STATUS_OWNER) && pEntity instanceof Player player && player instanceof IRealisticEntity real) {
				if (pStack.get(SDataComponents.STATUS_OWNER) == UUID.fromString("00000000-0000-0000-0000-000000000000")) {
					pStack.set(SDataComponents.STATUS_OWNER, player.getUUID());
				}
				if (pStack.get(SDataComponents.STATUS_OWNER).equals(player.getUUID()) && pLevel.isClientSide) {
					sendPacket(writtenbookcontent);
				}
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private void sendPacket(WrittenBookContent tag) {
		new ServerboundPlayerStatusBookPacket(tag, !Survive.TEMPERATURE_CONFIG.displayTempInFahrenheit, 
				net.minecraft.client.resources.language.I18n.get("book.patient.sleep", "%1$s"),
				net.minecraft.client.resources.language.I18n.get("book.patient.temperature", "%1$s")).send();
	}
	
	@Inject(method = "appendHoverText", at = @At("TAIL"))
	public void appendHoverText_inject(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag, CallbackInfo ci) {
		if (pStack.has(SDataComponents.STATUS_OWNER)) {
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
