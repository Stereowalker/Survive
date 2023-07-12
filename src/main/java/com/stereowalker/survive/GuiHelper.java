package com.stereowalker.survive;

import java.util.Random;

import org.apache.commons.lang3.mutable.MutableInt;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.TempDisplayMode;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.needs.IRoastedEntity;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.util.ScreenHelper;
import com.stereowalker.unionlib.util.ScreenHelper.ScreenOffset;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class GuiHelper {
	public static final ResourceLocation GUI_ICONS = new ResourceLocation(Survive.MOD_ID, "textures/gui/icons.png");
	@OnlyIn(Dist.CLIENT)
	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("tired", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
			if (Survive.CONFIG.tired_overlay && gui.minecraft.player.hasEffect(SMobEffects.TIREDNESS)) {
				gui.setupOverlayRenderState(true, false);
				GuiHelper.renderTiredOverlay(gui, mStack);
			}
		});
		event.registerAboveAll("heat_stroke", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
			gui.setupOverlayRenderState(true, false);
			GuiHelper.renderHeatStroke(gui, mStack);
		});
		event.registerAboveAll("temperature", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
			if (!gui.minecraft.options.hideGui && Survive.TEMPERATURE_CONFIG.enabled && !Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
				gui.setupOverlayRenderState(true, false);
				GuiHelper.renderTemperature(gui, ScreenOffset.TOP, gui.getCameraPlayer(), mStack, true);
			}
		});
		event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "thirst_level", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
			boolean isMounted = gui.minecraft.player.getVehicle() instanceof LivingEntity;
			if (Survive.THIRST_CONFIG.enabled && !isMounted && !gui.minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
			{
				gui.setupOverlayRenderState(true, false);
				int left = screenWidth / 2 + 91;
				int top = screenHeight - gui.rightHeight;
				renderThirst(gui, mStack, new MutableInt(), left, top, true);
				gui.rightHeight += 10;
			}
		});
		event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "stamina_level", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
			boolean isMounted = gui.minecraft.player.getVehicle() instanceof LivingEntity;
			if (Survive.STAMINA_CONFIG.enabled && !isMounted && !gui.minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
			{
				gui.setupOverlayRenderState(true, false);
				int left = screenWidth / 2 + 91;
				int top = screenHeight - gui.rightHeight;
				MutableInt moveUp = new MutableInt();
				renderEnergyBars(gui, mStack, moveUp, left, top, true);
				gui.rightHeight += moveUp.getValue();
			}
		});
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static void renderTemperature(Gui gui, ScreenOffset position, Player playerentity, GuiGraphics mStack, boolean forgeOverlay) {
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
					mStack.blit(GUI_ICONS, x-3, y-3, 0, 79, 138, 11);
				} else if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp <= -1) {//Hypothermia override
					mStack.blit(GUI_ICONS, x-3, y-3, 0, 90, 138, 11);
				} else {
					mStack.blit(GUI_ICONS, x, y, 3, 64, 132, 5);
					mStack.blit(GUI_ICONS, x, y, 3, 69, 132, 5);
				}
				mStack.blit(GUI_ICONS, x+Mth.floor(displayTemp*44)+63+(displayTemp>0?1:0), y, 1, 74, 4, 5);
			}
			else if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.VERTICAL_BAR)) {
				if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp >= 1) {//Hyperthermia override
					mStack.blit(GUI_ICONS, x-3, y-3, 11, 101, 11, 138);
				} else if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp <= -1) {//Hypothermia override
					mStack.blit(GUI_ICONS, x-3, y-3, 00, 101, 11, 138);
				} else {
					mStack.blit(GUI_ICONS, x, y, 32, 104, 5, 132);
					mStack.blit(GUI_ICONS, x, y, 27, 104, 5, 132);
				}
				mStack.blit(GUI_ICONS, x, y-Mth.floor(displayTemp*44)+63-(displayTemp>0?1:0), 22, 104, 5, 5);
			}
			else if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.NUMBERS)) {
				if (Survive.TEMPERATURE_CONFIG.displayTempInFahrenheit) {
					s = SurviveEntityStats.getTemperatureStats(playerentity).getFahrenheit()+" °F";
				}
				if (displayTemp >= 1) {
					mStack.drawString(Minecraft.getInstance().font, s, x, y, ChatFormatting.GOLD.getColor(), false);
				} else if (displayTemp <= -1) {
					mStack.drawString(Minecraft.getInstance().font, s, x, y, ChatFormatting.BLUE.getColor(), false);
				} else {
					mStack.drawString(Minecraft.getInstance().font, s, x, y, ChatFormatting.GRAY.getColor(), false);
				}
			}
		}
		if (Survive.CONFIG.nutrition_enabled && (playerentity.getMainHandItem().isEdible() || playerentity.getOffhandItem().isEdible())) {
			mStack.drawString(Minecraft.getInstance().font, "Carbs = "+((IRealisticEntity)playerentity).getNutritionData().getCarbLevel(), 0, 0, ChatFormatting.GRAY.getColor(), false);
			mStack.drawString(Minecraft.getInstance().font, "Protein = "+((IRealisticEntity)playerentity).getNutritionData().getProteinLevel(), 0, 10, ChatFormatting.GRAY.getColor(), false);
		}
		Minecraft.getInstance().getProfiler().pop();
