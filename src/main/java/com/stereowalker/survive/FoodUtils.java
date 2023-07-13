package com.stereowalker.survive;

import java.util.List;

import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FoodUtils {
	public enum State {Fresh, Good, Okay, Spoiling, Spoiled}
	
	public static final String EXPIRE = "expiry_date";
	public static void giveLifespanToFoodInInventory(Player player) {
		player.getInventory().items.forEach((stack) -> {
			if (stack.isEdible() && DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(stack.getItem())) && !stack.getOrCreateTag().contains(EXPIRE)) {
				stack.getTag().putLong(EXPIRE, player.level().getGameTime() + DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(stack.getItem())).getTimeUntilSpoil());
			}
		});
	}
	
	public static void applyFoodStatusToTooltip(Player player, ItemStack stack, List<Component> tip) {
		if (stack.isEdible()) {
			 if (foodStatus(stack, player.level()) == State.Fresh)
				tip.add(Component.literal("Fresh").setStyle(Style.EMPTY.withColor(0x88ff88)));
			else if (foodStatus(stack, player.level()) == State.Good)
				tip.add(Component.literal("Good").setStyle(Style.EMPTY.withColor(0x00ff00)));
			else if (foodStatus(stack, player.level()) == State.Spoiling)
				tip.add(Component.literal("Spoiling").setStyle(Style.EMPTY.withColor(0xaaff00)));
			else if (foodStatus(stack, player.level()) == State.Spoiled)
				tip.add(Component.literal("Spoiled").setStyle(Style.EMPTY.withColor(0x88aa00)));
			else
				tip.add(Component.literal("Okay").setStyle(Style.EMPTY.withColor(0xffff00)));
		}
	}
	
	public static State foodStatus(ItemStack stack, Level level) {
		if (stack.getTag() != null && DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(stack.getItem())) && stack.getTag().contains(EXPIRE)) {
			long timeTill = stack.getTag().getInt(EXPIRE) - level.getGameTime();
			long timeSince = DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(stack.getItem())).getTimeUntilSpoil() - timeTill;
			if (timeTill < 0) {
				return State.Spoiled;
			} else if (timeTill <= 2400) {
				return State.Spoiling;
			} else if (timeSince <= 1200) {
				return State.Fresh;
			} else if (timeSince <= 3600) {
				return State.Good;
			}
		}
		return State.Okay;
	}
}
