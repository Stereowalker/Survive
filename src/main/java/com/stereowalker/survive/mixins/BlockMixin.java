package com.stereowalker.survive.mixins;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.needs.IRealisticEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour implements ItemLike {
	
	public BlockMixin(Properties properties) {
		super(properties);
	}

	@Redirect(method = "playerDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
	public void exhaustStaminaWhenBreakingBlock(Player player, float value, Level worldIn, Player player2, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
		if (Survive.STAMINA_CONFIG.enabled) {
			((IRealisticEntity)player).addStaminaExhaustion(Survive.STAMINA_CONFIG.stamina_drain_from_breaking_blocks_with_tool, "Player broke block");
			//TODO: Fix THIS
//			if (ForgeHooks.canHarvestBlock(state, player2, worldIn, pos)) {
//			} else {
//				energyStats.addExhaustion(player, Config.stamina_drain_from_breaking_blocks_without_tool);
//			}
		}
		else if (Survive.CONFIG.nutrition_enabled) {
			((IRealisticEntity)player).nutritionData().removeCarbs(Mth.ceil(value*2.5f));
		}
		else {
			player.causeFoodExhaustion(value);
		}
	}
}
