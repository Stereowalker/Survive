package com.stereowalker.survive.world.level.block;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.core.cauldron.SCauldronInteraction;
import com.stereowalker.survive.world.level.material.SFluids;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

@RegistryHolder(namespace = Survive.MOD_ID)
public class SBlocks {
	@RegistryObject("purified_water")
	public static final Block PURIFIED_WATER = new LiquidBlock(() -> SFluids.PURIFIED_WATER, VersionHelper.blockPropertyWithId("survive:purified_water").mapColor(MapColor.WATER).noCollission().strength(100.0F).noLootTable()
            .replaceable()
            .pushReaction(PushReaction.DESTROY)
            .liquid()
            .sound(SoundType.EMPTY));
	@RegistryObject("purified_water_cauldron")
	public static final Block PURIFIED_WATER_CAULDRON = new LayeredCauldronBlock(VersionHelper.copyBlockPropertyWithId(Blocks.CAULDRON, "survive:purified_water_cauldron"), (precipitation) -> false, SCauldronInteraction.PURIFIED_WATER);
	@RegistryObject("drying_cauldron")
	public static final Block DRYING_CAULDRON = new DryingCauldronBlock(VersionHelper.copyBlockPropertyWithId(Blocks.CAULDRON, "survive:drying_cauldron"));
	@RegistryObject("temperature_regulator")
	public static final Block TEMPERATURE_REGULATOR = new TemperatureRegulatorBlock(VersionHelper.blockPropertyWithId("survive:temperature_regulator").mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(3.5F).noOcclusion());
	@RegistryObject("plated_temperature_regulator")
	public static final Block PLATED_TEMPERATURE_REGULATOR = new PlatedTemperatureRegulatorBlock(VersionHelper.copyBlockPropertyWithId(TEMPERATURE_REGULATOR, "survive:plated_temperature_regulator").lightLevel((state) -> {
		return state.getValue(PlatedTemperatureRegulatorBlock.POWERED) ? state.getValue(PlatedTemperatureRegulatorBlock.PLATE_COUNT) : 0;
	}));
	@RegistryObject("salt_box")
	public static final Block SALT_BOX = new SaltBoxBlock(
			VersionHelper.blockPropertyWithId("survive:salt_box").mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava()
	        );
	@RegistryObject("realistic_campfire")
	public static final Block REALISTIC_CAMPFIRE = new RealisticCampfireBlock(true, 1,
			VersionHelper.blockPropertyWithId("survive:realistic_campfire").mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(2.0F)
                .sound(SoundType.WOOD).lightLevel(bs -> bs.getValue(RealisticCampfireBlock.HEAT) * 3 + 3).noOcclusion().ignitedByLava()
        );
}

