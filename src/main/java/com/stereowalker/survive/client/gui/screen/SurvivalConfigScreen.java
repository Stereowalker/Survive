package com.stereowalker.survive.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.TempDisplayMode;
import com.stereowalker.unionlib.config.ConfigBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

@OnlyIn(Dist.CLIENT)
public class SurvivalConfigScreen extends Screen {
	private final Screen previousScreen;

	public SurvivalConfigScreen(Minecraft mc, Screen previousScreen) {
		super(new TranslationTextComponent(Survive.MOD_ID+".config.title"));
		this.previousScreen = previousScreen;
		this.minecraft = mc;
	}

	@Override
	public void init() {
		int xMod = -155;
		int yMod = 0;
		//TODO Fix Configs Screens
//		this.addBooleanOption(OldConfig.enable_temperature, xMod, yMod);
//		this.addBooleanOption(OldConfig.enable_thirst, xMod, yMod+=24);
//		this.addBooleanOption(OldConfig.enable_sleep, xMod, yMod+=24);
//		this.addBooleanOption(OldConfig.enable_stamina, xMod, yMod+=24);
//		this.addEnumOption("tempDisplayMode", Config.tempDisplayMode, TempDisplayMode.values(), xMod, yMod+=24);
////		this.addBooleanOption(Config.debug_mode, xMod=5, yMod=0);
////		this.addBooleanOption(Config.enable_enchantment_descriptions, xMod, yMod+=24);
////		this.addBooleanOption(Config.enable_leveling_system, xMod, yMod+=24);
////		this.addSliderOption(Config.scroll_chance, xMod, yMod+=24, 100);
////		this.addSliderOption(Config.abomination_chance, xMod, yMod+=24, 10000);
//		this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, new TranslationTextComponent("gui.done"), (onPress) -> {
//			this.minecraft.displayGuiScreen(this.previousScreen);
//		}));
	}
	
	public <T extends Enum<T>> void addEnumOption(String trans, T config, T[] values, int xMod, int yMod) {
		this.addButton(new Button(this.width / 2 + xMod, this.height / 6 + yMod, 150, 20, new TranslationTextComponent(Survive.MOD_ID+"."+trans).appendString(" : "+config), (p_212984_1_) -> {
			rotateEnum(config, values, true);
			this.minecraft.displayGuiScreen(this);
		}));
	}
	
	public static <T extends Enum<T>> void rotateEnum(T input, T[] values, boolean forward) {
		if (forward) {
			System.out.println(input);
			input = rotateEnumForward(input, values);
			System.out.println(rotateEnumForward(input, values)+" "+input);
		} else {
			input = rotateEnumBackward(input, values);
		}
	}
	
	public static <T extends Enum<T>> T rotateEnumForward(T input, T[] values) {
		if (input.ordinal() == values.length - 1) {
			return values[0];
		}
		else {
			return values[input.ordinal() + 1];
		}
	}
	
	public static <T extends Enum<T>> T rotateEnumBackward(T input, T[] values) {
		if (input.ordinal() == values.length - 1) {
			return values[0];
		}
		else {
			return values[input.ordinal() + 1];
		}
	}
	
	public void addBooleanOption(BooleanValue config, int xMod, int yMod) {
		String toggle;
		if (config.get()) toggle = "options.on";
		else toggle = "options.off";
		this.addButton(new Button(this.width / 2 + xMod, this.height / 6 + yMod, 150, 20, new TranslationTextComponent(Survive.MOD_ID+"."+config.getPath().get(1)).appendString(" : ").append(new TranslationTextComponent(toggle)), (onPress) -> {
			config.set(!config.get());
			this.minecraft.displayGuiScreen(this);
		}));
	}
	
	public void addSliderOption(IntValue config, int xMod, int yMod, float maxValue) {
		this.addButton(new Slider(this, this.width / 2 + xMod, this.height / 6 + yMod, 150, (float)(config.get()/maxValue)) {
			@Override
			protected void /*updateMessage*/func_230979_b_() {
				String s = (int)((float)this.sliderValue * maxValue) + "";
				this.setMessage(new TranslationTextComponent(Survive.MOD_ID+"."+config.getPath().get(1)).appendString(": " + s));
			}

			@Override
			protected void /*applyValue*/func_230972_a_() {
				config.set((int)((float)this.sliderValue * maxValue));
			}
		});
	}

	@Override
	public void onClose() {
		ConfigBuilder.write(Config.class);
//		this.minecraft.gameSettings.saveOptions();
	}

	@Override
	public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground(matrixStack);
		AbstractGui.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 16777215);
		super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
	}

	@OnlyIn(Dist.CLIENT)
	public abstract class Slider extends AbstractSlider {
		SurvivalConfigScreen screen;

		public Slider(SurvivalConfigScreen screen, int x, int y, int width, double value) {
			super(x, y, width, 20, new TranslationTextComponent(""), value);
			this./*updateMessage*/func_230979_b_();
			this.screen = screen;
		}
	}
}