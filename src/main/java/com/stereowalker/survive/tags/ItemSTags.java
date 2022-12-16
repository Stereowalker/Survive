package com.stereowalker.survive.tags;

import com.stereowalker.survive.Survive;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemSTags {
	public static final TagKey<Item> ANIMAL_FAT = create(new ResourceLocation("animal_fat"));

	public ItemSTags() {
	}

	private static TagKey<Item> create(String pName) {
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Survive.MOD_ID, pName));
	}

	public static TagKey<Item> create(ResourceLocation name) {
		return TagKey.create(Registry.ITEM_REGISTRY, name);
	}
}
