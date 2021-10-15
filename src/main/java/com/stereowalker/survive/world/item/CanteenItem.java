package com.stereowalker.survive.world.item;

import java.util.List;

import com.stereowalker.survive.Survive;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CanteenItem extends Item {

	public CanteenItem(Properties properties) {
		super(properties);
	}
	
	public static CompoundTag canteenTag(int drinks) {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("DrinksLeft", drinks);
		return nbt;
	}

	public static ItemStack addPropertiesToCanteen(ItemStack stack, int drinks) {
		stack.setTag(canteenTag(drinks));
		return stack;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ItemStack getDefaultInstance() {
		return addPropertiesToCanteen(new ItemStack(this), Survive.CONFIG.canteen_fill_amount);
	}
	
	public int getDrinksLeft(ItemStack stack) {
		return stack.getOrCreateTag().getInt("DrinksLeft");
	}
	
	public void setDrinksLeft(ItemStack stack, int drinks) {
		stack.getOrCreateTag().putInt("DrinksLeft", Mth.clamp(drinks, 0, Survive.CONFIG.canteen_fill_amount));
	}
	
	public void decrementDrinks(ItemStack stack) {
		setDrinksLeft(stack, getDrinksLeft(stack) - 1);
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
		Player entityplayer = entityLiving instanceof Player ? (Player)entityLiving : null;
		if (entityplayer instanceof ServerPlayer) {
			CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)entityplayer, stack);
		}
		
		if (entityplayer != null) {
			entityplayer.awardStat(Stats.ITEM_USED.get(this));
		}
		
		if (getDrinksLeft(stack) <= 1) {
			if (entityplayer == null || !entityplayer.getAbilities().instabuild) {
				stack.shrink(1);
			}

			if (entityplayer == null || !entityplayer.getAbilities().instabuild) {
				if (stack.isEmpty()) {
					return new ItemStack(SItems.CANTEEN);
				}

				if (entityplayer != null) {
					entityplayer.getInventory().add(new ItemStack(SItems.CANTEEN));
				}
			}
		}
		
		if (getDrinksLeft(stack) > 1) {
			if (entityplayer == null || !entityplayer.getAbilities().instabuild) {
				decrementDrinks(stack);
			}
		}

		return stack;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
	 * {@link #onItemUse}.
	 */
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		if (getDrinksLeft(stack) < Survive.CONFIG.canteen_fill_amount) {
			HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
			BlockPos blockpos = ((BlockHitResult)raytraceresult).getBlockPos();
			if (worldIn.getFluidState(blockpos).is(FluidTags.WATER)) {
				setDrinksLeft(stack, Survive.CONFIG.canteen_fill_amount);
			}
		}
		playerIn.startUsingItem(handIn);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(new TranslatableComponent("tooltip.drinks_left").append(": "+getDrinksLeft(stack)).withStyle(ChatFormatting.AQUA));
		if (this == SItems.PURIFIED_WATER_CANTEEN)
			tooltip.add(new TranslatableComponent("tooltip.drink_purified").withStyle(ChatFormatting.AQUA));
		if (this == SItems.WATER_CANTEEN)
			tooltip.add(new TranslatableComponent("tooltip.drink_not_purified").withStyle(ChatFormatting.RED));
	}

}
