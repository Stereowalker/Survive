package com.stereowalker.survive.compat.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;

public class PotionSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
	public static final PotionSubtypeInterpreter INSTANCE = new PotionSubtypeInterpreter();

	private PotionSubtypeInterpreter() {

	}

	@Override
	public String apply(ItemStack itemStack, UidContext context) {
		if (!itemStack.hasTag()) {
			return IIngredientSubtypeInterpreter.NONE;
		}
//		PotionContents contents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
		PotionUtils.getPotion(itemStack);
		String itemDescriptionId = itemStack.getItem().getDescriptionId();
		String potionEffectId = PotionUtils.getPotion(itemStack).getName("");
		return itemDescriptionId + ".effect_id." + potionEffectId;
	}
}
