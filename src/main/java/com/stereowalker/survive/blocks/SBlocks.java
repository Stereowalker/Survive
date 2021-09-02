package com.stereowalker.survive.blocks;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.fluid.SFluids;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.registries.IForgeRegistry;

public class SBlocks {
	private static final List<Block> BLOCKS = new ArrayList<Block>();
	//-Fluids-\\
	public static final Block PURIFIED_WATER = register("purified_water", new FlowingFluidBlock(() -> SFluids.PURIFIED_WATER, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
	public static final Block POTASH_CAULDRON = register("potash_cauldron", new PotashCauldronBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.STONE).setRequiresTool().hardnessAndResistance(2.0F).notSolid()));
	   
	public static Block register(String name, Block block) {
		block.setRegistryName(Survive.getInstance().location(name));
		SBlocks.BLOCKS.add(block);
		return block;
	}

	public static Block registerModded(boolean modIsLoaded, String name, Block block) {
		return modIsLoaded ? register(name, block) : block;
	}

	public static void registerAll(IForgeRegistry<Block> registry) {
		for(Block block: BLOCKS) {
			registry.register(block);
			Survive.getInstance().debug("Block: \""+block.getRegistryName().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Blocks Registered");
	}

}

