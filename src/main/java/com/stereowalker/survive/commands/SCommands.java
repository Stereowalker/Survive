package com.stereowalker.survive.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.stereowalker.survive.commands.impl.NeedsCommand;

import net.minecraft.command.CommandSource;

public class SCommands {

	public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
		NeedsCommand.register(dispatcher);
	}

}