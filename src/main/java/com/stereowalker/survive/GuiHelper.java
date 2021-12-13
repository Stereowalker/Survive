package com.stereowalker.survive;

import com.stereowalker.survive.needs.IRoastedEntity;
import com.stereowalker.survive.world.effect.SEffects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

@OnlyIn(Dist.CLIENT)
public class GuiHelper {
	@OnlyIn(Dist.CLIENT)
	public static void registerOverlays() {
		IIngameOverlay TIRED_ELEMENT;
		IIngameOverlay HEAT_STROKE_ELEMENT;
		TIRED_ELEMENT = OverlayRegistry.registerOverlayTop("Tired", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
	        gui.setupOverlayRenderState(true, false);
	        GuiHelper.renderTiredOverlay(gui);
	    });
		HEAT_STROKE_ELEMENT = OverlayRegistry.registerOverlayTop("Heat Stroke", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
	        gui.setupOverlayRenderState(true, false);
	        GuiHelper.renderHeatStroke(gui);
	    });
	}
	
	@SuppressWarnings("resource")
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
