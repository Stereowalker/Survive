package com.stereowalker.survive.world.inventory;

import com.stereowalker.survive.Survive;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SMenuType {
	@RegistryObject("salt_box")
	public static final MenuType<SaltBoxMenu> SALT_BOX = new MenuType<SaltBoxMenu>(SaltBoxMenu::new, FeatureFlags.VANILLA_SET);
}
