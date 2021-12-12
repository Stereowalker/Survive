package com.stereowalker.survive.client.events;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.TempDisplayMode;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class RenderEvents {
	public static final ResourceLocation GUI_ICONS = new ResourceLocation(Survive.MOD_ID, "textures/gui/icons.png");

	static Minecraft mc = Minecraft.getInstance();

	static Gui gui() {
		return mc.gui;
	}

	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent event) {
		Random rand = new Random();
		//Render Mana Bar
		Player playerentity = gui().getCameraPlayer();
		//Access Transform Gui#getMountEntity
		LivingEntity living;
		if (playerentity != null) {
			Entity entity = playerentity.getVehicle();
			if (entity == null) {
				living = null;
			}

			if (entity instanceof LivingEntity) {
				living = (LivingEntity)entity;
			}
		}
		//
		living = null;
		if (playerentity != null) {
			RenderSystem.enableBlend();
			AttributeInstance iattributemaxHealth = playerentity.getAttribute(Attributes.MAX_HEALTH);
			int i1 = gui().screenWidth / 2 - 91;
			int j1 = gui().screenWidth / 2 + 91;
			int k1 = gui().screenHeight - 39;
			float f = (float)iattributemaxHealth.getValue();
			int l1 = Mth.ceil(playerentity.getAbsorptionAmount());
			int i2 = Mth.ceil((f + (float)l1) / 2.0F / 10.0F);
			int j2 = Math.max(10 - (i2 - 2), 3);
			boolean needsAir = false;
			int l6 = playerentity.getAirSupply();
			int j7 = playerentity.getMaxAirSupply();
			if (playerentity.isEyeInFluid(FluidTags.WATER) || l6 < j7) {
				int j8 = Mth.ceil((double)(l6 - 2) * 10.0D / (double)j7);
				int l8 = Mth.ceil((double)l6 * 10.0D / (double)j7) - j8;

				for(int k5 = 0; k5 < j8 + l8; ++k5) {
					if (k5 < j8) {
						needsAir = true;
					} else {
						needsAir = true;
					}
				}
			}
			if (Survive.TEMPERATURE_CONFIG.enabled && !Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
				renderTemperature(ScreenOffset.TOP, playerentity, event.getMatrixStack());
			}
//			TODO: if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
				if (Survive.THIRST_CONFIG.enabled && !(playerentity.isCreative() || playerentity.isSpectator())) {
					LivingEntity livingentity = living;
					int moveUp = needsAir ? -10 : 0;
					int l = (int) SurviveEntityStats.getWaterStats(playerentity).getWaterLevel();
					int j6 = getRenderMountHealth(livingentity);
					if (j6 == 0) {
						mc.getProfiler().push("thirst");
						RenderSystem.setShader(GameRenderer::getPositionTexShader);
						RenderSystem.setShaderTexture(0, GUI_ICONS);
						for(int k6 = 0; k6 < 10; ++k6) {
							int i7 = k1;
							int k7 = 16;
							int i8 = 0;
							if (playerentity.hasEffect(SEffects.THIRST)) {
								k7 += 36;
								i8 = 13;
							}

							if (SurviveEntityStats.getWaterStats(playerentity).getHydrationLevel() <= 0.0F && gui().getGuiTicks() % (l * 3 + 1) == 0) {
								i7 = k1 + (rand.nextInt(3) - 1);
							}

							int k8 = j1 - k6 * 8 - 9;
							gui().blit(event.getMatrixStack(), k8, i7 - 10 + moveUp, 16 + i8 * 9, 54, 9, 9);
							if (k6 * 2 + 1 < l) {
								gui().blit(event.getMatrixStack(), k8, i7 - 10 + moveUp, k7 + 36, 54, 9, 9);
							}

							if (k6 * 2 + 1 == l) {
								gui().blit(event.getMatrixStack(), k8, i7 - 10 + moveUp, k7 + 45, 54, 9, 9);
							}
						}
						mc.getProfiler().pop();
						RenderSystem.setShader(GameRenderer::getPositionTexShader);
						RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
					}
				}
				//Energy
				if (Survive.CONFIG.enable_stamina && !(playerentity.isCreative() || playerentity.isSpectator())) {
					renderEnergyBars(event.getMatrixStack(), playerentity, needsAir, j1, k1, living);
				}
//			}

			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		}
	}


	public static void renderTemperature(ScreenOffset position, Player playerentity, PoseStack matrixStack) {
		int x = ScreenHelper.getXOffset(position) + Survive.TEMPERATURE_CONFIG.tempXLoc;
		int y = ScreenHelper.getYOffset(position) + Survive.TEMPERATURE_CONFIG.tempYLoc;
		mc.getProfiler().push("temperature");
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
				if(Minecraft.renderNames() && mc.gameMode.hasExperience())gui().blit(matrixStack, x-3, y-3, 0, 79, 138, 11);
			} else if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp <= -1) {//Hypothermia override
				if(Minecraft.renderNames() && mc.gameMode.hasExperience())gui().blit(matrixStack, x-3, y-3, 0, 90, 138, 11);
			} else {
				if(Minecraft.renderNames() && mc.gameMode.hasExperience())gui().blit(matrixStack, x, y, 3, 64, 132, 5);
				if(Minecraft.renderNames() && mc.gameMode.hasExperience())gui().blit(matrixStack, x, y, 3, 69, 132, 5);
			}
			if(Minecraft.renderNames() && mc.gameMode.hasExperience())gui().blit(matrixStack, x+Mth.floor(displayTemp*44)+63+(displayTemp>0?1:0), y, 3, 74, 5, 5);
		}
		if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.VERTICAL_BAR)) {
			if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp >= 1) {//Hyperthermia override
				if(Minecraft.renderNames() && mc.gameMode.hasExperience())gui().blit(matrixStack, x-3, y-3, 11, 101, 11, 138);
			} else if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp <= -1) {//Hypothermia override
				if(Minecraft.renderNames() && mc.gameMode.hasExperience())gui().blit(matrixStack, x-3, y-3, 00, 101, 11, 138);
			} else {
				if(Minecraft.renderNames() && mc.gameMode.hasExperience())gui().blit(matrixStack, x, y, 32, 104, 5, 132);
				if(Minecraft.renderNames() && mc.gameMode.hasExperience())gui().blit(matrixStack, x, y, 27, 104, 5, 132);
			}
			if(Minecraft.renderNames() && mc.gameMode.hasExperience())gui().blit(matrixStack, x, y-Mth.floor(displayTemp*44)+63-(displayTemp>0?1:0), 22, 104, 5, 5);
		}

		int temp = (int) (rawTemperature*100);
		double temperaure = ((double)temp) / 100.0D;
		String s = temperaure+" °C";
		if(Minecraft.renderNames() && !mc.gameMode.isAlwaysFlying() && Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.NUMBERS)) {
			if (Survive.TEMPERATURE_CONFIG.displayTempInFahrenheit) {
				double rawFTemp = (temperaure * (9.0D/5.0D)) + 32.0D;
				int fTemp = (int) (rawFTemp*100);
				double fTemperaure = ((double)fTemp) / 100.0D;
				s = fTemperaure+" °F";
			}
			if (displayTemp >= 1) {
				mc.font.drawShadow(matrixStack, s, x, y, ChatFormatting.GOLD.getColor());
			} else if (displayTemp <= -1) {
				mc.font.drawShadow(matrixStack, s, x, y, ChatFormatting.BLUE.getColor());
			} else {
				mc.font.drawShadow(matrixStack, s, x, y, ChatFormatting.GRAY.getColor());
			}
		}
		if (Survive.CONFIG.nutrition_enabled && (playerentity.getMainHandItem().isEdible() || playerentity.getOffhandItem().isEdible())) {
			NutritionData stats = SurviveEntityStats.getNutritionStats(playerentity);
			mc.font.drawShadow(matrixStack, "Carbs = "+stats.getCarbLevel(), 0, 0, ChatFormatting.GRAY.getColor());
			mc.font.drawShadow(matrixStack, "Protein = "+stats.getProteinLevel(), 0, 10, ChatFormatting.GRAY.getColor());
		}
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		mc.getProfiler().pop();


		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
	}

	public static void renderEnergyBars(PoseStack matrixStack, Player playerentity, boolean needsAir, int j1, int k1, LivingEntity living) {
		Random rand = new Random();
		LivingEntity livingentity = living;
		int moveUp = needsAir ? -10 : 0;
		int thirstEnabled = Survive.THIRST_CONFIG.enabled ? -10 : 0;
		int l = (int) SurviveEntityStats.getEnergyStats(playerentity).getEnergyLevel();
		if (SurviveEntityStats.getEnergyStats(playerentity).isExhausted()) l = (int) SurviveEntityStats.getEnergyStats(playerentity).getReserveLevel();
		//TODO Access transform this Gui#getRenderMountHealth
		int j6 = getRenderMountHealth(livingentity);
		if (j6 == 0) {
			mc.getProfiler().push("energy");
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, GUI_ICONS);
			for(int k6 = 0; k6 < 10; ++k6) {
				int i7 = k1;
				int k7 = 16;
				int i8 = 0;
				if (SurviveEntityStats.getEnergyStats(playerentity).isExhausted()) {
					k7 += 36;
					i8 = 13;
				}

				if (playerentity.getFoodData().getSaturationLevel() <= 0.0F && gui().getGuiTicks() % (l * 3 + 1) == 0) {
					i7 = k1 + (rand.nextInt(3) - 1);
				}

				int k8 = j1 - k6 * 8 - 9;
				gui().blit(matrixStack, k8, i7 - 10 + moveUp + thirstEnabled, 16 + i8 * 9, 36, 9, 9);
				if (k6 * 2 + 1 < l) {
					gui().blit(matrixStack, k8, i7 - 10 + moveUp + thirstEnabled, k7 + 36, 36, 9, 9);
				}

				if (k6 * 2 + 1 == l) {
					gui().blit(matrixStack, k8, i7 - 10 + moveUp + thirstEnabled, k7 + 45, 36, 9, 9);
				}
			}
			mc.getProfiler().pop();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		}
	}

	private static int getRenderMountHealth(LivingEntity p_212306_1_) {
		if (p_212306_1_ != null && p_212306_1_.showVehicleHealth()) {
			float f = p_212306_1_.getMaxHealth();
			int i = (int)(f + 0.5F) / 2;
			if (i > 30) {
				i = 30;
			}

			return i;
		} else {
			return 0;
		}
	}
}
