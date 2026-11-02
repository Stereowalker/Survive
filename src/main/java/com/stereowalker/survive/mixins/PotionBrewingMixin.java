package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

@Mixin(BrewingStandBlockEntity.class)
public class PotionBrewingMixin {

	@Redirect(method = "doBrew", at = @At(value = "INVOKE", ordinal = 1, target = "net/minecraft/core/NonNullList.set(ILjava/lang/Object;)Ljava/lang/Object;"))
	private static Object preserveComponents(NonNullList<ItemStack> items, int index, Object object, ServerLevel level, BlockPos pos, BrewingStandBlockEntity entity) {
		ItemStack stack = (ItemStack)object;
		if (SDataComponents.DRINKS_LEFT_D.hasData((ItemStack)items.get(index)))
			SDataComponents.DRINKS_LEFT_D.setData(stack, SDataComponents.DRINKS_LEFT_D.getData((ItemStack)items.get(index)));
		return items.set(index, stack);
	}

}
