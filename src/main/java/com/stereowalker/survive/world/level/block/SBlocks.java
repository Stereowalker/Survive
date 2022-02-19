package com.stereowalker.survive.world.level.block;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.cauldron.SCauldronInteraction;
import com.stereowalker.survive.world.level.material.SFluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.IForgeRegistry;

public class SBlocks {
	private static final List<Block> BLOCKS = new ArrayList<Block>();
	//-Fluids-\\
	public static final Block PURIFIED_WATER = register("purified_water", new LiquidBlock(() -> SFluids.PURIFIED_WATER, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()));
	public static final Block PURIFIED_WATER_CAULDRON = register("purified_water_cauldron", new LayeredCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON), PotashCauldronBlock.NONE, SCauldronInteraction.PURIFIED_WATER));
	public static final Block POTASH_CAULDRON = register("potash_cauldron", new PotashCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON)));
	public static final Block TEMPERATURE_REGULATOR = register("temperature_regulator", new TemperatureRegulatorBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5F).noOcclusion()));
	public static final Block PLATED_TEMPERATURE_REGULATOR = register("plated_temperature_regulator", new PlatedTemperatureRegulatorBlock(BlockBehaviour.Properties.copy(TEMPERATURE_REGULATOR).lightLevel((state) -> {
	      return state.getValue(PlatedTemperatureRegulatorBlock.POWERED) ? state.getValue(PlatedTemperatureRegulatorBlock.PLATE_COUNT) : 0;
	   })));
	     
	public static Block register(String name, Block block) {
		block.setRegistryName(new ResourceLocation("survive", name));
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

