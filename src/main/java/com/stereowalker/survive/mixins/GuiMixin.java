package com.stereowalker.survive.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.client.gui.SurviveHeartType;
import com.stereowalker.survive.core.SurviveEntityStats;
import com.stereowalker.survive.core.TempDisplayMode;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

@Mixin(Gui.class)
public abstract class GuiMixin extends GuiComponent {
	@Shadow protected Minecraft minecraft;
	@Shadow protected final Random random = new Random();
	@Shadow public Player getCameraPlayer() {return null;}
	@Shadow public void renderTextureOverlay(ResourceLocation p_168709_, float p_168710_) {}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	public void render2(PoseStack arg0, float arg1, CallbackInfo ci, Font font, float f) {
		Survive.renderTiredOverlay((Gui)(Object)this);
		Survive.renderHeatStroke((Gui)(Object)this);
	}


	@Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 0))
	public void hotbarColor(float x, float y, float z, float a) {
		Player playerentity = this.getCameraPlayer();
		if (Survive.TEMPERATURE_CONFIG.enabled && Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
			double rawTemperature = SurviveEntityStats.getTemperatureStats(playerentity).getTemperatureLevel();
			double tempLocation = rawTemperature - Survive.DEFAULT_TEMP;
			double displayTemp = 0;
			if (tempLocation > 0) {
				double maxTemp = 0.0D;
				if (playerentity.getAttribute(SAttributes.HEAT_RESISTANCE) != null) {
					maxTemp = playerentity.getAttributeValue(SAttributes.HEAT_RESISTANCE);
				} else {
					maxTemp = SAttributes.HEAT_RESISTANCE.getDefaultValue();
				}
				double div = tempLocation / maxTemp;
				displayTemp = Mth.clamp(div, 0, 1.0D+(28.0D/63.0D));
			}
			if (tempLocation < 0) {
				double maxTemp = 0.0D;
				if (playerentity.getAttribute(SAttributes.COLD_RESISTANCE) != null) {
					maxTemp = playerentity.getAttributeValue(SAttributes.COLD_RESISTANCE);
				} else {
					maxTemp = SAttributes.COLD_RESISTANCE.getDefaultValue();
				}
				double div = tempLocation / maxTemp;
				displayTemp = Mth.clamp(div, -1.0D-(28.0D/63.0D), 0);
			}

			float heatTemp = (float) (1.0F - displayTemp);
			float coldTemp = (float) (1.0F + displayTemp);
			float whiteTemp = (float) ((1.0F - Math.abs(displayTemp))/2 + 0.5F);
			RenderSystem.setShaderColor(coldTemp, whiteTemp, heatTemp, 1.0F);
		} else {
			RenderSystem.setShaderColor(x, y, z, a);
		}
	}

	@Inject(method = "renderHearts", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	public void changeHearts(PoseStack p_168689_, Player p_168690_, int p_168691_, int p_168692_, int p_168693_, int p_168694_, float p_168695_, int p_168696_, int p_168697_, int p_168698_, boolean p_168699_, CallbackInfo ci) {
		SurviveHeartType gui$hearttype = SurviveHeartType.forPlayer(p_168690_);
		int i = 9 * (p_168690_.level.getLevelData().isHardcore() ? 5 : 0);
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
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		ci.cancel();
	}

	protected void renderHeart(PoseStack p_168701_, SurviveHeartType surviveHeartType, int p_168703_, int p_168704_, int p_168705_, boolean p_168706_, boolean p_168707_) {
		if (surviveHeartType.usesVanilla()) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		} else {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, new ResourceLocation(Survive.MOD_ID, "textures/gui/icons.png"));
		}
		this.blit(p_168701_, p_168703_, p_168704_, surviveHeartType.getX(p_168707_, p_168706_), p_168705_, 9, 9);
	}

}
