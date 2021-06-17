package com.stereowalker.survive.mixins;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.EnergyStats;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

@Mixin(Block.class)
public class BlockMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), method = {"harvestBlock"})
	public void exhaustStaminaWhenBreakingBlock(PlayerEntity player, float value, World worldIn, PlayerEntity player2, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		if (Config.enable_stamina) {
			EnergyStats energyStats = SurviveEntityStats.getEnergyStats(player);
			if (ForgeHooks.canHarvestBlock(state, player2, worldIn, pos)) {
				energyStats.addExhaustion(player, 0.25F);
			} else {
				energyStats.addExhaustion(player, 0.025F);
			}
			SurviveEntityStats.setEnergyStats(player, energyStats);
		}
		else {
			player.addExhaustion(value);
		}
	}
}
