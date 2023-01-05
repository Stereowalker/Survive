package com.stereowalker.survive.client.events;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.WeightHandler;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.temperature.conditions.TemperatureChangeInstance;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
	public static void accessoryTooltip(Player player, ItemStack stack, List<Component> tooltip, boolean displayWeight, boolean displayTemp) {
		List<Component> tooltipsToAdd = new ArrayList<Component>();
		if (DataMaps.Client.armor.containsKey(BuiltInRegistries.ITEM.getKey(stack.getItem()))) {
			float kg = WeightHandler.getArmorWeightClient(stack);
			float rawPound = kg*2.205f;
			int poundInt = (int)(rawPound*1000);
			float pound = poundInt/1000.0F;
			if (displayWeight) tooltipsToAdd.add(Component.translatable("tooltip.survive.weight", Survive.STAMINA_CONFIG.displayWeightInPounds ? pound : kg, Survive.STAMINA_CONFIG.displayWeightInPounds ? "lbs" : "kg").withStyle(ChatFormatting.DARK_PURPLE));
			if (displayTemp)
				for (Pair<String,TemperatureChangeInstance> instance : DataMaps.Client.armor.get(BuiltInRegistries.ITEM.getKey(stack.getItem())).getTemperatureModifier()) {
					if (instance.getSecond().shouldChangeTemperature(player)) {
						if (instance.getSecond().getAdditionalContext() != null)
							tooltipsToAdd.add(Component.translatable("tooltip.survive.temperature", instance.getSecond().getTemperature()).append(instance.getSecond().getAdditionalContext()).withStyle(ChatFormatting.DARK_PURPLE));
						else
							tooltipsToAdd.add(Component.translatable("tooltip.survive.temperature", instance.getSecond().getTemperature()).withStyle(ChatFormatting.DARK_PURPLE));
						break;
					}
				}
		} else {
			if (displayWeight) tooltipsToAdd.add(Component.translatable("tooltip.survive.weight", 0, Survive.STAMINA_CONFIG.displayWeightInPounds ? "lbs" : "kg").withStyle(ChatFormatting.DARK_PURPLE));
			if (displayTemp) tooltipsToAdd.add(Component.translatable("tooltip.survive.temperature", 0).withStyle(ChatFormatting.DARK_PURPLE));
		}
		
		tooltip.addAll(1, tooltipsToAdd);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void tooltips(ItemTooltipEvent event) {
		boolean showWeight = false;
		boolean showTemp = false;
		if ((Survive.STAMINA_CONFIG.enabled && Survive.STAMINA_CONFIG.enable_weights) || Survive.TEMPERATURE_CONFIG.enabled) {
			for(EquipmentSlot type : EquipmentSlot.values()) {
				if (event.getEntity() != null && event.getItemStack().canEquip(type, event.getEntity()) && type.getType() == Type.ARMOR) {
					showWeight = Survive.STAMINA_CONFIG.enabled && Survive.STAMINA_CONFIG.enable_weights;
					showTemp = Survive.TEMPERATURE_CONFIG.enabled;
					break;
				}
			}
		}

		if (showWeight || showTemp) {
			accessoryTooltip(event.getEntity(), event.getItemStack(), event.getToolTip(), showWeight, showTemp);
		}
	}
}
