package com.stereowalker.survive;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.TempDisplayMode;
import com.stereowalker.survive.needs.IRoastedEntity;
import com.stereowalker.survive.needs.NutritionData;
import com.stereowalker.survive.world.effect.SEffects;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.unionlib.util.ScreenHelper;
import com.stereowalker.unionlib.util.ScreenHelper.ScreenOffset;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.OverlayRegistry;

@OnlyIn(Dist.CLIENT)
public class GuiHelper {
	public static final ResourceLocation GUI_ICONS = new ResourceLocation(Survive.MOD_ID, "textures/gui/icons.png");
	@OnlyIn(Dist.CLIENT)
	public static void registerOverlays() {
		OverlayRegistry.registerOverlayTop("Tired", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
	        gui.setupOverlayRenderState(true, false);
	        GuiHelper.renderTiredOverlay(gui);
	    });
		OverlayRegistry.registerOverlayTop("Heat Stroke", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
	        gui.setupOverlayRenderState(true, false);
	        GuiHelper.renderHeatStroke(gui);
	    });
		OverlayRegistry.registerOverlayTop("Temperature", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
	        gui.setupOverlayRenderState(true, false);
	        if (Survive.TEMPERATURE_CONFIG.enabled && !Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
	        	GuiHelper.renderTemperature(gui, ScreenOffset.TOP, gui.getCameraPlayer(), mStack);
			}
	    });
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static void renderTemperature(Gui gui, ScreenOffset position, Player playerentity, PoseStack matrixStack) {
		int x = ScreenHelper.getXOffset(position) + Survive.TEMPERATURE_CONFIG.tempXLoc;
		int y = ScreenHelper.getYOffset(position) + Survive.TEMPERATURE_CONFIG.tempYLoc;
		Minecraft.getInstance().getProfiler().push("temperature");
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GUI_ICONS);
		double rawTemperature = SurviveEntityStats.getTemperatureStats(playerentity).getTemperatureLevel();
		double tempLocation = rawTemperature - Survive.DEFAULT_TEMP;
		double displayTemp = 0;
		if (tempLocation > 0) {
			double maxTemp = 0.0D;
			if (playerentity.getAttribute(SAttributes.HEAT_RESISTANCE) != null) {
				maxTemp = playerentity.getAttributeValue(SAttributes.HEAT_RESISTANCE);
			}
			double div = tempLocation / maxTemp;
			displayTemp = Mth.clamp(div, 0, 1.0D+(28.0D/63.0D));
		}
		if (tempLocation < 0) {
			double maxTemp = 0.0D;
			if (playerentity.getAttribute(SAttributes.COLD_RESISTANCE) != null) {
				maxTemp = playerentity.getAttributeValue(SAttributes.COLD_RESISTANCE);
			}
			double div = tempLocation / maxTemp;
			displayTemp = Mth.clamp(div, -1.0D-(28.0D/63.0D), 0);
		}
		if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HORIZONTAL_BAR)) {
			if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp >= 1) {//Hyperthermia override
				if(Minecraft.renderNames() && Minecraft.getInstance().gameMode.hasExperience())gui.blit(matrixStack, x-3, y-3, 0, 79, 138, 11);
			} else if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp <= -1) {//Hypothermia override
				if(Minecraft.renderNames() && Minecraft.getInstance().gameMode.hasExperience())gui.blit(matrixStack, x-3, y-3, 0, 90, 138, 11);
			} else {
				if(Minecraft.renderNames() && Minecraft.getInstance().gameMode.hasExperience())gui.blit(matrixStack, x, y, 3, 64, 132, 5);
				if(Minecraft.renderNames() && Minecraft.getInstance().gameMode.hasExperience())gui.blit(matrixStack, x, y, 3, 69, 132, 5);
			}
			if(Minecraft.renderNames() && Minecraft.getInstance().gameMode.hasExperience())gui.blit(matrixStack, x+Mth.floor(displayTemp*44)+63+(displayTemp>0?1:0), y, 3, 74, 5, 5);
		}
		if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.VERTICAL_BAR)) {
			if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp >= 1) {//Hyperthermia override
				if(Minecraft.renderNames() && Minecraft.getInstance().gameMode.hasExperience())gui.blit(matrixStack, x-3, y-3, 11, 101, 11, 138);
			} else if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp <= -1) {//Hypothermia override
				if(Minecraft.renderNames() && Minecraft.getInstance().gameMode.hasExperience())gui.blit(matrixStack, x-3, y-3, 00, 101, 11, 138);
			} else {
				if(Minecraft.renderNames() && Minecraft.getInstance().gameMode.hasExperience())gui.blit(matrixStack, x, y, 32, 104, 5, 132);
				if(Minecraft.renderNames() && Minecraft.getInstance().gameMode.hasExperience())gui.blit(matrixStack, x, y, 27, 104, 5, 132);
			}
			if(Minecraft.renderNames() && Minecraft.getInstance().gameMode.hasExperience())gui.blit(matrixStack, x, y-Mth.floor(displayTemp*44)+63-(displayTemp>0?1:0), 22, 104, 5, 5);
		}

		int temp = (int) (rawTemperature*100);
		double temperaure = ((double)temp) / 100.0D;
		String s = temperaure+" °C";
		if(Minecraft.renderNames() && !Minecraft.getInstance().gameMode.isAlwaysFlying() && Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.NUMBERS)) {
			if (Survive.TEMPERATURE_CONFIG.displayTempInFahrenheit) {
				double rawFTemp = (temperaure * (9.0D/5.0D)) + 32.0D;
				int fTemp = (int) (rawFTemp*100);
				double fTemperaure = ((double)fTemp) / 100.0D;
				s = fTemperaure+" °F";
			}
			if (displayTemp >= 1) {
				Minecraft.getInstance().font.drawShadow(matrixStack, s, x, y, ChatFormatting.GOLD.getColor());
			} else if (displayTemp <= -1) {
				Minecraft.getInstance().font.drawShadow(matrixStack, s, x, y, ChatFormatting.BLUE.getColor());
			} else {
				Minecraft.getInstance().font.drawShadow(matrixStack, s, x, y, ChatFormatting.GRAY.getColor());
			}
		}
		if (Survive.CONFIG.nutrition_enabled && (playerentity.getMainHandItem().isEdible() || playerentity.getOffhandItem().isEdible())) {
			NutritionData stats = SurviveEntityStats.getNutritionStats(playerentity);
			Minecraft.getInstance().font.drawShadow(matrixStack, "Carbs = "+stats.getCarbLevel(), 0, 0, ChatFormatting.GRAY.getColor());
			Minecraft.getInstance().font.drawShadow(matrixStack, "Protein = "+stats.getProteinLevel(), 0, 10, ChatFormatting.GRAY.getColor());
		}
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		Minecraft.getInstance().getProfiler().pop();


		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
	}
	
	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static void renderTiredOverlay(Gui gui) {
		if (Survive.CONFIG.tired_overlay) {
			if (Minecraft.getInstance().player.hasEffect(SEffects.TIREDNESS)) {
				Minecraft.getInstance().getProfiler().push("tired");
				int amplifier = Minecraft.getInstance().player.getEffect(SEffects.TIREDNESS).getAmplifier() + 1;
				amplifier/=(Survive.CONFIG.tiredTimeStacks/5);
				amplifier = Mth.clamp(amplifier, 0, 4);
				gui.renderTextureOverlay(Survive.getInstance().location("textures/misc/sleep_overlay_"+(amplifier)+".png"), 0.5F);
				Minecraft.getInstance().getProfiler().pop();
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
    public static void renderHeatStroke(Gui gui)
    {
        if (((IRoastedEntity)gui.minecraft.player).getTicksRoasted() > 0) {
        	gui.renderTextureOverlay(Survive.getInstance().location("textures/misc/burning_overlay.png"), ((IRoastedEntity)gui.minecraft.player).getPercentRoasted());
        }
    }
}