//		if (!forgeOverlay) {
//			RenderSystem.setShader(GameRenderer::getPositionTexShader);
//			RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
//			RenderSystem.enableBlend();
//			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
//		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderThirst(Gui gui, GuiGraphics mStack, MutableInt moveUp, int j1, int k1, boolean forgeOverlay) {
		Player player = (Player)gui.minecraft.getCameraEntity();
		IRealisticEntity realisticPlayer = (IRealisticEntity)player;
		int waterL = (int) realisticPlayer.getWaterData().getWaterLevel();
		gui.minecraft.getProfiler().push("thirst");
//		if (!forgeOverlay) {
//			RenderSystem.setShader(GameRenderer::getPositionTexShader);
//			RenderSystem.setShaderTexture(0, GUI_ICONS);
//		}
		for(int k6 = 0; k6 < 10; ++k6) {
			int i7 = k1;
			int k7 = 16;
			int i8 = 0;
			if (player.hasEffect(SMobEffects.THIRST)) {
				k7 += 36;
				i8 = 13;
			}

			if (realisticPlayer.getWaterData().getHydrationLevel() <= 0.0F && gui.tickCount % (waterL * 3 + 1) == 0) {
				i7 = k1 + (gui.random.nextInt(3) - 1);
			}

			int k8 = j1 - k6 * 8 - 9;
			mStack.blit(GUI_ICONS, k8, i7 - moveUp.getValue(), 16 + i8 * 9, 54, 9, 9);
			if (k6 * 2 + 1 < waterL) {
				mStack.blit(GUI_ICONS, k8, i7 - moveUp.getValue(), k7 + 36, 54, 9, 9);
			}

			if (k6 * 2 + 1 == waterL) {
				mStack.blit(GUI_ICONS, k8, i7 - moveUp.getValue(), k7 + 45, 54, 9, 9);
			}
		}
		moveUp.add(-10);
		gui.minecraft.getProfiler().pop();
//		if (!forgeOverlay) {
//			RenderSystem.setShader(GameRenderer::getPositionTexShader);
//			RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
//		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderEnergyBars(Gui gui, GuiGraphics mStack, MutableInt moveUp, int j1, int k1, boolean forgeOverlay) {
		Random rand = new Random();
		Player player = (Player)gui.minecraft.getCameraEntity();
		float maxStamina = (float) player.getAttributeValue(SAttributes.MAX_STAMINA);
		int l = (int) SurviveEntityStats.getEnergyStats(player).getEnergyLevel();
		if (SurviveEntityStats.getEnergyStats(player).isExhausted()) l = (int) SurviveEntityStats.getEnergyStats(player).getReserveLevel();
		Minecraft.getInstance().getProfiler().push("energy");
		if (!forgeOverlay) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, GUI_ICONS);
		}
		for (int i = 0; i < Mth.ceil((float)maxStamina/20.0F); i++) {
			for(int k6 = 0; k6 < 10; ++k6) {
				int i7 = k1;
				int k7 = 16;
				int i8 = 0;
				if (SurviveEntityStats.getEnergyStats(player).isExhausted()) {
					k7 += 36;
					i8 = 13;
				}
				
				if (player.getFoodData().getSaturationLevel() <= 0.0F && gui.getGuiTicks() % (l * 3 + 1) == 0) {
					i7 = k1 + (rand.nextInt(3) - 1);
				}
				
				int k8 = j1 - k6 * 8 - 9;
				if ((k6 * 2 + 1) + (20*i) < Mth.floor(maxStamina)+1) {
					mStack.blit(GUI_ICONS, k8, i7 - moveUp.getValue(), 16 + i8 * 9, 36, 9, 9);
				}
				if ((k6 * 2 + 1) + (20*i) < l) {
					mStack.blit(GUI_ICONS, k8, i7 - moveUp.getValue(), k7 + 36, 36, 9, 9);
				}
				
				if ((k6 * 2 + 1) + (20*i)  == l) {
					mStack.blit(GUI_ICONS, k8, i7 - moveUp.getValue(), k7 + 45, 36, 9, 9);
				}
			}
			moveUp.add(10);
		}
		Minecraft.getInstance().getProfiler().pop();
//		if (!forgeOverlay) {
//			RenderSystem.setShader(GameRenderer::getPositionTexShader);
//			RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
//		}
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static void renderTiredOverlay(Gui gui, GuiGraphics graphics) {
		Minecraft.getInstance().getProfiler().push("tired");
		int amplifier = Minecraft.getInstance().player.getEffect(SMobEffects.TIREDNESS).getAmplifier() + 1;
		amplifier/=(Survive.CONFIG.tiredTimeStacks/5);
		amplifier = Mth.clamp(amplifier, 0, 4);
		gui.renderTextureOverlay(graphics, Survive.getInstance().location("textures/misc/sleep_overlay_"+(amplifier)+".png"), 0.5F);
		Minecraft.getInstance().getProfiler().pop();
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderHeatStroke(Gui gui, GuiGraphics graphics)
	{
		if (((IRoastedEntity)gui.minecraft.player).getTicksRoasted() > 0) {
			gui.renderTextureOverlay(graphics, Survive.getInstance().location("textures/misc/burning_overlay.png"), ((IRoastedEntity)gui.minecraft.player).getPercentRoasted());
		}
	}
}
