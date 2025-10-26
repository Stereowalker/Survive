package com.stereowalker.survive;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stereowalker.survive.json.FoodJsonHolder;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.unionlib.util.RegistryHelper;
import com.stereowalker.unionlib.util.VersionHelper.VanillaComponents;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FoodUtils {

	public record FoodStatus(long creationTime, double lifespan, double saltDose) {
		public static final Codec<FoodStatus> CODEC = RecordCodecBuilder.create(
				builder -> builder.group(
						Codec.LONG.fieldOf("creation_time").forGetter(FoodStatus::creationTime),
						Codec.DOUBLE.fieldOf("lifespan").forGetter(FoodStatus::lifespan),
						Codec.DOUBLE.fieldOf("salt_dose").forGetter(FoodStatus::saltDose)
						)
				.apply(builder, FoodStatus::new)
				);
//		public static final StreamCodec<RegistryFriendlyByteBuf, FoodStatus> STREAM_CODEC = StreamCodec.composite(
//				ByteBufCodecs.VAR_LONG,
//				FoodStatus::creationTime,
//				ByteBufCodecs.DOUBLE,
//				FoodStatus::lifespan,
//				ByteBufCodecs.DOUBLE,
//				FoodStatus::saltDose,
//				FoodStatus::new
//				);
		
		public long expireTime() {return creationTime + (long)Math.floor(lifespan);}
		
		public FoodStatus extendTime(float extension) { return new FoodStatus(creationTime, lifespan + extension, saltDose);}
		
		public FoodStatus increaseSalt(float extension) { return new FoodStatus(creationTime, lifespan, saltDose + extension);}
	}


	public enum State {Fresh, Good, Okay, Spoiling, Spoiled}
	public enum Saltiness {None, Light, Average, Heavy, Extreme}

	public static void giveLifespanToFood(NonNullList<ItemStack> items, long gametime) {
		if (Survive.FOOD_CONFIG.enabled) {
			items.forEach((stack) -> {
				if (VanillaComponents.FOOD.hasData(stack) && !SDataComponents.FOOD_STATUS_D.hasData(stack) && DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(stack.getItem()))) {
					long lifespan = DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(stack.getItem())).lifespan();
					if (lifespan > 0) {
						long shaveAMinuteOff = gametime - (gametime % (20 * 60));
						SDataComponents.FOOD_STATUS_D.setData(stack, new FoodStatus(shaveAMinuteOff, lifespan, 0));
					}
				}
			});
		} else {
			items.forEach((stack) -> {
				if (SDataComponents.FOOD_STATUS_D.hasData(stack)) {
					SDataComponents.FOOD_STATUS_D.removeData(stack);
				}
			});
		}
	}

	public static void giveLifespanToFood(ItemStack stack, long gametime) {
		if (Survive.FOOD_CONFIG.enabled) {
			if (VanillaComponents.FOOD.hasData(stack) && !SDataComponents.FOOD_STATUS_D.hasData(stack) && DataMaps.Server.consummableItem.containsKey(RegistryHelper.items().getKey(stack.getItem()))) {
				long lifespan = DataMaps.Server.consummableItem.get(RegistryHelper.items().getKey(stack.getItem())).lifespan();
				if (lifespan > 0) {
					long shaveAMinuteOff = gametime - (gametime % (20 * 60));
					SDataComponents.FOOD_STATUS_D.setData(stack, new FoodStatus(shaveAMinuteOff, lifespan, 0));
				}
			}
		} else {
			if (SDataComponents.FOOD_STATUS_D.hasData(stack)) {
				SDataComponents.FOOD_STATUS_D.removeData(stack);
			}
		}
	}

	public static void applyFoodStatusToTooltip(Player player, ItemStack stack, List<Component> tip) {
		if (VanillaComponents.FOOD.hasData(stack)) {//TODO: Use version helper to check this in the future
			if (Survive.FOOD_CONFIG.enabled) {
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
			if (SDataComponents.FOOD_STATUS_D.hasData(stack)) {
				var fd = SDataComponents.FOOD_STATUS_D.getData(stack);
				if (fd.saltDose > 500) {
					tip.add(Component.literal("Deathly Salted").setStyle(Style.EMPTY));
				} else if (fd.saltDose > 250) {
					tip.add(Component.literal("Heavily Salted").setStyle(Style.EMPTY));
				} else if (fd.saltDose > 125) {
					tip.add(Component.literal("Salted").setStyle(Style.EMPTY));
				} else if (fd.saltDose > 10) {
					tip.add(Component.literal("Lightly Salted").setStyle(Style.EMPTY));
				}
			}
			
		}
	}

	public static State foodStatus(ItemStack stack, Level level) {
		if (SDataComponents.FOOD_STATUS_D.hasData(stack) && (level.isClientSide ?  DataMaps.Client.consummableItem : DataMaps.Server.consummableItem).containsKey(RegistryHelper.items().getKey(stack.getItem())) && Survive.FOOD_CONFIG.enabled) {
			FoodJsonHolder food = (level.isClientSide ?  DataMaps.Client.consummableItem : DataMaps.Server.consummableItem).get(RegistryHelper.items().getKey(stack.getItem()));
			long timeTill = SDataComponents.FOOD_STATUS_D.getData(stack).expireTime() - level.getGameTime();
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
