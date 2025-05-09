package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.survive.hooks.ColdMenu;
import com.stereowalker.survive.hooks.ColdStorage;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;

@Mixin(ChestMenu.class)
public abstract class ChestMenuMixin extends AbstractContainerMenu implements ColdMenu {
	ContainerData data;

	protected ChestMenuMixin(MenuType<?> pMenuType, int pContainerId) {
		super(pMenuType, pContainerId);
	}

	@Inject(method = "Lnet/minecraft/world/inventory/ChestMenu;<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;I)V", at = @At("TAIL"))
	private void loadAdditional(MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, Container pContainer, int pRows, CallbackInfo ci) {
		if (pContainer instanceof ColdStorage cold) {
			data = cold.data();
	        checkContainerDataCount(data, 2);
		}
		else {
			data = new SimpleContainerData(2);
		}
		this.addDataSlots(data);
	}
	
	@Override
	public ContainerData data() {
		return data;
	}
}
