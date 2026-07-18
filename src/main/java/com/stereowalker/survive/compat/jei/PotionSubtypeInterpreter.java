package com.stereowalker.survive.compat.jei;

import org.jspecify.annotations.Nullable;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public class PotionSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final PotionSubtypeInterpreter INSTANCE = new PotionSubtypeInterpreter();

	private PotionSubtypeInterpreter() {

	}
	
	@Override
	@Nullable
	public Object getSubtypeData(ItemStack itemStack, UidContext context) {
		PotionContents contents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
		return contents.potion()
			.orElse(null);
	}

//	@Override
	public String getSubtypeData2(ItemStack itemStack, UidContext context) {
		if (itemStack.getComponentsPatch().isEmpty()) {
			return null;
		}
		PotionContents contents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
		String itemDescriptionId = itemStack.getItem().getDescriptionId();
		String potionEffectId = contents.potion().map(Holder::getRegisteredName).orElse("none");
		return itemDescriptionId + ".effect_id." + potionEffectId;
	}
}
