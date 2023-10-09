package com.stereowalker.survive;

import java.util.Random;

import org.apache.commons.lang3.mutable.MutableInt;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;

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

}
