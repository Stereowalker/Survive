package com.stereowalker.survive.mixins.client;

import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.client.gui.SurviveHeartType;
import com.stereowalker.survive.core.TempDisplayMode;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.unionlib.util.math.Color;
import com.stereowalker.unionlib.util.math.MutableColor;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

@Mixin(Gui.class)
public abstract class GuiMixin {
	@Shadow @Final public RandomSource random;
	@Shadow public Player getCameraPlayer() {return null;}

	MutableColor hotbarColor = Color.WHITE.toMutable();
//	@Inject(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
	@WrapOperation(method = "extractItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
	public void hotbarColor(GuiGraphicsExtractor graphics, RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height, Operation<Void> original) {
		Player playerentity = this.getCameraPlayer();
		if (Survive.TEMPERATURE_CONFIG.enabled && Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
			double displayTemp = ((IRealisticEntity)playerentity).temperatureData().getDisplayTemperature();

			float heatTemp = (float) (1.0F - displayTemp);
			float coldTemp = (float) (1.0F + displayTemp);
			float whiteTemp = (float) ((1.0F - Math.abs(displayTemp))/2 + 0.5F);
//			guiGraphics.setColor(coldTemp, whiteTemp, heatTemp, 1.0F);
			hotbarColor.r(Math.clamp(coldTemp, 0f, 1f));
			hotbarColor.g(Math.clamp(whiteTemp, 0f, 1f));
			hotbarColor.b(Math.clamp(heatTemp, 0f, 1f));
			graphics.blitSprite(renderPipeline, location, x, y, width, height, hotbarColor.toIntARGB());
		}
		else {
			original.call(graphics, renderPipeline, location, x, y, width, height);
		}
	}


//	@Inject(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
//	public void resetHotbarColor(GuiGraphicsExtractor guiGraphics, DeltaTracker pDeltaTracker, CallbackInfo ci) {
//		guiGraphics.setColor(1f, 1f, 1f, 1f);
//	}

//	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void addThirstAndOthers(GuiGraphicsExtractor pPoseStack, CallbackInfo ci, Player player, int i, boolean flag, long j, int k, FoodData fooddata, int l, int i1, int j1, int k1, float f, int l1, int i2, int j2, int k2, int l2, int i3, int j3, LivingEntity livingentity, int k5, int l5, int i6) {
//		RenderSystem.enableBlend();
		boolean needsAir = false;
		if (player.isEyeInFluid(FluidTags.WATER) || i6 < l5) {
			needsAir = true;
		}
		if (k5 == 0) {
			MutableInt moveUp = new MutableInt(needsAir ? -10 : 0 + 10);
//			if (Survive.THIRST_CONFIG.enabled) {
//				GuiHelper.renderThirst((Gui)(Object)this, pPoseStack, moveUp, j1, k1, false);
//				moveUp.add(10);
//			}
//			//Energy
//			if (Survive.STAMINA_CONFIG.enabled) {
//				GuiHelper.renderEnergyBars((Gui)(Object)this, pPoseStack, moveUp, j1, k1, false);
//			}
//			RenderSystem.enableBlend();
//			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		}
	}


	@Inject(method = "extractHearts", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	public void changeHearts(
			GuiGraphicsExtractor graphics,
	        Player player,
	        int xLeft,
	        int yLineBase,
	        int healthRowHeight,
	        int heartOffsetIndex,
	        float maxHealth,
	        int currentHealth,
	        int oldHealth,
	        int absorption,
	        boolean blink, CallbackInfo ci) {
		SurviveHeartType type = SurviveHeartType.forPlayer(player);
        boolean isHardcore = player.level().getLevelData().isHardcore();
        int healthContainerCount = Mth.ceil(maxHealth / 2.0);
        int absorptionContainerCount = Mth.ceil(absorption / 2.0);
        int maxHealthHalvesCount = healthContainerCount * 2;

        for (int containerIndex = healthContainerCount + absorptionContainerCount - 1; containerIndex >= 0; containerIndex--) {
            int row = containerIndex / 10;
            int column = containerIndex % 10;
            int xo = xLeft + column * 8;
            int yo = yLineBase - row * healthRowHeight;
            if (currentHealth + absorption <= 4) {
                yo += this.random.nextInt(2);
            }

            if (containerIndex < healthContainerCount && containerIndex == heartOffsetIndex) {
                yo -= 2;
            }

            this.extractHeart(graphics, SurviveHeartType.CONTAINER, xo, yo, isHardcore, blink, false);
            int halves = containerIndex * 2;
            boolean isAbsorptionHeart = containerIndex >= healthContainerCount;
            if (isAbsorptionHeart) {
                int absorptionHalves = halves - maxHealthHalvesCount;
                if (absorptionHalves < absorption) {
                    boolean halfHeart = absorptionHalves + 1 == absorption;
                    this.extractHeart(graphics, type == SurviveHeartType.WITHERED ? type : SurviveHeartType.ABSORBING, xo, yo, isHardcore, false, halfHeart);
                }
            }

            if (blink && halves < oldHealth) {
                boolean halfHeart = halves + 1 == oldHealth;
                this.extractHeart(graphics, type, xo, yo, isHardcore, true, halfHeart);
            }

            if (halves < currentHealth) {
                boolean halfHeart = halves + 1 == currentHealth;
                this.extractHeart(graphics, type, xo, yo, isHardcore, false, halfHeart);
            }
        }
		ci.cancel();
	}

	protected void extractHeart(GuiGraphicsExtractor pGuiGraphics, SurviveHeartType surviveHeartType, int pX, int pY, boolean pHardcore, boolean pHalfHeart, boolean pBlinking) {
//        RenderSystem.enableBlend();
        pGuiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, surviveHeartType.getSprite(pHardcore, pBlinking, pHalfHeart), pX, pY, 9, 9);
//        RenderSystem.disableBlend();TODO: 26.1
	}

}
