package com.stereowalker.survive.compat;

import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.stereowalker.survive.Survive;

import io.github.edwinmindcraft.calio.api.event.DynamicRegistrationEvent;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class OriginsCompat {
	public static void initOriginsPatcher() {
		MinecraftForge.EVENT_BUS.addGenericListener(Origin.class, (Consumer<DynamicRegistrationEvent<Origin>>)event-> {
			modifyOriginss(event);
		});
	}

	public static void modifyOriginss(DynamicRegistrationEvent<Origin> event) {
		Origin origin = event.getOriginal();
		if (Lists.newArrayList(Survive.CONFIG.originsHeat.split(",")).contains(event.getRegistryName().toString())) {
			if (origin.getPowers() != null) {
				Survive.getInstance().getLogger().info("Gave "+event.getRegistryName()+" the Heat Resistance power");
				event.setNewEntry(new Origin(
						new ImmutableSet.Builder<ResourceLocation>().addAll(origin.getPowers()).add(Survive.getInstance().location("heat_resistance")).build(),
						origin.getIcon(),
						origin.isUnchoosable(),
						origin.getOrder(),
						origin.getImpact(),
						origin.getName(),
						origin.getDescription(),
						origin.getUpgrades(), origin.isSpecial()));
			}
		}
		if (Lists.newArrayList(Survive.CONFIG.originsCold.split(",")).contains(event.getRegistryName().toString())) {
			if (origin.getPowers() != null) {
				Survive.getInstance().getLogger().info("Gave "+event.getRegistryName()+" the Cold Blooded power");
				event.setNewEntry(new Origin(
						new ImmutableSet.Builder<ResourceLocation>().addAll(origin.getPowers()).add(Survive.getInstance().location("cold_blooded")).build(),
						origin.getIcon(),
						origin.isUnchoosable(),
						origin.getOrder(),
						origin.getImpact(),
						origin.getName(),
						origin.getDescription(),
						origin.getUpgrades(), origin.isSpecial()));
			}
		}
		if (Lists.newArrayList(Survive.CONFIG.originsAirFromCanteen.split(",")).contains(event.getRegistryName().toString())) {
			if (origin.getPowers() != null) {
				Survive.getInstance().getLogger().info("Gave "+event.getRegistryName()+" the Air From Canteen power");
				event.setNewEntry(new Origin(
						new ImmutableSet.Builder<ResourceLocation>().addAll(origin.getPowers()).add(Survive.getInstance().location("air_from_canteen")).build(),
						origin.getIcon(),
						origin.isUnchoosable(),
						origin.getOrder(),
						origin.getImpact(),
						origin.getName(),
						origin.getDescription(),
						origin.getUpgrades(), origin.isSpecial()));
			}
		}
		
	}
}
