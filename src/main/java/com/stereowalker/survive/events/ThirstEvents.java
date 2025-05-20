package com.stereowalker.survive.events;

import com.stereowalker.survive.json.BiomeJsonHolder;
import com.stereowalker.survive.json.FluidJsonHolder;
import com.stereowalker.survive.network.protocol.game.ServerboundInteractWithWaterPacket;
import com.stereowalker.survive.world.DataMaps;
import com.stereowalker.unionlib.api.insert.InsertResultCanceller;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class ThirstEvents {

	public static void interactWithWaterSourceBlock(Player player, Level level, InteractionHand hand, BlockHitResult result, InsertResultCanceller<InteractionResult> cancel) {
		ItemStack stack = player.getItemInHand(hand);
		BlockPos pos = result.getBlockPos();
		HitResult raytraceresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		BlockPos sourcepos = ((BlockHitResult)raytraceresult).getBlockPos();
		BlockState state = level.getBlockState(result.getBlockPos());
		Fluid fluid = level.getFluidState(sourcepos).getType();
		BlockState stateUnder = level.getBlockState(pos.below());
		if (level.isClientSide && ServerboundInteractWithWaterPacket.isValidStack(stack)) {
			//Source Block Of Water
			if (DataMaps.Client.fluid.containsKey(RegistryHelper.fluids().getKey(fluid))) {
				FluidJsonHolder fluidHolder = DataMaps.Client.fluid.get(RegistryHelper.fluids().getKey(fluid));
				float thirstChance = fluidHolder.getThirstChance();
				if (DataMaps.Client.biome.containsKey(level.getBiome(sourcepos).unwrapKey().get().location())) {
					BiomeJsonHolder biomeData = DataMaps.Client.biome.get(level.getBiome(sourcepos).unwrapKey().get().location());
					if (biomeData.getThirstChance() >= 0)
						thirstChance = biomeData.getThirstChance();
				}
				cancel.cancel(InteractionResult.SUCCESS);
				new ServerboundInteractWithWaterPacket(sourcepos, thirstChance, fluidHolder.getThirstAmount(), fluidHolder.getHydrationAmount(), hand).send();
			}
			//Cauldron
			if (state.getBlock() == Blocks.WATER_CAULDRON) {
				int i = state.getValue(LayeredCauldronBlock.LEVEL);
				if (i > 0) {
					cancel.cancel(InteractionResult.SUCCESS);
					if (stateUnder.getBlock() == Blocks.CAMPFIRE && stateUnder.getValue(BlockStateProperties.LIT)) {
						new ServerboundInteractWithWaterPacket(pos, 0.0f, 4.0D, hand).send();
					}
					else {
						new ServerboundInteractWithWaterPacket(pos, 0.5f, 4.0D, hand).send();
					}
				}
			}
		}
	}

	public static void interactWithWaterSourceBlock(Player player, Level level, InteractionHand hand, InsertResultCanceller<InteractionResultHolder<ItemStack>> cancel) {
		ItemStack stack = player.getItemInHand(hand);
		HitResult raytraceresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		BlockPos sourcepos = ((BlockHitResult)raytraceresult).getBlockPos();
		Fluid fluid = level.getFluidState(sourcepos).getType();
		BlockPos blockpos = ((BlockHitResult)raytraceresult).getBlockPos();
		if (level.isClientSide && ServerboundInteractWithWaterPacket.isValidStack(stack)) {
			//Source Block Of Water
			if (DataMaps.Client.fluid.containsKey(RegistryHelper.fluids().getKey(fluid))) {
				FluidJsonHolder fluidHolder = DataMaps.Client.fluid.get(RegistryHelper.fluids().getKey(fluid));
				float thirstChance = fluidHolder.getThirstChance();
				if (DataMaps.Client.biome.containsKey(level.getBiome(blockpos).unwrapKey().get().location())) {
					BiomeJsonHolder biomeData = DataMaps.Client.biome.get(level.getBiome(blockpos).unwrapKey().get().location());
					if (biomeData.getThirstChance() >= 0)
						thirstChance = biomeData.getThirstChance();
				}
				cancel.cancel(InteractionResultHolder.success(stack));
				new ServerboundInteractWithWaterPacket(blockpos, thirstChance, fluidHolder.getThirstAmount(), fluidHolder.getHydrationAmount(), hand).send();
			}
		}
	}
	
	//TODO: AT this later
	protected static BlockHitResult getPlayerPOVHitResult(Level pLevel, Player pPlayer, ClipContext.Fluid pFluidMode) {
        /*Vec3 vec3 = pPlayer.getEyePosition();
        Vec3 vec31 = vec3.add(pPlayer.calculateViewVector(pPlayer.getXRot(), pPlayer.getYRot()).scale(pPlayer.blockInteractionRange()));
        return pLevel.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, pFluidMode, pPlayer));*/
		
		float f = pPlayer.getXRot();
		float f1 = pPlayer.getYRot();
		Vec3 vec3d = pPlayer.getEyePosition(1.0F);
		float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
		float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = pPlayer.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue();
		Vec3 vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
		return pLevel.clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, pFluidMode, pPlayer));
    }
}
