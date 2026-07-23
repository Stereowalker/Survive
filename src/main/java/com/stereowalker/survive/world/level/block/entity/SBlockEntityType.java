package com.stereowalker.survive.world.level.block.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.level.block.SBlocks;
import com.stereowalker.unionlib.api.registries.RegistryCollector;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SBlockEntityType {
	private static final Map<Identifier,BlockEntityType<?>> TILE_ENTITY_TYPES = new HashMap<Identifier,BlockEntityType<?>>();

	@RegistryObject("salt_box")
	public static final BlockEntityType<SaltBoxBlockEntity> SALT_BOX = register("salt_box", new BlockEntityType<>(SaltBoxBlockEntity::new, Set.of(SBlocks.SALT_BOX)));
	
	@RegistryObject("drying_cauldron")
	public static final BlockEntityType<DryingCauldronBlockEntity> DRYING_CAULDRON = register("drying_cauldron", new BlockEntityType<>(DryingCauldronBlockEntity::new, Set.of(SBlocks.DRYING_CAULDRON)));
	
	@RegistryObject("realistic_campfire")
	public static final BlockEntityType<RealisticCampfireBlockEntity> REALISIC_CAMPFIRE = register("realistic_campfire", new BlockEntityType<>(RealisticCampfireBlockEntity::new, Set.of(SBlocks.REALISTIC_CAMPFIRE)));
			

	public static void registerAll(RegistryCollector.Custom<BlockEntityType<?>> registry) {
		for(Entry<Identifier, BlockEntityType<?>> entitytype: TILE_ENTITY_TYPES.entrySet()) {
			registry.register(entitytype.getKey(), entitytype.getValue());
		}
	}

	private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> builder){
		TILE_ENTITY_TYPES.put(Survive.getInstance().location(name), builder);
		return builder;
	}
}
