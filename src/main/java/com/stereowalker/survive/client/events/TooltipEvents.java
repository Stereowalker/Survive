package com.stereowalker.survive.client.events;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.events.SurviveEvents;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeInstance;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = "survive", value = Dist.CLIENT)
public class TooltipEvents {

	@OnlyIn(Dist.CLIENT)
	public static void accessoryTooltip(Player player, ItemStack stack, List<Component> tooltip) {
		if (DataMaps.Client.armor.containsKey(stack.getItem().getRegistryName())) {
			float kg = SurviveEvents.getArmorWeightClient(stack);
			float rawPound = kg*2.205f;
			int poundInt = (int)(rawPound*1000);
			float pound = poundInt/1000.0F;
			tooltip.add(1, new TranslatableComponent("tooltip.survive.weight", Survive.CONFIG.displayWeightInPounds ? pound : kg, Survive.CONFIG.displayWeightInPounds ? "lbs" : "kg").withStyle(ChatFormatting.DARK_PURPLE));
			for (Pair<String,TemperatureChangeInstance> instance : DataMaps.Client.armor.get(stack.getItem().getRegistryName()).getTemperatureModifier()) {
				if (instance.getSecond().shouldChangeTemperature(player)) {
					if (instance.getSecond().getAdditionalContext() != null)
						tooltip.add(2, new TranslatableComponent("tooltip.survive.temperature", instance.getSecond().getTemperature()).append(instance.getSecond().getAdditionalContext()).withStyle(ChatFormatting.DARK_PURPLE));
					else
						tooltip.add(2, new TranslatableComponent("tooltip.survive.temperature", instance.getSecond().getTemperature()).withStyle(ChatFormatting.DARK_PURPLE));
					break;
				}
			}
		} else {
			tooltip.add(1, new TranslatableComponent("tooltip.survive.weight", 0, Survive.CONFIG.displayWeightInPounds ? "lbs" : "kg").withStyle(ChatFormatting.DARK_PURPLE));
			tooltip.add(2, new TranslatableComponent("tooltip.survive.temperature", 0).withStyle(ChatFormatting.DARK_PURPLE));

		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void tooltips(ItemTooltipEvent event) {
		if (Survive.CONFIG.enable_weights) {
			boolean showWeight = false;
			for(EquipmentSlot type : EquipmentSlot.values()) {
				if (event.getPlayer() != null && event.getItemStack().canEquip(type, event.getPlayer()) && type.getType() == Type.ARMOR) {
					showWeight = true;
					break;
				}
			}
			if (showWeight) {
				accessoryTooltip(event.getPlayer(), event.getItemStack(), event.getToolTip());
			}
		}
	}
}
