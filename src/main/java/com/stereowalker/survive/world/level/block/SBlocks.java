package com.stereowalker.survive.world.level.block;

import com.stereowalker.survive.core.cauldron.SCauldronInteraction;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

@RegistryHolder(registry = Block.class)
public class SBlocks {
	@RegistryObject("purified_water")
	public static final Block PURIFIED_WATER = new LiquidBlock(() -> SFluids.PURIFIED_WATER, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().strength(100.0F).noLootTable());
	@RegistryObject("purified_water_cauldron")
	public static final Block PURIFIED_WATER_CAULDRON = new LayeredCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON), PotashCauldronBlock.NONE, SCauldronInteraction.PURIFIED_WATER);
	@RegistryObject("potash_cauldron")
	public static final Block POTASH_CAULDRON = new PotashCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON));
	@RegistryObject("temperature_regulator")
	public static final Block TEMPERATURE_REGULATOR = new TemperatureRegulatorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(3.5F).noOcclusion());
	@RegistryObject("plated_temperature_regulator")
	public static final Block PLATED_TEMPERATURE_REGULATOR = new PlatedTemperatureRegulatorBlock(BlockBehaviour.Properties.copy(TEMPERATURE_REGULATOR).lightLevel((state) -> {
	      return state.getValue(PlatedTemperatureRegulatorBlock.POWERED) ? state.getValue(PlatedTemperatureRegulatorBlock.PLATE_COUNT) : 0;
	   }));
}

