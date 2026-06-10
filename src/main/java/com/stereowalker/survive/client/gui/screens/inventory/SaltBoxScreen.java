package com.stereowalker.survive.client.gui.screens.inventory;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.inventory.SaltBoxMenu;
import com.stereowalker.unionlib.api.gui.GuiRenderer;
import com.stereowalker.unionlib.api.gui.SeamlessContainerScreen;
import com.stereowalker.unionlib.util.VersionHelper;
import com.stereowalker.unionlib.util.math.ImmutableColor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class SaltBoxScreen extends AbstractContainerScreen<SaltBoxMenu> implements MenuAccess<SaltBoxMenu>, SeamlessContainerScreen {
	private static final ResourceLocation CONTAINER_BACKGROUND = VersionHelper.toLoc(Survive.MOD_ID, "textures/gui/container/salt_box.png");
	public final int containerRows;

	public SaltBoxScreen(SaltBoxMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		int i = 222;
		int j = 114;
		this.containerRows = pMenu.getRowCount();
		this.setImageHeight(175);
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	public void drawContents(GuiRenderer renderer, int mouseX, int mouseY, Runnable originalContents) {
		SeamlessContainerScreen.super.drawContents(renderer, mouseX, mouseY, originalContents);
		this.renderTooltip(renderer.guiGraphics(), mouseX, mouseY);
		renderBars(renderer);
	}

	@Override
	public void drawBackground(GuiRenderer renderer, int mouseX, int mouseY, Runnable originalBackground) {
		SeamlessContainerScreen.super.drawBackground(renderer, mouseX, mouseY, originalBackground);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		renderer.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
	}
	///
	public void renderBars(GuiRenderer renderer) {
		int w = (this.width - this.imageWidth) / 2;
		int h = (this.height - this.imageHeight) / 2;
		for (int j = 0; j < this.containerRows; j++) {
			for (int k = 0; k < 9; k++) {
				int x = (w + 8 + k * 18) + 1;
				int y = (h + 18 + j * (18 + 3)) + 18;
				renderer.fillOverlay(x, y, x + Mth.ceil((this.menu.data().get(k + j * 9) / 1000f) * 14f), y + 1, new ImmutableColor(.86f,.55f,.53f,1).toIntARGB());
			}
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
	}
}
