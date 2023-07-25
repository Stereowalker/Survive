package com.stereowalker.survive;

import java.util.List;

import com.stereowalker.survive.json.FoodJsonHolder;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FoodUtils {
	public enum State {Fresh, Good, Okay, Spoiling, Spoiled}
	
	public static final String EXPIRE = "expiry_date";
	public static void giveLifespanToFood(NonNullList<ItemStack> items, long gametime) {
		if (Survive.CONFIG.enable_food_spoiling) {
			items.forEach((stack) -> {
				if (stack.isEdible() && DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(stack.getItem())) && !stack.getOrCreateTag().contains(EXPIRE)) {
					long shaveAMinuteOff = gametime - (gametime % (20 * 60));
					stack.getTag().putLong(EXPIRE, shaveAMinuteOff + DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(stack.getItem())).lifespan());
				}
			});
		} else {
			items.forEach((stack) -> {
				if (stack.getTag() != null && stack.getTag().contains(EXPIRE)) {
					stack.getTag().remove(EXPIRE);
				}
			});
		}
	}
	
	public static void applyFoodStatusToTooltip(Player player, ItemStack stack, List<Component> tip) {
		if (stack.isEdible() && Survive.CONFIG.enable_food_spoiling) {
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
		if (stack.getTag() != null && DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(stack.getItem())) && stack.getTag().contains(EXPIRE) && Survive.CONFIG.enable_food_spoiling) {
			FoodJsonHolder food = DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(stack.getItem()));
			long timeTill = stack.getTag().getInt(EXPIRE) - level.getGameTime();
			long timeSince = food.lifespan() - timeTill;
			if (timeTill < 0) {
				return State.Spoiled;
			} else if (timeTill <= food.ticksFresh() * 2) {
				return State.Spoiling;
			} else if (timeSince <= food.ticksFresh()) {
				return State.Fresh;
			} else if (timeSince <= food.ticksFresh() * 3) {
				return State.Good;
			}
		}
		return State.Okay;
	}
}
