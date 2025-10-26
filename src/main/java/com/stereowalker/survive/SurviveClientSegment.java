package com.stereowalker.survive;

import java.util.Random;

import org.apache.commons.lang3.mutable.MutableInt;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.survive.client.events.TooltipEvents;
import com.stereowalker.survive.client.gui.screens.inventory.SaltBoxScreen;
import com.stereowalker.survive.client.particle.HygieneParticle;
import com.stereowalker.survive.core.TempDisplayMode;
import com.stereowalker.survive.core.particles.SParticleTypes;
import com.stereowalker.survive.hooks.ColdMenu;
import com.stereowalker.survive.needs.IRealisticEntity;
import com.stereowalker.survive.needs.IRoastedEntity;
import com.stereowalker.survive.world.effect.SMobEffects;
import com.stereowalker.survive.world.entity.ai.attributes.SAttributes;
import com.stereowalker.survive.world.inventory.SMenuType;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.TemperatureRegulatorPlateItem;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.survive.world.level.block.DryingCauldronBlock;
import com.stereowalker.survive.world.level.block.DryingCauldronBlock.FluidToDry;
import com.stereowalker.survive.world.level.block.PlatedTemperatureRegulatorBlock;
import com.stereowalker.survive.world.level.block.SBlocks;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.unionlib.api.collectors.ColorOverrideCollector;
import com.stereowalker.unionlib.api.collectors.InsertCollector;
import com.stereowalker.unionlib.api.collectors.MenuCollector;
import com.stereowalker.unionlib.api.collectors.OverlayCollector;
import com.stereowalker.unionlib.api.collectors.OverlayCollector.Order;
import com.stereowalker.unionlib.api.collectors.ParticleCollector;
import com.stereowalker.unionlib.api.collectors.RenderLayerCollector;
import com.stereowalker.unionlib.api.gui.GuiRenderer;
import com.stereowalker.unionlib.client.gui.screens.config.MinecraftModConfigsScreen;
import com.stereowalker.unionlib.insert.ClientInserts;
import com.stereowalker.unionlib.mod.ClientSegment;
import com.stereowalker.unionlib.util.LoaderHelper;
import com.stereowalker.unionlib.util.ScreenHelper;
import com.stereowalker.unionlib.util.ScreenHelper.ScreenOffset;
import com.stereowalker.unionlib.util.VersionHelper;
import com.stereowalker.unionlib.util.VersionHelper.VanillaComponents;
import com.stereowalker.unionlib.util.math.Color;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;

public class SurviveClientSegment extends ClientSegment {

	public static final ResourceLocation GUI_ICONS = VersionHelper.toLoc(Survive.MOD_ID, "textures/gui/icons.png");
	public static float maxContainerCoolness;
	@Override
	public ResourceLocation getModIcon() {
		return VersionHelper.toLoc(Survive.MOD_ID, "textures/icon.png");
	}

	@Override
	public Screen getConfigScreen(Minecraft mc, Screen previousScreen) {
		return new MinecraftModConfigsScreen(previousScreen, Component.translatable("gui.survive.config.title"), Survive.TEMPERATURE_CONFIG, Survive.HYGIENE_CONFIG, Survive.STAMINA_CONFIG, Survive.THIRST_CONFIG, Survive.WELLBEING_CONFIG, Survive.FOOD_CONFIG, Survive.CONFIG);
	}
	
	@Override
	public void setupParticles(ParticleCollector collector) {
		collector.addFactory(SParticleTypes.STINK, HygieneParticle.StinkFactory::new);
		collector.addFactory(SParticleTypes.CLEAN, HygieneParticle.CleanFactory::new);
	}
	
	@Override
	public void setupRenderLayers(RenderLayerCollector collector) {
		RenderType frendertype = RenderType.translucent();
		collector.setFluidRenderLayer(frendertype, SFluids.PURIFIED_WATER, SFluids.FLOWING_PURIFIED_WATER);
        RenderType cutout = RenderType.cutout();
		collector.setBlockRenderLayer(cutout, SBlocks.REALISTIC_CAMPFIRE);
	}
	
