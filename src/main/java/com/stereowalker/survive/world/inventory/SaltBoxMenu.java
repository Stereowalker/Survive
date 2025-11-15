package com.stereowalker.survive.world.inventory;

import com.stereowalker.survive.world.level.block.entity.SaltBoxBlockEntity;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SaltBoxMenu extends AbstractContainerMenu {
    private static final int SLOTS_PER_ROW = 9;
    private final Container container;
    private final int containerRows;
    ContainerData data;

    public SaltBoxMenu(int id, Inventory playerInventory) {
		this(SMenuType.SALT_BOX, id, playerInventory, new SimpleContainer(27), new SimpleContainerData(27), 3);
	}
    
    public SaltBoxMenu(int id, Inventory playerInventory, Container pContainer, ContainerData data) {
		this(SMenuType.SALT_BOX, id, playerInventory, pContainer, data, 3);
	}

    public SaltBoxMenu(MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, Container pContainer, ContainerData containerData, int pRows) {
        super(pType, pContainerId);
        checkContainerSize(pContainer, pRows * 9);
        this.container = pContainer;
        this.containerRows = pRows;
        pContainer.startOpen(pPlayerInventory.player);
        
        data = containerData;
        checkContainerDataCount(data, 27);
		this.addDataSlots(data);
        
        int i = (this.containerRows - 3) * (18 + 3);
        i = (this.containerRows * (18 + 3)) - 73;

        for (int j = 0; j < this.containerRows; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(pContainer, k + j * 9, 8 + k * 18, 18 + j * (18 + 3)));
            }
        }

        for (int l = 0; l < 3; l++) {
            for (int j1 = 0; j1 < 9; j1++) {
                this.addSlot(new Slot(pPlayerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(pPlayerInventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < this.containerRows * 9) {
                if (!this.moveItemStackTo(itemstack1, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.container.stopOpen(pPlayer);
    }

    public Container getContainer() {
        return this.container;
    }

    public int getRowCount() {
        return this.containerRows;
    }
    
	public ContainerData data() {
		return data;
	}
}