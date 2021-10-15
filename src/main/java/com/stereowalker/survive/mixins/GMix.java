package com.stereowalker.survive.mixins;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.platform.GlDebug;

@Mixin(GlDebug.class)
public class GMix {
	@Redirect(method = "printDebugLog", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"))
	private static void noDebug(Logger logger, String message, Object p0) {
		
	}
}
