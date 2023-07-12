package com.stereowalker.survive;

import com.stereowalker.unionlib.client.gui.screens.config.MinecraftModConfigsScreen;
import com.stereowalker.unionlib.mod.ClientSegment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SurviveClientSegment extends ClientSegment {

	@Override
	public ResourceLocation getModIcon() {
		return new ResourceLocation(Survive.MOD_ID, "textures/icon.png");
	}

	@Override
	public Screen getConfigScreen(Minecraft mc, Screen previousScreen) {
		return new MinecraftModConfigsScreen(previousScreen, Component.translatable("gui.survive.config.title"), Survive.HYGIENE_CONFIG, Survive.STAMINA_CONFIG, Survive.TEMPERATURE_CONFIG, Survive.THIRST_CONFIG, Survive.WELLBEING_CONFIG, Survive.CONFIG);
	}

}
