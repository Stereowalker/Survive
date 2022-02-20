package com.stereowalker.survive.commands.impl;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.HygieneStats;
import com.stereowalker.survive.util.SleepStats;
import com.stereowalker.survive.util.StaminaStats;
import com.stereowalker.survive.util.WaterStats;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class NeedsCommand {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("needs").requires((p_137384_) -> {
					return p_137384_.hasPermissionLevel(2);
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

	private static int restore(CommandSource source, float amount, NeedType type, Collection<ServerPlayerEntity> pTargets) throws CommandSyntaxException {
		for(ServerPlayerEntity player : pTargets) {
			SleepStats sleepData = SurviveEntityStats.getSleepStats(player);
			WaterStats waterData = SurviveEntityStats.getWaterStats(player);
			StaminaStats staminaData = SurviveEntityStats.getEnergyStats(player);
			HygieneStats hygieneData = SurviveEntityStats.getHygieneStats(player);
			switch (type)  {
			case STAMINA:
				staminaData.addStats(MathHelper.floor(amount));
				break;
			case HUNGER:
				player.getFoodStats().setFoodLevel(MathHelper.floor(amount));
				break;
			case SATURATION:
				player.getFoodStats().setFoodSaturationLevel(amount);
				break;
			case CLEANSING:
				hygieneData.clean(MathHelper.floor(amount));
				break;
			case SLEEP:
				sleepData.addAwakeTime(player, -MathHelper.floor(amount));
				break;
			case THIRST:
				waterData.addStats(MathHelper.floor(amount), 0);
				break;
			case HYDRATION:
				waterData.addStats(0, amount);
				break;
			}
			sleepData.save(player);
			waterData.save(player);
			hygieneData.save(player);
			staminaData.save(player);
		}

		if (pTargets.size() == 1) {
			source.sendFeedback(new TranslationTextComponent("commands.needs.restore."+type.getString()+".success.single", amount, pTargets.iterator().next().getDisplayName()), true);
		} else {
			source.sendFeedback(new TranslationTextComponent("commands.needs.restore."+type.getString()+".success.multiple", amount, pTargets.size()), true);
		}
		return pTargets.size();
	}

	private static int deplete(CommandSource source, float amount, NeedType type, Collection<ServerPlayerEntity> pTargets) throws CommandSyntaxException {
		for(ServerPlayerEntity player : pTargets) {
			SleepStats sleepData = SurviveEntityStats.getSleepStats(player);
			WaterStats waterData = SurviveEntityStats.getWaterStats(player);
			StaminaStats staminaData = SurviveEntityStats.getEnergyStats(player);
			HygieneStats hygieneData = SurviveEntityStats.getHygieneStats(player);
			switch (type)  {
			case STAMINA:
				staminaData.setEnergyLevel(staminaData.getEnergyLevel()-MathHelper.floor(amount));
				break;
			case HUNGER:
				player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel()-MathHelper.floor(amount));
				break;
			case SATURATION:
				player.getFoodStats().setFoodSaturationLevel(player.getFoodStats().getSaturationLevel()-amount);
				break;
			case CLEANSING:
				hygieneData.dirty(MathHelper.floor(amount));
				break;
			case SLEEP:
				sleepData.addAwakeTime(player, MathHelper.floor(amount));
				break;
			case THIRST:
				waterData.setWaterLevel(waterData.getWaterLevel()-MathHelper.floor(amount));
				break;
			case HYDRATION:
				waterData.setWaterHydrationLevel(waterData.getHydrationLevel()-MathHelper.floor(amount));
				break;
			}
			sleepData.save(player);
			waterData.save(player);
			hygieneData.save(player);
			staminaData.save(player);
		}

		if (pTargets.size() == 1) {
			source.sendFeedback(new TranslationTextComponent("commands.needs.deplete."+type.getString()+".success.single", amount, pTargets.iterator().next().getDisplayName()), true);
		} else {
			source.sendFeedback(new TranslationTextComponent("commands.needs.deplete."+type.getString()+".success.multiple", amount, pTargets.size()), true);
		}
		return pTargets.size();
	}

	private static int query(CommandSource source, NeedType type, ServerPlayerEntity pTarget) throws CommandSyntaxException {
		float result = 0;
		SleepStats sleepData = SurviveEntityStats.getSleepStats(pTarget);
		WaterStats waterData = SurviveEntityStats.getWaterStats(pTarget);
		StaminaStats staminaData = SurviveEntityStats.getEnergyStats(pTarget);
		HygieneStats hygieneData = SurviveEntityStats.getHygieneStats(pTarget);
		switch (type)  {
		case STAMINA:
			result = staminaData.getEnergyLevel();
			break;
		case HUNGER:
			result = pTarget.getFoodStats().getFoodLevel();
			break;
		case SATURATION:
			result = pTarget.getFoodStats().getSaturationLevel();
			break;
		case CLEANSING:
			result = hygieneData.getUncleanLevel();
			break;
		case SLEEP:
			result = sleepData.getAwakeTimer();
			break;
		case THIRST:
			result = waterData.getWaterLevel();
			break;
		case HYDRATION:
			result = waterData.getHydrationLevel();
			break;
		}

		source.sendFeedback(new TranslationTextComponent("commands.needs.query."+type.getString(), pTarget.getDisplayName(), result), true);
		return MathHelper.floor(result);
	}

	static enum NeedType implements IStringSerializable {
		STAMINA("stamina"), HUNGER("hunger"), SATURATION("saturation"), CLEANSING("cleansing"), SLEEP("sleep"), THIRST("thirst"), HYDRATION("hydration");

		String name;
		private NeedType(String name) {
			this.name = name;
		}

		@Override
		public String getString() {
			return this.name;
		}
	}
}