	@Override
	public void setupColorOverrides(ColorOverrideCollector collector) {
		collector.overrideBlocks((state, displayReader, blockPos, tintIndex) -> {
			return Survive.PURIFIED_WATER_COLOR;
		}, SBlocks.PURIFIED_WATER, SBlocks.PURIFIED_WATER_CAULDRON);
		collector.overrideBlocks((state, displayReader, blockPos, tintIndex) -> {
			if (state.getValue(DryingCauldronBlock.FLUID) == FluidToDry.POTASH) {
				return Color.parse("0x483c35").brighter(state.getValue(DryingCauldronBlock.BOILING) * 0.12f).toIntRGB();
			}
			else if (state.getValue(DryingCauldronBlock.FLUID) == FluidToDry.SEA_SALT) {
				return Color.fromIntRGB(BiomeColors.getAverageWaterColor(displayReader, blockPos))
						.brighter(state.getValue(DryingCauldronBlock.BOILING) * 0.12f).toIntRGB();
			}
			else {
				return new Color(1f, 0, 0).brighter(state.getValue(DryingCauldronBlock.BOILING) * 0.12f).toIntRGB();
			}
//			if (displayReader.getBlockEntity(blockPos) instanceof DryingCauldronBlockEntity dbe) {
//			}
		}, SBlocks.DRYING_CAULDRON);
		collector.overrideBlocks((state, displayReader, blockPos, tintIndex) -> {
			return PlatedTemperatureRegulatorBlock.getColor(state).toIntRGB();
		}, SBlocks.PLATED_TEMPERATURE_REGULATOR);
		collector.overrideItems((stack, tintIndex) -> {
			return tintIndex > 0 ? -1 : PotionUtils.getPotion(stack) == SPotions.PURIFIED_WATER.holder().value() ? Survive.PURIFIED_WATER_COLOR : PotionUtils.getColor(stack);
	      }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
		collector.overrideItems((stack, tintIndex) -> {
			return TemperatureRegulatorPlateItem.getColor(stack).toIntARGB();
		}, SItems.LARGE_HEATING_PLATE, SItems.LARGE_COOLING_PLATE, SItems.MEDIUM_HEATING_PLATE, SItems.MEDIUM_COOLING_PLATE, SItems.SMALL_HEATING_PLATE, SItems.SMALL_COOLING_PLATE);
	}
	
	@Override
	public void registerInserts(InsertCollector collector) {
		collector.addInsert(ClientInserts.SCREEN_RENDER_FINISH, insert -> {
			if (insert.screen() instanceof AbstractContainerScreen cont && cont.getMenu() instanceof ColdMenu cold) {
				float progress = (float)cold.coldness() / (float)cold.maxColdness();
				if (progress > 0) {
					int i = ((cont.width - cont.imageWidth) / 2) + 9;
					int j = ((cont.height - cont.imageHeight) / 2);
					if (cont instanceof ContainerScreen c) j += c.containerRows * 18;
					j += 96;
					insert.guiRenderer().blit(VersionHelper.toLoc("survive:textures/gui/coldness.png"), i, j + 17, 0, 0, 158, 22);
					
					int i1 = 142;
					int j1 = Mth.ceil(progress * 142.0F);
					insert.guiRenderer().blit(VersionHelper.toLoc("survive:textures/gui/coldness.png"), i + 8, j + 17 + 4, 0, 30, j1, 10);
				}
				
			}
		});
		collector.addInsert(ClientInserts.ITEM_TOOLTIP, insert ->{
			if (insert.player() != null) {
				boolean showWeight = false;
				boolean showTemp = false;
				if ((Survive.STAMINA_CONFIG.enabled && Survive.STAMINA_CONFIG.enable_weights) || Survive.TEMPERATURE_CONFIG.enabled) {
					for(EquipmentSlot type : EquipmentSlot.values()) {
						if (LoaderHelper.canEquip(insert.player(), insert.itemStack(), type) && type.getType() == Type.ARMOR) {
							showWeight = Survive.STAMINA_CONFIG.enabled && Survive.STAMINA_CONFIG.enable_weights;
							showTemp = Survive.TEMPERATURE_CONFIG.enabled;
							break;
						}
					}
				}

				if (showWeight || showTemp) {
					TooltipEvents.accessoryTooltip(insert.player(), insert.itemStack(), insert.tooltips(), showWeight, showTemp);
				}
				FoodUtils.applyFoodStatusToTooltip(insert.player(), insert.itemStack(), insert.tooltips());
				
				if (SDataComponents.BIOME_SOURCE_D.hasData(insert.itemStack()) && insert.player().level().registryAccess()
				.lookup(Registries.BIOME)
				.get().get(ResourceKey.create(Registries.BIOME, SDataComponents.BIOME_SOURCE_D.getData(insert.itemStack())))
				.get().is(BiomeTags.IS_OCEAN)) {
					insert.tooltips().add(Component.translatable("Sea Water"));
				}
			}
		});
	}

	@Override
	public void setupGuiOverlays(OverlayCollector collector) {
		//Should Render after food ideally
		MutableInt rightHeight = new MutableInt(49);
		collector.register("reset", Order.END, (gui,renderer,width,height)->{
			rightHeight.setValue(49);
		});
		collector.register("thirst_level", Order.END, (gui,renderer,width,height)->{
			boolean isMounted = gui.minecraft.player.getVehicle() instanceof LivingEntity;
			if (Survive.THIRST_CONFIG.enabled && !isMounted && !gui.minecraft.options.hideGui && gui.minecraft.gameMode.canHurtPlayer())
			{
//				gui.setupOverlayRenderState(true, false);
				int left = width / 2 + 91;
				int top = height - rightHeight.intValue();
				renderThirst(gui, renderer, left, top, true);
				rightHeight.add(10);
			}
		});
		collector.register("stamina_level", Order.END, (gui,renderer,width,height)->{
			boolean isMounted = gui.minecraft.player.getVehicle() instanceof LivingEntity;
			if (Survive.STAMINA_CONFIG.enabled && !isMounted && !gui.minecraft.options.hideGui && gui.minecraft.gameMode.canHurtPlayer())
			{
//				gui.setupOverlayRenderState(true, false);
				int left = width / 2 + 91;
				int top = height - rightHeight.intValue();
				renderEnergyBars(gui, renderer, rightHeight, left, top, true);
//				gui.rightHeight += moveUp.getValue();
			}
		});
		collector.register("tired", Order.END, (gui,renderer,width,height)->{
			if (Survive.CONFIG.tired_overlay && gui.minecraft.player.hasEffect(SMobEffects.TIREDNESS.holder().value())) {
//				gui.setupOverlayRenderState(true, false);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.disableDepthTest();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				renderTiredOverlay(gui, renderer);
			}
		});
		collector.register("heat_stroke", Order.END, (gui,renderer,width,height)->{
//			gui.setupOverlayRenderState(true, false);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.disableDepthTest();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			renderHeatStroke(gui, renderer);
		});
		collector.register("temperature", Order.END, (gui,renderer,width,height)->{
			if (!gui.minecraft.options.hideGui && Survive.TEMPERATURE_CONFIG.enabled && !Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HOTBAR)) {
				//				gui.setupOverlayRenderState(true, false);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.disableDepthTest();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				renderTemperature(gui, ScreenOffset.TOP, gui.getCameraPlayer(), renderer, true);
			}
		});
		collector.register("nutrition", Order.END, (gui,renderer,width,height)->{
			if (gui.getCameraPlayer() instanceof IRealisticEntity real && Survive.CONFIG.nutrition_enabled && (VanillaComponents.FOOD.hasData(gui.getCameraPlayer().getMainHandItem()) || VanillaComponents.FOOD.hasData(gui.getCameraPlayer().getOffhandItem()) || Survive.CONFIG.always_render_nut)) {
				ScreenOffset position = Survive.CONFIG.nut_offset;
				int x = ScreenHelper.getXOffset(position, gui.minecraft) + Survive.CONFIG.nut_xLoc;
				int y = ScreenHelper.getYOffset(position, gui.minecraft) + Survive.CONFIG.nut_yLoc;
				if (Survive.CONFIG.show_raw_nut_vals) {
					renderer.drawString("Carbs = "+real.nutritionData().carbs().level(), x, y, ChatFormatting.GRAY.getColor(), false);
					renderer.drawString("Protein = "+real.nutritionData().protein().level(), x, y + 10, ChatFormatting.GRAY.getColor(), false);
					renderer.drawString("Fats = "+real.nutritionData().fat().level(), x, y + 20, ChatFormatting.GRAY.getColor(), false);
				}
				else {
					//Carbs
					renderer.blit(GUI_ICONS, x + 7, y + 02, 194, 22, 62, 5);
					renderer.blit(GUI_ICONS, x + 8, y + 03, 195, 28, Mth.floor(real.nutritionData().carbs().level() / 50f), 3);
					renderer.blit(GUI_ICONS, x + 0, y + 00, 246, 11, 9, 9);
					//Protein
					renderer.blit(GUI_ICONS, x + 7, y + 12, 194, 32, 62, 5);
					renderer.blit(GUI_ICONS, x + 8, y + 13, 195, 38, Mth.floor(real.nutritionData().protein().level() / 50f), 3);
					renderer.blit(GUI_ICONS, x + 0, y + 10, 226, 1, 9, 9);
					//Fats
					renderer.blit(GUI_ICONS, x + 7, y + 22, 194, 42, 62, 5);
					renderer.blit(GUI_ICONS, x + 8, y + 23, 195, 48, Mth.floor(real.nutritionData().fat().level() / 50f), 3);
					renderer.blit(GUI_ICONS, x + 0, y + 20, 236, 11, 9, 9);
				}
			}
		});
	}

