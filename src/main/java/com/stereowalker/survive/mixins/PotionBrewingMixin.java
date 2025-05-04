package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {

	@Redirect(method = "mixVanilla", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/item/alchemy/PotionContents;createItemStack(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack preserveComponents(Item pItem, Holder<Potion> pPotion, ItemStack pPotion2, ItemStack pPotionItem) {
		ItemStack stack = PotionContents.createItemStack(pPotionItem.getItem(), pPotion);
		if (SDataComponents.DRINKS_LEFT_D.hasData(stack))
			SDataComponents.DRINKS_LEFT_D.setData(stack, SDataComponents.DRINKS_LEFT_D.getData(pPotionItem));
		return stack;
	}

}
