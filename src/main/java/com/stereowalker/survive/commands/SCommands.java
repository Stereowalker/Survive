package com.stereowalker.survive.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.stereowalker.survive.server.commands.NeedsCommand;

import net.minecraft.commands.CommandSourceStack;

public class SCommands {

	public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		NeedsCommand.register(dispatcher);
	}

}