	@SuppressWarnings("resource")
	public static void renderTemperature(Gui gui, ScreenOffset position, Player playerentity, GuiRenderer renderer, boolean forgeOverlay) {
		int x = ScreenHelper.getXOffset(position, gui.minecraft) + Survive.TEMPERATURE_CONFIG.tempXLoc;
		int y = ScreenHelper.getYOffset(position, gui.minecraft) + Survive.TEMPERATURE_CONFIG.tempYLoc;
		Minecraft.getInstance().getProfiler().push("temperature");
		//		if (!forgeOverlay) {
		//			RenderSystem.setShader(GameRenderer::getPositionTexShader);
		//			RenderSystem.setShaderTexture(0, GUI_ICONS);
		//		}
		double displayTemp = ((IRealisticEntity)playerentity).temperatureData().getDisplayTemperature();
		//For Numbers
		String s = ((IRealisticEntity)playerentity).temperatureData().getCelcius()+" °C";
		if (Minecraft.getInstance().gameMode.hasExperience()) {
			if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.HORIZONTAL_BAR)) {
				if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp >= 1) {//Hyperthermia override
					renderer.blit(GUI_ICONS, x-3, y-3, 0, 79, 138, 11);
				} else if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp <= -1) {//Hypothermia override
					renderer.blit(GUI_ICONS, x-3, y-3, 0, 90, 138, 11);
				} else {
					renderer.blit(GUI_ICONS, x, y, 3, 64, 132, 5);
					renderer.blit(GUI_ICONS, x, y, 3, 69, 132, 5);
				}
				renderer.blit(GUI_ICONS, x+Mth.floor(displayTemp*44)+63+(displayTemp>0?1:0), y, 1, 74, 4, 5);
			}
			else if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.VERTICAL_BAR)) {
				if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp >= 1) {//Hyperthermia override
					renderer.blit(GUI_ICONS, x-3, y-3, 11, 101, 11, 138);
				} else if (Survive.TEMPERATURE_CONFIG.tempEffects && displayTemp <= -1) {//Hypothermia override
					renderer.blit(GUI_ICONS, x-3, y-3, 00, 101, 11, 138);
				} else {
					renderer.blit(GUI_ICONS, x, y, 32, 104, 5, 132);
					renderer.blit(GUI_ICONS, x, y, 27, 104, 5, 132);
				}
				renderer.blit(GUI_ICONS, x, y-Mth.floor(displayTemp*44)+63-(displayTemp>0?1:0), 22, 104, 5, 5);
			}
			else if (Survive.TEMPERATURE_CONFIG.tempDisplayMode.equals(TempDisplayMode.NUMBERS)) {
				if (Survive.TEMPERATURE_CONFIG.displayTempInFahrenheit) {
					s = ((IRealisticEntity)playerentity).temperatureData().getFahrenheit()+" °F";
				}
				if (displayTemp >= 1) {
					renderer.drawString(s, x, y, ChatFormatting.GOLD.getColor(), false);
				} else if (displayTemp <= -1) {
					renderer.drawString(s, x, y, ChatFormatting.BLUE.getColor(), false);
				} else {
					renderer.drawString(s, x, y, ChatFormatting.GRAY.getColor(), false);
				}
			}
		}
		Minecraft.getInstance().getProfiler().pop();
		//		if (!forgeOverlay) {
		//			RenderSystem.setShader(GameRenderer::getPositionTexShader);
		//			RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		//			RenderSystem.enableBlend();
		//			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		//		}
	}


	public static void renderHeatStroke(Gui gui, GuiRenderer renderer)
	{
		if (((IRoastedEntity)gui.minecraft.player).getTicksRoasted() > 0) {
			gui.renderTextureOverlay(renderer.guiGraphics(), Survive.getInstance().location("textures/misc/burning_overlay.png"), ((IRoastedEntity)gui.minecraft.player).getPercentRoasted());
		}
	}

	@SuppressWarnings("resource")
	public static void renderTiredOverlay(Gui gui, GuiRenderer graphics) {
		Minecraft.getInstance().getProfiler().push("tired");
		int amplifier = Minecraft.getInstance().player.getEffect(SMobEffects.TIREDNESS.holder().value()).getAmplifier() + 1;
		amplifier/=(Survive.CONFIG.tiredTimeStacks/5);
		amplifier = Mth.clamp(amplifier, 0, 4);
		gui.renderTextureOverlay(graphics.guiGraphics(), Survive.getInstance().location("textures/misc/sleep_overlay_"+(amplifier)+".png"), 0.5F);
		Minecraft.getInstance().getProfiler().pop();
	}

	public static void renderThirst(Gui gui, GuiRenderer graphics, int j1, int k1, boolean forgeOverlay) {
		Player player = (Player)gui.minecraft.getCameraEntity();
		IRealisticEntity realisticPlayer = (IRealisticEntity)player;
		int waterL = (int) realisticPlayer.waterData().getWaterLevel();
		gui.minecraft.getProfiler().push("thirst");
		for(int k6 = 0; k6 < 10; ++k6) {
			int i7 = k1;
			int k7 = 16;
			int i8 = 0;
			if (player.hasEffect(SMobEffects.THIRST.holder().value())) {
				k7 += 36;
				i8 = 13;
			}

			if (realisticPlayer.waterData().getHydrationLevel() <= 0.0F && gui.tickCount % (waterL * 3 + 1) == 0) {
				i7 = k1 + (gui.random.nextInt(3) - 1);
			}

			int k8 = j1 - k6 * 8 - 9;
			graphics.blit(GUI_ICONS, k8, i7, 16 + i8 * 9, 54, 9, 9);
			if (k6 * 2 + 1 < waterL) {
				graphics.blit(GUI_ICONS, k8, i7, k7 + 36, 54, 9, 9);
			}

			if (k6 * 2 + 1 == waterL) {
				graphics.blit(GUI_ICONS, k8, i7, k7 + 45, 54, 9, 9);
			}
		}
		gui.minecraft.getProfiler().pop();
	}

	public static void renderEnergyBars(Gui gui, GuiRenderer graphics, MutableInt moveUp, int j1, int k1, boolean forgeOverlay) {
		Random rand = new Random();
		Player player = (Player)gui.minecraft.getCameraEntity();
		IRealisticEntity real = (IRealisticEntity)player;
		float maxStamina = (float) player.getAttributeValue(SAttributes.MAX_STAMINA.holder());
		int l = (int) real.staminaData().getLTS();
		if (real.staminaData().isDeadTired()) l = (int) real.staminaData().getReserveLevel();
		Minecraft.getInstance().getProfiler().push("energy");
		if (!forgeOverlay) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, GUI_ICONS);
		}
		
		int[] i9 = new int[30];
		for (int i = 0; i < 30; i++) i9[i] = k1 + (rand.nextInt(3) - 1);
		

		for (int i = 0; i < Mth.ceil((float)maxStamina/20.0F); i++) {
			for(int k6 = 0; k6 < 10; ++k6) {
				int i7 = k1;
				int k7 = 16;
				int i8 = 0;
				if (real.staminaData().isDeadTired()) {
					k7 += 36;
					i8 = 13;
				}
				
				if (real.staminaData().getLTS() <= 10.0F && gui.getGuiTicks() % (l * 3 + 1) == 0) {
					i7 = i9[i];
				}
				
				int k8 = j1 - k6 * 8 - 9;
				if ((k6 * 2 + 1) + (20*i) < Mth.floor(maxStamina)+1) {
					graphics.blit(GUI_ICONS, k8, i7, 16 + i8 * 9, 36, 9, 9);
				}
				if ((k6 * 2 + 1) + (20*i) < l) {
					graphics.blit(GUI_ICONS, k8, i7, k7 + 36, 36, 9, 9);
				}
				
				if ((k6 * 2 + 1) + (20*i)  == l) {
					graphics.blit(GUI_ICONS, k8, i7, k7 + 45, 36, 9, 9);
				}
			}
			moveUp.add(10);
		}
		if (real.staminaData().isExerting() || real.staminaData().isShortOfBreath()) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
			l = real.staminaData().isShortOfBreath() ? real.staminaData().getSTSRecovery() : real.staminaData().getBurstStamina();
			for (int i = 0; i < Mth.ceil((float)maxStamina/20.0F); i++) {
				for(int k6 = 0; k6 < 10; ++k6) {
					int i7 = k1;
					int k7 = 16;
					if (real.staminaData().isShortOfBreath()) {
						k7 += 36;
					}
					
					if (real.staminaData().getLTS() <= 10.0F && gui.getGuiTicks() % (l * 3 + 1) == 0) {
						i7 = i9[i];
					}
					
					int k8 = j1 - k6 * 8 - 9;
					if ((k6 * 2 + 1) + (20*i) < l) {
						graphics.blit(GUI_ICONS, k8, i7, k7 + 36, 27, 9, 9);
					}
					
					if ((k6 * 2 + 1) + (20*i)  == l) {
						graphics.blit(GUI_ICONS, k8, i7, k7 + 45, 27, 9, 9);
					}
				}
			}
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
		Minecraft.getInstance().getProfiler().pop();
	}
	
	@Override
	public void setupMenus(MenuCollector collector) {
		collector.addMenu(SMenuType.SALT_BOX, SaltBoxScreen::new);
	}

}
