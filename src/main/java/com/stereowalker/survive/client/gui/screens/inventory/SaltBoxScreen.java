package com.stereowalker.survive.client.gui.screens.inventory;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.inventory.SaltBoxMenu;
import com.stereowalker.unionlib.util.VersionHelper;
import com.stereowalker.unionlib.util.math.Color;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class SaltBoxScreen extends AbstractContainerScreen<SaltBoxMenu> implements MenuAccess<SaltBoxMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = VersionHelper.toLoc(Survive.MOD_ID, "textures/gui/container/salt_box.png");
    public final int containerRows;

    public SaltBoxScreen(SaltBoxMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        int i = 222;
        int j = 114;
        this.containerRows = pMenu.getRowCount();
        this.imageHeight = 175;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        renderBars(pGuiGraphics);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
    ///
    public void renderBars(GuiGraphics pGuiGraphics) {
    	int w = (this.width - this.imageWidth) / 2;
        int h = (this.height - this.imageHeight) / 2;
    	for (int j = 0; j < this.containerRows; j++) {
            for (int k = 0; k < 9; k++) {
            	int x = (w + 8 + k * 18) + 1;
            	int y = (h + 18 + j * (18 + 3)) + 18;
            	pGuiGraphics.fill(x, y, x + Mth.ceil((this.menu.data().get(k + j * 9) / 1000f) * 14f), y + 1, new Color(.86f,.55f,.53f,1).toIntARGB());
            }
        }
    }
}