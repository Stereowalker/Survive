package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.entity.TempDisplayMode;
import com.stereowalker.survive.entity.ai.SAttributes;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

@Mixin(IngameGui.class)
public abstract class IngameGuiMixin extends AbstractGui{
	@Shadow
	public PlayerEntity getRenderViewPlayer() {return null;}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;color4f(FFFF)V", ordinal = 0), method = {"renderHotbar"})
	public void hotbarColor(float x, float y, float z, float a) {
		PlayerEntity playerentity = this.getRenderViewPlayer();
		if (Config.enable_temperature && Config.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
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
				displayTemp = MathHelper.clamp(div, 0, 1.0D+(28.0D/63.0D));
			}
			if (tempLocation < 0) {
				double maxTemp = 0.0D;
				if (playerentity.getAttribute(SAttributes.COLD_RESISTANCE) != null) {
					maxTemp = playerentity.getAttributeValue(SAttributes.COLD_RESISTANCE);
				} else {
					maxTemp = SAttributes.COLD_RESISTANCE.getDefaultValue();
				}
				double div = tempLocation / maxTemp;
				displayTemp = MathHelper.clamp(div, -1.0D-(28.0D/63.0D), 0);
			}

			float heatTemp = (float) (1.0F - displayTemp);
			float coldTemp = (float) (1.0F + displayTemp);
			float whiteTemp = (float) ((1.0F - Math.abs(displayTemp))/2 + 0.5F);
			RenderSystem.color4f(coldTemp, whiteTemp, heatTemp, 1.0F);
		} else {
			RenderSystem.color4f(x, y, z, a);
		}
	}
}
