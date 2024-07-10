package com.stereowalker.survive.mixins.client;

import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.client.gui.SurviveHeartType;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.TempDisplayMode;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
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


	@Inject(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
	public void hotbarColor(GuiGraphics guiGraphics, float p_283031_, CallbackInfo ci) {
		Player playerentity = this.getCameraPlayer();
		if (Survive.TEMPERATURE_CONFIG.enabled && Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
			double displayTemp = SurviveEntityStats.getTemperatureStats(playerentity).getDisplayTemperature();

			float heatTemp = (float) (1.0F - displayTemp);
			float coldTemp = (float) (1.0F + displayTemp);
			float whiteTemp = (float) ((1.0F - Math.abs(displayTemp))/2 + 0.5F);
			guiGraphics.setColor(coldTemp, whiteTemp, heatTemp, 1.0F);
		}
	}


	@Inject(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
	public void resetHotbarColor(GuiGraphics guiGraphics, float p_283031_, CallbackInfo ci) {
		guiGraphics.setColor(1f, 1f, 1f, 1f);
	}

//	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void addThirstAndOthers(GuiGraphics pPoseStack, CallbackInfo ci, Player player, int i, boolean flag, long j, int k, FoodData fooddata, int l, int i1, int j1, int k1, float f, int l1, int i2, int j2, int k2, int l2, int i3, int j3, LivingEntity livingentity, int k5, int l5, int i6) {
		RenderSystem.enableBlend();
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
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		}
	}


	@Inject(method = "renderHearts", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	public void changeHearts(GuiGraphics pGuiGraphics,
	        Player pPlayer,
	        int pX,
	        int pY,
	        int pHeight,
	        int pOffsetHeartIndex,
	        float pMaxHealth,
	        int pCurrentHealth,
	        int pDisplayHealth,
	        int pAbsorptionAmount,
	        boolean pRenderHighlight, CallbackInfo ci) {
		SurviveHeartType gui$hearttype = SurviveHeartType.forPlayer(pPlayer);
		boolean flag = pPlayer.level().getLevelData().isHardcore();
        int i = Mth.ceil((double)pMaxHealth / 2.0);
        int j = Mth.ceil((double)pAbsorptionAmount / 2.0);
        int k = i * 2;

        for (int l = i + j - 1; l >= 0; l--) {
            int i1 = l / 10;
            int j1 = l % 10;
            int k1 = pX + j1 * 8;
            int l1 = pY - i1 * pHeight;
            if (pCurrentHealth + pAbsorptionAmount <= 4) {
                l1 += this.random.nextInt(2);
            }

            if (l < i && l == pOffsetHeartIndex) {
                l1 -= 2;
            }

            this.renderHeart(pGuiGraphics, SurviveHeartType.CONTAINER, k1, l1, flag, pRenderHighlight, false);
            int i2 = l * 2;
            boolean flag1 = l >= i;
            if (flag1) {
                int j2 = i2 - k;
                if (j2 < pAbsorptionAmount) {
                    boolean flag2 = j2 + 1 == pAbsorptionAmount;
                    this.renderHeart(pGuiGraphics, gui$hearttype == SurviveHeartType.WITHERED ? gui$hearttype : SurviveHeartType.ABSORBING, k1, l1, flag, false, flag2);
                }
            }

            if (pRenderHighlight && i2 < pDisplayHealth) {
                boolean flag3 = i2 + 1 == pDisplayHealth;
                this.renderHeart(pGuiGraphics, gui$hearttype, k1, l1, flag, true, flag3);
            }

            if (i2 < pCurrentHealth) {
                boolean flag4 = i2 + 1 == pCurrentHealth;
                this.renderHeart(pGuiGraphics, gui$hearttype, k1, l1, flag, false, flag4);
            }
        }
		ci.cancel();
	}

	protected void renderHeart(GuiGraphics pGuiGraphics, SurviveHeartType surviveHeartType, int pX, int pY, boolean pHardcore, boolean pHalfHeart, boolean pBlinking) {
        RenderSystem.enableBlend();
        pGuiGraphics.blitSprite(surviveHeartType.getSprite(pHardcore, pBlinking, pHalfHeart), pX, pY, 9, 9);
        RenderSystem.disableBlend();
	}

}
