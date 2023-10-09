package com.stereowalker.survive.mixins.client;

import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.survive.GuiHelper;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.client.gui.SurviveHeartType;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.TempDisplayMode;

import net.minecraft.client.Minecraft;
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
	@Shadow protected Minecraft minecraft;
	@Shadow @Final public RandomSource random;
	@Shadow public Player getCameraPlayer() {return null;}
	@Shadow public void renderTextureOverlay(GuiGraphics p_282304_, ResourceLocation p_168709_, float p_168710_) {}
	@Shadow public int screenWidth;
	@Shadow public int screenHeight;
	@Shadow protected int tickCount;


	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
	public void hotbarColor(float p_283031_, GuiGraphics guiGraphics, CallbackInfo ci) {
		Player playerentity = this.getCameraPlayer();
		if (Survive.TEMPERATURE_CONFIG.enabled && Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
			double displayTemp = SurviveEntityStats.getTemperatureStats(playerentity).getDisplayTemperature();

			float heatTemp = (float) (1.0F - displayTemp);
			float coldTemp = (float) (1.0F + displayTemp);
			float whiteTemp = (float) ((1.0F - Math.abs(displayTemp))/2 + 0.5F);
			guiGraphics.setColor(coldTemp, whiteTemp, heatTemp, 1.0F);
		}
	}


	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
	public void resetHotbarColor(float p_283031_, GuiGraphics guiGraphics, CallbackInfo ci) {
		guiGraphics.setColor(1f, 1f, 1f, 1f);
	}

	@Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void addThirstAndOthers(GuiGraphics pPoseStack, CallbackInfo ci, Player player, int i, boolean flag, long j, int k, FoodData fooddata, int l, int i1, int j1, int k1, float f, int l1, int i2, int j2, int k2, int l2, int i3, int j3, LivingEntity livingentity, int k5, int l5, int i6) {
		RenderSystem.enableBlend();
		boolean needsAir = false;
		if (player.isEyeInFluid(FluidTags.WATER) || i6 < l5) {
			needsAir = true;
		}
		if (k5 == 0) {
			MutableInt moveUp = new MutableInt(needsAir ? -10 : 0 + 10);
			if (Survive.THIRST_CONFIG.enabled) {
				GuiHelper.renderThirst((Gui)(Object)this, pPoseStack, moveUp, j1, k1, false);
				moveUp.add(10);
			}
			//Energy
			if (Survive.STAMINA_CONFIG.enabled) {
				GuiHelper.renderEnergyBars((Gui)(Object)this, pPoseStack, moveUp, j1, k1, false);
			}
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		}
	}


	@Inject(method = "renderHearts", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	public void changeHearts(GuiGraphics p_168689_, Player p_168690_, int p_168691_, int p_168692_, int p_168693_, int p_168694_, float p_168695_, int p_168696_, int p_168697_, int p_168698_, boolean p_168699_, CallbackInfo ci) {
		SurviveHeartType gui$hearttype = SurviveHeartType.forPlayer(p_168690_);
		int i = 9 * (p_168690_.level().getLevelData().isHardcore() ? 5 : 0);
		int j = Mth.ceil((double)p_168695_ / 2.0D);
		int k = Mth.ceil((double)p_168698_ / 2.0D);
		int l = j * 2;

		for(int i1 = j + k - 1; i1 >= 0; --i1) {
			int j1 = i1 / 10;
			int k1 = i1 % 10;
			int l1 = p_168691_ + k1 * 8;
			int i2 = p_168692_ - j1 * p_168693_;
			if (p_168696_ + p_168698_ <= 4) {
				i2 += this.random.nextInt(2);
			}

			if (i1 < j && i1 == p_168694_) {
				i2 -= 2;
			}

			this.renderHeart(p_168689_, SurviveHeartType.CONTAINER, l1, i2, i, p_168699_, false);
			int j2 = i1 * 2;
			boolean flag = i1 >= j;
			if (flag) {
				int k2 = j2 - l;
				if (k2 < p_168698_) {
					boolean flag1 = k2 + 1 == p_168698_;
					this.renderHeart(p_168689_, gui$hearttype == SurviveHeartType.WITHERED ? gui$hearttype : SurviveHeartType.ABSORBING, l1, i2, i, false, flag1);
				}
			}

			if (p_168699_ && j2 < p_168697_) {
				boolean flag2 = j2 + 1 == p_168697_;
				this.renderHeart(p_168689_, gui$hearttype, l1, i2, i, true, flag2);
			}

			if (j2 < p_168696_) {
				boolean flag3 = j2 + 1 == p_168696_;
				this.renderHeart(p_168689_, gui$hearttype, l1, i2, i, false, flag3);
			}
		}
		ci.cancel();
	}

	protected void renderHeart(GuiGraphics p_168701_, SurviveHeartType surviveHeartType, int p_168703_, int p_168704_, int p_168705_, boolean p_168706_, boolean p_168707_) {
		p_168701_.blit(surviveHeartType.usesVanilla() ? new ResourceLocation("textures/gui/icons.png") : new ResourceLocation(Survive.MOD_ID, "textures/gui/icons.png"), p_168703_, p_168704_, surviveHeartType.getX(p_168707_, p_168706_), p_168705_, 9, 9);
	}

}
