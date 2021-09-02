package com.stereowalker.survive.item;

import java.util.List;

import com.stereowalker.survive.config.Config;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CanteenItem extends Item {

	public CanteenItem(Properties properties) {
		super(properties);
	}
	
	public static CompoundNBT canteenTag(int drinks) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("DrinksLeft", drinks);
		return nbt;
	}

	public static ItemStack addPropertiesToCanteen(ItemStack stack, int drinks) {
		stack.setTag(canteenTag(drinks));
		return stack;
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack getDefaultInstance() {
		return addPropertiesToCanteen(new ItemStack(this), Config.canteen_fill_amount);
	}
	
	public int getDrinksLeft(ItemStack stack) {
		return stack.getOrCreateTag().getInt("DrinksLeft");
	}
	
	public void setDrinksLeft(ItemStack stack, int drinks) {
		stack.getOrCreateTag().putInt("DrinksLeft", MathHelper.clamp(drinks, 0, Config.canteen_fill_amount));
	}
	
	public void decrementDrinks(ItemStack stack) {
		setDrinksLeft(stack, getDrinksLeft(stack) - 1);
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		PlayerEntity entityplayer = entityLiving instanceof PlayerEntity ? (PlayerEntity)entityLiving : null;
		if (entityplayer instanceof ServerPlayerEntity) {
			CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)entityplayer, stack);
		}
		
		if (entityplayer != null) {
			entityplayer.addStat(Stats.ITEM_USED.get(this));
		}
		
		if (getDrinksLeft(stack) <= 1) {
			if (entityplayer == null || !entityplayer.abilities.isCreativeMode) {
				stack.shrink(1);
			}

			if (entityplayer == null || !entityplayer.abilities.isCreativeMode) {
				if (stack.isEmpty()) {
					return new ItemStack(SItems.CANTEEN);
				}

				if (entityplayer != null) {
					entityplayer.inventory.addItemStackToInventory(new ItemStack(SItems.CANTEEN));
				}
			}
		}
		
		if (getDrinksLeft(stack) > 1) {
			if (entityplayer == null || !entityplayer.abilities.isCreativeMode) {
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
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
	 * {@link #onItemUse}.
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (getDrinksLeft(stack) < Config.canteen_fill_amount) {
			RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
			BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
			if (worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
				setDrinksLeft(stack, Config.canteen_fill_amount);
			}
		}
		playerIn.setActiveHand(handIn);
		return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("tooltip.drinks_left").appendString(": "+getDrinksLeft(stack)).mergeStyle(TextFormatting.AQUA));
		if (this == SItems.PURIFIED_WATER_CANTEEN)
			tooltip.add(new TranslationTextComponent("tooltip.drink_purified").mergeStyle(TextFormatting.AQUA));
		if (this == SItems.WATER_CANTEEN)
			tooltip.add(new TranslationTextComponent("tooltip.drink_not_purified").mergeStyle(TextFormatting.RED));
	}

}
