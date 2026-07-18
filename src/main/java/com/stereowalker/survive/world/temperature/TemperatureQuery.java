package com.stereowalker.survive.world.temperature;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;
import com.stereowalker.survive.world.temperature.TemperatureModifier.ContributingFactor;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * DO NOT use the player to check the level loaded or the position of the player as I would not 
 * be passing those variables as arguments if I wanted you to do that
 * @author Stereowalker
 */
@FunctionalInterface
public interface TemperatureQuery {
	public static Map<Identifier,Tuple<TemperatureQuery, ContributingFactor>> queries = Maps.newHashMap();
	public static void registerQuery(Identifier id, ContributingFactor factor, TemperatureQuery query) {
		queries.put(id, new Tuple<>(query, factor));
	}
	public static void registerQuery(String id, ContributingFactor factor, TemperatureQuery query) {
		registerQuery(VersionHelper.toLoc(id), factor, query);
	}
	
	double run(@Nullable Player player, double temp, Level level, BlockPos pos, boolean applyTemp);
}
