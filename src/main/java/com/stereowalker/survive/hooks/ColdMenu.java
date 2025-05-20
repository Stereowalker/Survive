package com.stereowalker.survive.hooks;

import net.minecraft.world.inventory.ContainerData;

public interface ColdMenu {
	public ContainerData data();
	
	default int coldness() {
		if (data() != null) {
			return data().get(0);
		}
		return 0;
	}
	
	default int maxColdness() {
		if (data() != null) {
			return data().get(1);
		}
		return 0;
	}
}
