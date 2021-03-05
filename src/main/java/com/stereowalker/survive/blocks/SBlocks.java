package com.stereowalker.survive.blocks;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.fluid.SFluids;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.IForgeRegistry;

public class SBlocks {
	private static final List<Block> BLOCKS = new ArrayList<Block>();
	public static final List<ItemGroup> ITEMGROUPS = new ArrayList<ItemGroup>();
	public static final List<Block> BLOCKITEMS = new ArrayList<Block>();
	//-Fluids-\\
	public static final Block PURIFIED_WATER = register("purified_water", new FlowingFluidBlock(() -> SFluids.PURIFIED_WATER, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));

	public static Block register(String name, Block block) {
		block.setRegistryName(Survive.location(name));
		SBlocks.BLOCKS.add(block);
		return block;
	}

	public static Block registerModded(boolean modIsLoaded, String name, Block block) {
		return modIsLoaded ? register(name, block) : block;
	}

	public static Block register(String name, ItemGroup group, Block block) {
		SBlocks.BLOCKITEMS.add(block);
		SBlocks.ITEMGROUPS.add(group);
		return register(name, block);
	}

	public static void registerAll(IForgeRegistry<Block> registry) {
		for(Block block: BLOCKS) {
			registry.register(block);
			Survive.debug("Block: \""+block.getRegistryName().toString()+"\" registered");
		}
		Survive.debug("All Blocks Registered");
	}

}

