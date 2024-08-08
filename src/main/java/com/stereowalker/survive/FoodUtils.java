package com.stereowalker.survive;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stereowalker.survive.json.FoodJsonHolder;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FoodUtils {

	public record FoodStatus(long creationTime, double lifespan) {
		public static final Codec<FoodStatus> CODEC = RecordCodecBuilder.create(
				builder -> builder.group(
						Codec.LONG.fieldOf("creation_time").forGetter(FoodStatus::creationTime),
						Codec.DOUBLE.fieldOf("lifespan").forGetter(FoodStatus::lifespan)
						)
				.apply(builder, FoodStatus::new)
				);
		public static final StreamCodec<RegistryFriendlyByteBuf, FoodStatus> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_LONG,
				FoodStatus::creationTime,
				ByteBufCodecs.DOUBLE,
				FoodStatus::lifespan,
				FoodStatus::new
				);
		
		public long expireTime() {return creationTime + (long)Math.floor(lifespan);}
		
		public FoodStatus extendTime(float extension) { return new FoodStatus(creationTime, lifespan + extension);}
	}


	public enum State {Fresh, Good, Okay, Spoiling, Spoiled}

	public static void giveLifespanToFood(NonNullList<ItemStack> items, long gametime) {
		if (Survive.FOOD_CONFIG.enabled) {
			items.forEach((stack) -> {
				if (stack.has(DataComponents.FOOD) && !stack.has(SDataComponents.FOOD_STATUS) && DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(stack.getItem()))) {
					long lifespan = DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(stack.getItem())).lifespan();
					if (lifespan > 0) {
						long shaveAMinuteOff = gametime - (gametime % (20 * 60));
						stack.set(SDataComponents.FOOD_STATUS, new FoodStatus(shaveAMinuteOff, lifespan));
					}
				}
			});
		} else {
			items.forEach((stack) -> {
				if (stack.has(SDataComponents.FOOD_STATUS)) {
					stack.remove(SDataComponents.FOOD_STATUS);
				}
			});
		}
	}

	public static void giveLifespanToFood(ItemStack stack, long gametime) {
		if (Survive.FOOD_CONFIG.enabled) {
			if (stack.has(DataComponents.FOOD) && !stack.has(SDataComponents.FOOD_STATUS) && DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(stack.getItem()))) {
				long lifespan = DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(stack.getItem())).lifespan();
				if (lifespan > 0) {
					long shaveAMinuteOff = gametime - (gametime % (20 * 60));
					stack.set(SDataComponents.FOOD_STATUS, new FoodStatus(shaveAMinuteOff, lifespan));
				}
			}
		} else {
			if (stack.has(SDataComponents.FOOD_STATUS)) {
				stack.remove(SDataComponents.FOOD_STATUS);
			}
		}
	}

	public static void applyFoodStatusToTooltip(Player player, ItemStack stack, List<Component> tip) {
		if (stack.has(DataComponents.FOOD) && Survive.FOOD_CONFIG.enabled) {
			State state = foodStatus(stack, player.level());
			if (state == State.Fresh)
				tip.add(Component.literal("Fresh").setStyle(Style.EMPTY.withColor(0x88ff88)));
			else if (state == State.Good)
				tip.add(Component.literal("Good").setStyle(Style.EMPTY.withColor(0x00ff00)));
			else if (state == State.Spoiling)
				tip.add(Component.literal("Spoiling").setStyle(Style.EMPTY.withColor(0xaaff00)));
			else if (state == State.Spoiled)
				tip.add(Component.literal("Spoiled").setStyle(Style.EMPTY.withColor(0x88aa00)));
			else
				tip.add(Component.literal("Okay").setStyle(Style.EMPTY.withColor(0xffff00)));
		}
	}

	public static State foodStatus(ItemStack stack, Level level) {
		if (stack.has(SDataComponents.FOOD_STATUS) && (level.isClientSide ?  DataMaps.Client.consummableItem : DataMaps.Server.consummableItem).containsKey(RegistryHelper.items().getKey(stack.getItem())) && Survive.FOOD_CONFIG.enabled) {
			FoodJsonHolder food = (level.isClientSide ?  DataMaps.Client.consummableItem : DataMaps.Server.consummableItem).get(RegistryHelper.items().getKey(stack.getItem()));
			long timeTill = stack.get(SDataComponents.FOOD_STATUS).expireTime() - level.getGameTime();
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
