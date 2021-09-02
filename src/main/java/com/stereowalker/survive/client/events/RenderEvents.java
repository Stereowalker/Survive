package com.stereowalker.survive.client.events;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.entity.TempDisplayMode;
import com.stereowalker.survive.entity.ai.SAttributes;
import com.stereowalker.survive.potion.SEffects;
import com.stereowalker.survive.util.NutritionStats;
import com.stereowalker.unionlib.util.ScreenHelper;
import com.stereowalker.unionlib.util.ScreenHelper.ScreenOffset;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
public class RenderEvents {
	public static final ResourceLocation GUI_ICONS = new ResourceLocation(Survive.MOD_ID, "textures/gui/icons.png");

	static Minecraft mc = Minecraft.getInstance();

	static long healthUpdateCounter;
	static int playerHealth;
	static long lastSystemTime;
	static int lastPlayerHealth;

	static IngameGui gui() {
		return mc.ingameGUI;
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void renderGameOverlay(RenderGameOverlayEvent event) {
		Random rand = new Random();
		//Render Mana Bar
		PlayerEntity playerentity = gui().getRenderViewPlayer();
		//Access Transform IngameGui#getMountEntity
		LivingEntity living;
		if (playerentity != null) {
			Entity entity = playerentity.getRidingEntity();
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
			ModifiableAttributeInstance iattributemaxHealth = playerentity.getAttribute(Attributes.MAX_HEALTH);
			int i1 = gui().scaledWidth / 2 - 91;
			int j1 = gui().scaledWidth / 2 + 91;
			int k1 = gui().scaledHeight - 39;
			float f = (float)iattributemaxHealth.getValue();
			int l1 = MathHelper.ceil(playerentity.getAbsorptionAmount());
			int i2 = MathHelper.ceil((f + (float)l1) / 2.0F / 10.0F);
			int j2 = Math.max(10 - (i2 - 2), 3);
			boolean needsAir = false;
			int l6 = playerentity.getAir();
			int j7 = playerentity.getMaxAir();
			if (playerentity.areEyesInFluid(FluidTags.WATER) || l6 < j7) {
				int j8 = MathHelper.ceil((double)(l6 - 2) * 10.0D / (double)j7);
				int l8 = MathHelper.ceil((double)l6 * 10.0D / (double)j7) - j8;
				
				for(int k5 = 0; k5 < j8 + l8; ++k5) {
					if (k5 < j8) {
						needsAir = true;
					} else {
						needsAir = true;
					}
				}
			}
			if (Config.enable_temperature && !Config.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
				renderTemperature(ScreenOffset.TOP, playerentity, event.getMatrixStack());
			}
			
			
			//Render Hypothermia Hearts
			int i = MathHelper.ceil(playerentity.getHealth());
			boolean flag = healthUpdateCounter > (long)gui().getTicks() && (healthUpdateCounter - (long)gui().getTicks()) / 3L % 2L == 1L;
			long j = Util.milliTime();
			if (i < playerHealth && playerentity.hurtResistantTime > 0) {
				lastSystemTime = j;
				healthUpdateCounter = (long)(gui().getTicks() + 20);
			} else if (i > playerHealth && playerentity.hurtResistantTime > 0) {
				lastSystemTime = j;
				healthUpdateCounter = (long)(gui().getTicks() + 10);
			}
			
			if (j - lastSystemTime > 1000L) {
				playerHealth = i;
				lastPlayerHealth = i;
				lastSystemTime = j;
			}
			
			playerHealth = i;
			int k = lastPlayerHealth;
			rand.setSeed((long)(gui().getTicks() * 312871));
			int i3 = l1;
			int k3 = -1;
			if (playerentity.isPotionActive(Effects.REGENERATION)) {
				k3 = gui().getTicks() % MathHelper.ceil(f + 5.0F);
			}
			
			if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH && (Config.tempDisplayMode.equals(TempDisplayMode.HOTBAR) || !Config.tempEffects)) {
				mc.getProfiler().startSection("health");
				for(int l5 = MathHelper.ceil((f + (float)l1) / 2.0F) - 1; l5 >= 0; --l5) {
					int i6 = 16;
					if (playerentity.isPotionActive(SEffects.HYPOTHERMIA)) {
						i6 += 36;
					} else if (playerentity.isPotionActive(SEffects.HYPERTHERMIA)) {
						i6 += 72;
					}
					
					int j4 = 0;
					if (flag) {
						j4 = 1;
					}
					
					int k4 = MathHelper.ceil((float)(l5 + 1) / 10.0F) - 1;
					int l4 = i1 + l5 % 10 * 8;
					int i5 = k1 - k4 * j2;
					if (i <= 4) {
						i5 += rand.nextInt(2);
					}
					
					if (i3 <= 0 && l5 == k3) {
						i5 -= 2;
					}
					
					int j5 = 0;
					if (playerentity.world.getWorldInfo().isHardcore()) {
						j5 = 5;
					}
					if (playerentity.isPotionActive(SEffects.HYPERTHERMIA) || playerentity.isPotionActive(SEffects.HYPOTHERMIA)) {
						mc.getTextureManager().bindTexture(GUI_ICONS);
						gui().blit(event.getMatrixStack(), l4, i5, 16 + j4 * 9, 9 * j5, 9, 9);
						if (flag) {
							if (l5 * 2 + 1 < k) {
								gui().blit(event.getMatrixStack(), l4, i5, i6 + 54, 9 * j5, 9, 9);
							}
							
							if (l5 * 2 + 1 == k) {
								gui().blit(event.getMatrixStack(), l4, i5, i6 + 63, 9 * j5, 9, 9);
							}
						}
						
						if (i3 > 0) {
							if (i3 == l1 && l1 % 2 == 1) {
								gui().blit(event.getMatrixStack(), l4, i5, i6 + 153, 9 * j5, 9, 9);
								--i3;
							} else {
								gui().blit(event.getMatrixStack(), l4, i5, i6 + 144, 9 * j5, 9, 9);
								i3 -= 2;
							}
						} else {
							if (l5 * 2 + 1 < i) {
								gui().blit(event.getMatrixStack(), l4, i5, i6 + 36, 9 * j5, 9, 9);
							}
							
							if (l5 * 2 + 1 == i) {
								gui().blit(event.getMatrixStack(), l4, i5, i6 + 45, 9 * j5, 9, 9);
							}
						}
					}
					mc.getProfiler().endSection();
					mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
				}
			}
			if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
				if (Config.enable_thirst) {
					LivingEntity livingentity = living;
					int moveUp = needsAir ? -10 : 0;
					int l = (int) SurviveEntityStats.getWaterStats(playerentity).getWaterLevel();
					int j6 = getRenderMountHealth(livingentity);
					if (j6 == 0) {
						mc.getProfiler().startSection("thirst");
						mc.getTextureManager().bindTexture(GUI_ICONS);
						for(int k6 = 0; k6 < 10; ++k6) {
							int i7 = k1;
							int k7 = 16;
							int i8 = 0;
							if (playerentity.isPotionActive(SEffects.THIRST)) {
								k7 += 36;
								i8 = 13;
							}
							
							if (SurviveEntityStats.getWaterStats(playerentity).getHydrationLevel() <= 0.0F && gui().getTicks() % (l * 3 + 1) == 0) {
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
						mc.getProfiler().endSection();
						mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
					}
				}
				//Energy
				if (Config.enable_stamina) {
					renderEnergyBars(event.getMatrixStack(), playerentity, needsAir, j1, k1, living);
				}
			}
			if (event.getType() == RenderGameOverlayEvent.ElementType.VIGNETTE) {
				if (Config.tired_overlay) {
					if (playerentity.isPotionActive(SEffects.TIREDNESS)) {
						mc.getProfiler().startSection("tired");
						int amplifier = playerentity.getActivePotionEffect(SEffects.TIREDNESS).getAmplifier() + 1;
						amplifier/=(Config.tiredTimeStacks/5);
						amplifier = MathHelper.clamp(amplifier, 0, 4);
						renderTiredOverlay(amplifier);
						mc.getProfiler().endSection();
					}
				}
			}
			
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			RenderSystem.disableAlphaTest();
		}
	}

	@SuppressWarnings("deprecation")
	protected static void renderTiredOverlay(int amp) {
		int scaledWidth = mc.getMainWindow().getScaledWidth();
		int scaledHeight = mc.getMainWindow().getScaledHeight();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableAlphaTest();
		mc.getTextureManager().bindTexture(Survive.getInstance().location("textures/misc/sleep_overlay_"+(amp)+".png"));
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(0.0D, (double)scaledHeight, -90.0D).tex(0.0F, 1.0F).endVertex();
		bufferbuilder.pos((double)scaledWidth, (double)scaledHeight, -90.0D).tex(1.0F, 1.0F).endVertex();
		bufferbuilder.pos((double)scaledWidth, 0.0D, -90.0D).tex(1.0F, 0.0F).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0F, 0.0F).endVertex();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@SuppressWarnings("deprecation")
	public static void renderTemperature(ScreenOffset position, PlayerEntity playerentity, MatrixStack matrixStack) {
		int x = ScreenHelper.getXOffset(position) + Config.tempXLoc;
		int y = ScreenHelper.getYOffset(position) + Config.tempYLoc;
		mc.getProfiler().startSection("temperature");
		mc.getTextureManager().bindTexture(GUI_ICONS);
		double rawTemperature = SurviveEntityStats.getTemperatureStats(playerentity).getTemperatureLevel();
		double tempLocation = rawTemperature - Survive.DEFAULT_TEMP;
		double displayTemp = 0;
		if (tempLocation > 0) {
			double maxTemp = 0.0D;
			if (playerentity.getAttribute(SAttributes.HEAT_RESISTANCE) != null) {
				maxTemp = playerentity.getAttributeValue(SAttributes.HEAT_RESISTANCE);
			}
			double div = tempLocation / maxTemp;
			displayTemp = MathHelper.clamp(div, 0, 1.0D+(28.0D/63.0D));
		}
		if (tempLocation < 0) {
			double maxTemp = 0.0D;
			if (playerentity.getAttribute(SAttributes.COLD_RESISTANCE) != null) {
				maxTemp = playerentity.getAttributeValue(SAttributes.COLD_RESISTANCE);
			}
			double div = tempLocation / maxTemp;
			displayTemp = MathHelper.clamp(div, -1.0D-(28.0D/63.0D), 0);
		}
		if (Config.tempDisplayMode.equals(TempDisplayMode.HORIZONTAL_BAR)) {
			if (Config.tempEffects && displayTemp >= 1) {//Hyperthermia override
				if(Minecraft.isGuiEnabled() && mc.playerController.gameIsSurvivalOrAdventure())gui().blit(matrixStack, x-3, y-3, 0, 79, 138, 11);
			} else if (Config.tempEffects && displayTemp <= -1) {//Hypothermia override
				if(Minecraft.isGuiEnabled() && mc.playerController.gameIsSurvivalOrAdventure())gui().blit(matrixStack, x-3, y-3, 0, 90, 138, 11);
			} else {
				if(Minecraft.isGuiEnabled() && mc.playerController.gameIsSurvivalOrAdventure())gui().blit(matrixStack, x, y, 3, 64, 132, 5);
				if(Minecraft.isGuiEnabled() && mc.playerController.gameIsSurvivalOrAdventure())gui().blit(matrixStack, x, y, 3, 69, 132, 5);
			}
			if(Minecraft.isGuiEnabled() && mc.playerController.gameIsSurvivalOrAdventure())gui().blit(matrixStack, x+MathHelper.floor(displayTemp*44)+63+(displayTemp>0?1:0), y, 3, 74, 5, 5);
		}
		if (Config.tempDisplayMode.equals(TempDisplayMode.VERTICAL_BAR)) {
			if (Config.tempEffects && displayTemp >= 1) {//Hyperthermia override
				if(Minecraft.isGuiEnabled() && mc.playerController.gameIsSurvivalOrAdventure())gui().blit(matrixStack, x-3, y-3, 11, 101, 11, 138);
			} else if (Config.tempEffects && displayTemp <= -1) {//Hypothermia override
				if(Minecraft.isGuiEnabled() && mc.playerController.gameIsSurvivalOrAdventure())gui().blit(matrixStack, x-3, y-3, 00, 101, 11, 138);
			} else {
				if(Minecraft.isGuiEnabled() && mc.playerController.gameIsSurvivalOrAdventure())gui().blit(matrixStack, x, y, 32, 104, 5, 132);
				if(Minecraft.isGuiEnabled() && mc.playerController.gameIsSurvivalOrAdventure())gui().blit(matrixStack, x, y, 27, 104, 5, 132);
			}
			if(Minecraft.isGuiEnabled() && mc.playerController.gameIsSurvivalOrAdventure())gui().blit(matrixStack, x, y-MathHelper.floor(displayTemp*44)+63-(displayTemp>0?1:0), 22, 104, 5, 5);
		}

		int temp = (int) (rawTemperature*100);
		double temperaure = ((double)temp) / 100.0D;
		String s = temperaure+" °C";
		if(Minecraft.isGuiEnabled() && !mc.playerController.isSpectatorMode() && Config.tempDisplayMode.equals(TempDisplayMode.NUMBERS)) {
			if (Config.displayTempInFahrenheit) {
				double rawFTemp = (temperaure * (9.0D/5.0D)) + 32.0D;
				int fTemp = (int) (rawFTemp*100);
				double fTemperaure = ((double)fTemp) / 100.0D;
				s = fTemperaure+" °F";
			}
			if (displayTemp >= 1) {
				mc.fontRenderer.drawStringWithShadow(matrixStack, s, x, y, TextFormatting.GOLD.getColor());
			} else if (displayTemp <= -1) {
				mc.fontRenderer.drawStringWithShadow(matrixStack, s, x, y, TextFormatting.BLUE.getColor());
			} else {
				mc.fontRenderer.drawStringWithShadow(matrixStack, s, x, y, TextFormatting.GRAY.getColor());
			}
		}
		if (Config.nutrition_enabled && (playerentity.getHeldItemMainhand().isFood() || playerentity.getHeldItemOffhand().isFood())) {
			NutritionStats stats = SurviveEntityStats.getNutritionStats(playerentity);
			mc.fontRenderer.drawStringWithShadow(matrixStack, "Carbs = "+stats.getCarbLevel(), 0, 0, TextFormatting.GRAY.getColor());
			mc.fontRenderer.drawStringWithShadow(matrixStack, "Protein = "+stats.getProteinLevel(), 0, 10, TextFormatting.GRAY.getColor());
		}
		mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
		mc.getProfiler().endSection();


		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		RenderSystem.disableAlphaTest();
	}

	public static void renderEnergyBars(MatrixStack matrixStack, PlayerEntity playerentity, boolean needsAir, int j1, int k1, LivingEntity living) {
		Random rand = new Random();
		LivingEntity livingentity = living;
		int moveUp = needsAir ? -10 : 0;
		int thirstEnabled = Config.enable_thirst ? -10 : 0;
		int l = (int) SurviveEntityStats.getEnergyStats(playerentity).getEnergyLevel();
		if (SurviveEntityStats.getEnergyStats(playerentity).isExhausted()) l = (int) SurviveEntityStats.getEnergyStats(playerentity).getReserveLevel();
		//TODO Access transform this IngameGui#getRenderMountHealth
		int j6 = getRenderMountHealth(livingentity);
		if (j6 == 0) {
			mc.getProfiler().startSection("energy");
			mc.getTextureManager().bindTexture(GUI_ICONS);
			for(int k6 = 0; k6 < 10; ++k6) {
				int i7 = k1;
				int k7 = 16;
				int i8 = 0;
				if (SurviveEntityStats.getEnergyStats(playerentity).isExhausted()) {
					k7 += 36;
					i8 = 13;
				}

				if (playerentity.getFoodStats().getSaturationLevel() <= 0.0F && gui().getTicks() % (l * 3 + 1) == 0) {
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
			mc.getProfiler().endSection();
			mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
		}
	}

	private static int getRenderMountHealth(LivingEntity p_212306_1_) {
		if (p_212306_1_ != null && p_212306_1_.isLiving()) {
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
