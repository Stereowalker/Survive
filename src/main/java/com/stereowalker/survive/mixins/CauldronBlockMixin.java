package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.stereowalker.survive.blocks.PotashCauldronBlock;
import com.stereowalker.survive.blocks.SBlocks;
import com.stereowalker.survive.item.SItems;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

@Mixin(CauldronBlock.class)
public abstract class CauldronBlockMixin extends Block {

	public CauldronBlockMixin(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Inject(method = "onBlockActivated", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void onBlockActivatedMixin(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit, CallbackInfoReturnable<ActionResultType> cir, ItemStack itemstack, int i, Item item) {
		if (i == 0 && item == SItems.POTASH_SOLUTION) {
			if (!worldIn.isRemote) {
				if (!player.abilities.isCreativeMode) {
					ItemStack itemstack3 = new ItemStack(Items.GLASS_BOTTLE);
					player.addStat(Stats.USE_CAULDRON);
					player.setHeldItem(handIn, itemstack3);
					if (player instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
					}
				}

				worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
				worldIn.setBlockState(pos, SBlocks.POTASH_CAULDRON.getDefaultState().with(PotashCauldronBlock.LEVEL, 1));
			}
			cir.setReturnValue(ActionResultType.func_233537_a_(worldIn.isRemote));
		}
	}

	@Shadow public void setWaterLevel(World worldIn, BlockPos pos, BlockState state, int level) {}

}
