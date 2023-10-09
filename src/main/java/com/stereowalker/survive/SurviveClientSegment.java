package com.stereowalker.survive;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.TempDisplayMode;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.needs.IRoastedEntity;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.unionlib.api.collectors.OverlayCollector;
import com.stereowalker.unionlib.api.collectors.OverlayCollector.Order;
import com.stereowalker.unionlib.api.gui.GuiRenderer;
import com.stereowalker.unionlib.client.gui.screens.config.MinecraftModConfigsScreen;
import com.stereowalker.unionlib.mod.ClientSegment;
import com.stereowalker.unionlib.util.ScreenHelper;
import com.stereowalker.unionlib.util.ScreenHelper.ScreenOffset;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class SurviveClientSegment extends ClientSegment {

	public static final ResourceLocation GUI_ICONS = new ResourceLocation(Survive.MOD_ID, "textures/gui/icons.png");
	@Override
	public ResourceLocation getModIcon() {
		return new ResourceLocation(Survive.MOD_ID, "textures/icon.png");
	}

	@Override
	public Screen getConfigScreen(Minecraft mc, Screen previousScreen) {
		return new MinecraftModConfigsScreen(previousScreen, Component.translatable("gui.survive.config.title"), Survive.HYGIENE_CONFIG, Survive.STAMINA_CONFIG, Survive.TEMPERATURE_CONFIG, Survive.THIRST_CONFIG, Survive.WELLBEING_CONFIG, Survive.CONFIG);
	}

	@Override
	public void setupGuiOverlays(OverlayCollector collector) {
		collector.register("tired", Order.END, (gui,renderer,width,height)->{
			if (!Survive.CONFIG.tired_overlay && gui.minecraft.player.hasEffect(SMobEffects.TIREDNESS)) {
//				gui.setupOverlayRenderState(true, false);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.disableDepthTest();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				renderTiredOverlay(gui, renderer);
			}
		});
		collector.register("heat_stroke", Order.END, (gui,renderer,width,height)->{
//			gui.setupOverlayRenderState(true, false);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.disableDepthTest();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			renderHeatStroke(gui, renderer);
		});
		collector.register("temperature", Order.END, (gui,renderer,width,height)->{
			if (!gui.minecraft.options.hideGui && Survive.TEMPERATURE_CONFIG.enabled && !Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
				//				gui.setupOverlayRenderState(true, false);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.disableDepthTest();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				renderTemperature(gui, ScreenOffset.TOP, gui.getCameraPlayer(), renderer, true);
			}
		});
	}

	@SuppressWarnings("resource")
	public static void renderTemperature(Gui gui, ScreenOffset position, Player playerentity, GuiRenderer renderer, boolean forgeOverlay) {
		int x = ScreenHelper.getXOffset(position, gui.minecraft) + Survive.TEMPERATURE_CONFIG.tempXLoc;
		int y = ScreenHelper.getYOffset(position, gui.minecraft) + Survive.TEMPERATURE_CONFIG.tempYLoc;
		Minecraft.getInstance().getProfiler().push("temperature");
		//		if (!forgeOverlay) {
		//			RenderSystem.setShader(GameRenderer::getPositionTexShader);
		//			RenderSystem.setShaderTexture(0, GUI_ICONS);
		//		}
		double displayTemp = SurviveEntityStats.getTemperatureStats(playerentity).getDisplayTemperature();
		//For Numbers
		String s = SurviveEntityStats.getTemperatureStats(playerentity).getCelcius()+" °C";
		if (Minecraft.getInstance().gameMode.hasExperience()) {
			if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HORIZONTAL_BAR)) {
				if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp >= 1) {//Hyperthermia override
					renderer.blit(GUI_ICONS, x-3, y-3, 0, 79, 138, 11);
				} else if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp <= -1) {//Hypothermia override
					renderer.blit(GUI_ICONS, x-3, y-3, 0, 90, 138, 11);
				} else {
					renderer.blit(GUI_ICONS, x, y, 3, 64, 132, 5);
					renderer.blit(GUI_ICONS, x, y, 3, 69, 132, 5);
				}
				renderer.blit(GUI_ICONS, x+Mth.floor(displayTemp*44)+63+(displayTemp>0?1:0), y, 1, 74, 4, 5);
			}
			else if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.VERTICAL_BAR)) {
				if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp >= 1) {//Hyperthermia override
					renderer.blit(GUI_ICONS, x-3, y-3, 11, 101, 11, 138);
				} else if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp <= -1) {//Hypothermia override
					renderer.blit(GUI_ICONS, x-3, y-3, 00, 101, 11, 138);
				} else {
					renderer.blit(GUI_ICONS, x, y, 32, 104, 5, 132);
					renderer.blit(GUI_ICONS, x, y, 27, 104, 5, 132);
				}
				renderer.blit(GUI_ICONS, x, y-Mth.floor(displayTemp*44)+63-(displayTemp>0?1:0), 22, 104, 5, 5);
			}
			else if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.NUMBERS)) {
				if (Survive.TEMPERATURE_CONFIG.displayTempInFahrenheit) {
					s = SurviveEntityStats.getTemperatureStats(playerentity).getFahrenheit()+" °F";
				}
				if (displayTemp >= 1) {
					renderer.drawString(s, x, y, ChatFormatting.GOLD.getColor(), false);
				} else if (displayTemp <= -1) {
					renderer.drawString(s, x, y, ChatFormatting.BLUE.getColor(), false);
				} else {
					renderer.drawString(s, x, y, ChatFormatting.GRAY.getColor(), false);
				}
			}
		}
		if (Survive.CONFIG.nutrition_enabled && (playerentity.getMainHandItem().isEdible() || playerentity.getOffhandItem().isEdible())) {
			renderer.drawString("Carbs = "+((IRealisticEntity)playerentity).getNutritionData().getCarbLevel(), 0, 0, ChatFormatting.GRAY.getColor(), false);
			renderer.drawString("Protein = "+((IRealisticEntity)playerentity).getNutritionData().getProteinLevel(), 0, 10, ChatFormatting.GRAY.getColor(), false);
		}
		Minecraft.getInstance().getProfiler().pop();
		//		if (!forgeOverlay) {
		//			RenderSystem.setShader(GameRenderer::getPositionTexShader);
		//			RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		//			RenderSystem.enableBlend();
		//			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		//		}
	}


	public static void renderHeatStroke(Gui gui, GuiRenderer renderer)
	{
		if (((IRoastedEntity)gui.minecraft.player).getTicksRoasted() > 0) {
			gui.renderTextureOverlay(renderer.guiGraphics(), Survive.getInstance().location("textures/misc/burning_overlay.png"), ((IRoastedEntity)gui.minecraft.player).getPercentRoasted());
		}
	}

	@SuppressWarnings("resource")
	public static void renderTiredOverlay(Gui gui, GuiRenderer graphics) {
		Minecraft.getInstance().getProfiler().push("tired");
		int amplifier = Minecraft.getInstance().player.getEffect(SMobEffects.TIREDNESS).getAmplifier() + 1;
		amplifier/=(Survive.CONFIG.tiredTimeStacks/5);
		amplifier = Mth.clamp(amplifier, 0, 4);
		gui.renderTextureOverlay(graphics.guiGraphics(), Survive.getInstance().location("textures/misc/sleep_overlay_"+(amplifier)+".png"), 0.5F);
		Minecraft.getInstance().getProfiler().pop();
	}

}
