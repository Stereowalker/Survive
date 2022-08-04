package com.stereowalker.survive.server.commands;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.HygieneData;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.needs.SleepData;
import com.stereowalker.survive.needs.StaminaData;
import com.stereowalker.survive.needs.WaterData;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

public class NeedsCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("needs").requires((p_137384_) -> {
					return p_137384_.hasPermission(2);
				}).then(Commands.literal("restore").then(Commands.argument("amount", FloatArgumentType.floatArg(0.1f)).then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.literal("cleansing").executes((e) -> {
							return restore(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.CLEANSING, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("hunger").executes((e) -> {
							return restore(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.HUNGER, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("hydration").executes((e) -> {
							return restore(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.HYDRATION, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("saturation").executes((e) -> {
							return restore(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.SATURATION, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("sleep").executes((e) -> {
							return restore(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.SLEEP, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("stamina").executes((e) -> {
							return restore(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.STAMINA, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("thirst").executes((e) -> {
							return restore(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.THIRST, EntityArgument.getPlayers(e, "targets"));
						}))
						)))
				.then(Commands.literal("deplete").then(Commands.argument("amount", FloatArgumentType.floatArg(0.1f)).then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.literal("cleansing").executes((e) -> {
							return deplete(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.CLEANSING, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("hunger").executes((e) -> {
							return deplete(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.HUNGER, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("hydration").executes((e) -> {
							return deplete(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.HYDRATION, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("saturation").executes((e) -> {
							return deplete(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.SATURATION, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("sleep").executes((e) -> {
							return deplete(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.SLEEP, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("stamina").executes((e) -> {
							return deplete(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.STAMINA, EntityArgument.getPlayers(e, "targets"));
						}))
						.then(Commands.literal("thirst").executes((e) -> {
							return deplete(e.getSource(), FloatArgumentType.getFloat(e, "amount"), NeedType.THIRST, EntityArgument.getPlayers(e, "targets"));
						}))
						)))
				.then(Commands.literal("query").then(Commands.argument("targets", EntityArgument.player())
						.then(Commands.literal("cleansing").executes((e) -> {
							return query(e.getSource(), NeedType.CLEANSING, EntityArgument.getPlayer(e, "targets"));
						}))
						.then(Commands.literal("hunger").executes((e) -> {
							return query(e.getSource(), NeedType.HUNGER, EntityArgument.getPlayer(e, "targets"));
						}))
						.then(Commands.literal("hydration").executes((e) -> {
							return query(e.getSource(), NeedType.HYDRATION, EntityArgument.getPlayer(e, "targets"));
						}))
						.then(Commands.literal("saturation").executes((e) -> {
							return query(e.getSource(), NeedType.SATURATION, EntityArgument.getPlayer(e, "targets"));
						}))
						.then(Commands.literal("sleep").executes((e) -> {
							return query(e.getSource(), NeedType.SLEEP, EntityArgument.getPlayer(e, "targets"));
						}))
						.then(Commands.literal("stamina").executes((e) -> {
							return query(e.getSource(), NeedType.STAMINA, EntityArgument.getPlayer(e, "targets"));
						}))
						.then(Commands.literal("thirst").executes((e) -> {
							return query(e.getSource(), NeedType.THIRST, EntityArgument.getPlayer(e, "targets"));
						}))
						))
				);
	}

	private static int restore(CommandSourceStack source, float amount, NeedType type, Collection<ServerPlayer> pTargets) throws CommandSyntaxException {
		for(ServerPlayer player : pTargets) {
			IRealisticEntity realisticPlayer = (IRealisticEntity)player;
			SleepData sleepData = SurviveEntityStats.getSleepStats(player);
			WaterData waterData = realisticPlayer.getWaterData();
			StaminaData staminaData = SurviveEntityStats.getEnergyStats(player);
			HygieneData hygieneData = SurviveEntityStats.getHygieneStats(player);
			switch (type)  {
			case STAMINA:
				staminaData.relax(Mth.floor(amount), player.getAttributeValue(SAttributes.MAX_STAMINA));
				break;
			case HUNGER:
				player.getFoodData().setFoodLevel(Mth.floor(amount));
				break;
			case SATURATION:
				player.getFoodData().setSaturation(amount);
				break;
			case CLEANSING:
				hygieneData.clean(Mth.floor(amount), true);
				break;
			case SLEEP:
				sleepData.addAwakeTime(player, -Mth.floor(amount));
				break;
			case THIRST:
				waterData.drink(Mth.floor(amount), 0, true);
				break;
			case HYDRATION:
				waterData.drink(0, amount, true);
				break;
			}
			sleepData.save(player);
			waterData.save(player);
			hygieneData.save(player);
			staminaData.save(player);
		}

		if (pTargets.size() == 1) {
			source.sendSuccess(new TranslatableComponent("commands.needs.restore."+type.getSerializedName()+".success.single", amount, pTargets.iterator().next().getDisplayName()), true);
		} else {
			source.sendSuccess(new TranslatableComponent("commands.needs.restore."+type.getSerializedName()+".success.multiple", amount, pTargets.size()), true);
		}
		return pTargets.size();
	}

	private static int deplete(CommandSourceStack source, float amount, NeedType type, Collection<ServerPlayer> pTargets) throws CommandSyntaxException {
		for(ServerPlayer player : pTargets) {
			IRealisticEntity realisticPlayer = (IRealisticEntity)player;
			SleepData sleepData = SurviveEntityStats.getSleepStats(player);
			WaterData waterData = realisticPlayer.getWaterData();
			StaminaData staminaData = SurviveEntityStats.getEnergyStats(player);
			HygieneData hygieneData = SurviveEntityStats.getHygieneStats(player);
			switch (type)  {
			case STAMINA:
				staminaData.setEnergyLevel(staminaData.getEnergyLevel()-Mth.floor(amount));
				break;
			case HUNGER:
				player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel()-Mth.floor(amount));
				break;
			case SATURATION:
				player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel()-amount);
				break;
			case CLEANSING:
				hygieneData.dirty(Mth.floor(amount));
				break;
			case SLEEP:
				sleepData.addAwakeTime(player, Mth.floor(amount));
				break;
			case THIRST:
				waterData.setWaterLevel(waterData.getWaterLevel()-Mth.floor(amount));
				break;
			case HYDRATION:
				waterData.setWaterHydrationLevel(waterData.getHydrationLevel()-Mth.floor(amount));
				break;
			}
			sleepData.save(player);
			waterData.save(player);
			hygieneData.save(player);
			staminaData.save(player);
		}

		if (pTargets.size() == 1) {
			source.sendSuccess(new TranslatableComponent("commands.needs.deplete."+type.getSerializedName()+".success.single", amount, pTargets.iterator().next().getDisplayName()), true);
		} else {
			source.sendSuccess(new TranslatableComponent("commands.needs.deplete."+type.getSerializedName()+".success.multiple", amount, pTargets.size()), true);
		}
		return pTargets.size();
	}

	private static int query(CommandSourceStack source, NeedType type, ServerPlayer pTarget) throws CommandSyntaxException {
		IRealisticEntity realisticPlayer = (IRealisticEntity)pTarget;
		float result = 0;
		SleepData sleepData = SurviveEntityStats.getSleepStats(pTarget);
		StaminaData staminaData = SurviveEntityStats.getEnergyStats(pTarget);
		HygieneData hygieneData = SurviveEntityStats.getHygieneStats(pTarget);
		switch (type)  {
		case STAMINA:
			result = staminaData.getEnergyLevel();
			break;
		case HUNGER:
			result = pTarget.getFoodData().getFoodLevel();
			break;
		case SATURATION:
			result = pTarget.getFoodData().getSaturationLevel();
			break;
		case CLEANSING:
			result = hygieneData.getUncleanLevel();
			break;
		case SLEEP:
			result = sleepData.getAwakeTimer();
			break;
		case THIRST:
			result = realisticPlayer.getWaterData().getWaterLevel();
			break;
		case HYDRATION:
			result = realisticPlayer.getWaterData().getHydrationLevel();
			break;
		}

		source.sendSuccess(new TranslatableComponent("commands.needs.query."+type.getSerializedName(), pTarget.getDisplayName(), result), true);
		return Mth.floor(result);
	}

	static enum NeedType implements StringRepresentable {
		STAMINA("stamina"), HUNGER("hunger"), SATURATION("saturation"), CLEANSING("cleansing"), SLEEP("sleep"), THIRST("thirst"), HYDRATION("hydration");

		String name;
		private NeedType(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}