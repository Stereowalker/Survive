package com.stereowalker.survive.world.level.block.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.level.block.SBlocks;
import com.stereowalker.unionlib.api.registries.RegistryCollector;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SBlockEntityType {
	private static final Map<ResourceLocation,BlockEntityType<?>> TILE_ENTITY_TYPES = new HashMap<ResourceLocation,BlockEntityType<?>>();

	@RegistryObject("salt_box")
	public static final BlockEntityType<SaltBoxBlockEntity> SALT_BOX = register("salt_box", BlockEntityType.Builder.of(SaltBoxBlockEntity::new, SBlocks.SALT_BOX)
	.build(Util.fetchChoiceType(References.BLOCK_ENTITY, "salt_box")));
	
	@RegistryObject("drying_cauldron")
	public static final BlockEntityType<DryingCauldronBlockEntity> DRYING_CAULDRON = register("drying_cauldron", BlockEntityType.Builder.of(DryingCauldronBlockEntity::new, SBlocks.DRYING_CAULDRON)
	.build(Util.fetchChoiceType(References.BLOCK_ENTITY, "drying_cauldron")));
	
	@RegistryObject("realistic_campfire")
	public static final BlockEntityType<RealisticCampfireBlockEntity> REALISIC_CAMPFIRE = register("realistic_campfire", BlockEntityType.Builder.of(RealisticCampfireBlockEntity::new, SBlocks.REALISTIC_CAMPFIRE)
	.build(Util.fetchChoiceType(References.BLOCK_ENTITY, "realistic_campfire")));
			

	public static void registerAll(RegistryCollector.Custom<BlockEntityType<?>> registry) {
		for(Entry<ResourceLocation, BlockEntityType<?>> entitytype: TILE_ENTITY_TYPES.entrySet()) {
			registry.register(entitytype.getKey(), entitytype.getValue());
		}
	}

	private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> builder){
		TILE_ENTITY_TYPES.put(Survive.getInstance().location(name), builder);
		return builder;
	}
}
