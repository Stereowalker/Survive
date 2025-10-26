package com.stereowalker.survive.world.level.block;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.cauldron.SCauldronInteraction;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;

import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SBlocks {
	@RegistryObject("purified_water")
	public static final Block PURIFIED_WATER = new LiquidBlock(() -> SFluids.PURIFIED_WATER, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().strength(100.0F).noLootTable()
            .replaceable()
            .pushReaction(PushReaction.DESTROY)
            .liquid()
            .sound(SoundType.EMPTY));
	@RegistryObject("purified_water_cauldron")
	public static final Block PURIFIED_WATER_CAULDRON = new LayeredCauldronBlock(Precipitation.NONE, SCauldronInteraction.PURIFIED_WATER, BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON));
	@RegistryObject("drying_cauldron")
	public static final Block DRYING_CAULDRON = new DryingCauldronBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON));
	@RegistryObject("temperature_regulator")
	public static final Block TEMPERATURE_REGULATOR = new TemperatureRegulatorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(3.5F).noOcclusion());
	@RegistryObject("plated_temperature_regulator")
	public static final Block PLATED_TEMPERATURE_REGULATOR = new PlatedTemperatureRegulatorBlock(BlockBehaviour.Properties.ofLegacyCopy(TEMPERATURE_REGULATOR).lightLevel((state) -> {
		return state.getValue(PlatedTemperatureRegulatorBlock.POWERED) ? state.getValue(PlatedTemperatureRegulatorBlock.PLATE_COUNT) : 0;
	}));
	@RegistryObject("salt_box")
	public static final Block SALT_BOX = new SaltBoxBlock(
	            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava()
	        );
	@RegistryObject("realistic_campfire")
	public static final Block REALISTIC_CAMPFIRE = new RealisticCampfireBlock(true, 1,
            BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(2.0F)
                .sound(SoundType.WOOD).lightLevel(bs -> bs.getValue(RealisticCampfireBlock.HEAT) * 3 + 3).noOcclusion().ignitedByLava()
        );
}

