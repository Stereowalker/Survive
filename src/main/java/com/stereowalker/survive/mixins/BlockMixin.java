package com.stereowalker.survive.mixins;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.survive.config.Config;
import com.stereowalker.survive.entity.SurviveEntityStats;
import com.stereowalker.survive.util.NutritionStats;
import com.stereowalker.survive.util.StaminaStats;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock implements IItemProvider, net.minecraftforge.common.extensions.IForgeBlock {
	
	public BlockMixin(Properties properties) {
		super(properties);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"), method = {"harvestBlock"})
	public void exhaustStaminaWhenBreakingBlock(PlayerEntity player, float value, World worldIn, PlayerEntity player2, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		if (Config.enable_stamina) {
			StaminaStats energyStats = SurviveEntityStats.getEnergyStats(player);
			if (ForgeHooks.canHarvestBlock(state, player2, worldIn, pos)) {
				energyStats.addExhaustion(player, 0.5F);
			} else {
				energyStats.addExhaustion(player, 0.05F);
			}
			SurviveEntityStats.setStaminaStats(player, energyStats);
		}
		else if (Config.nutrition_enabled) {
			NutritionStats nutritionStats = SurviveEntityStats.getNutritionStats(player);
			nutritionStats.removeCarbs(MathHelper.ceil(value*2.5f));
		}
		else {
			player.addExhaustion(value);
		}
	}
}
