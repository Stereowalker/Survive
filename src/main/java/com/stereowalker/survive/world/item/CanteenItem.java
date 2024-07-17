package com.stereowalker.survive.world.item;

import java.util.List;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CanteenItem extends Item {

	public CanteenItem(Properties properties) {
		super(properties);
	}

	public static ItemStack addToCanteen(ItemStack stack, int drinks, PotionContents potion) {
		stack.set(SDataComponents.DRINKS_LEFT, drinks);
		stack.set(DataComponents.POTION_CONTENTS, potion);
		return stack;
	}

	public static ItemStack addToCanteen(ItemStack stack, int drinks, Holder<Potion> potion) {
		return addToCanteen(stack, drinks, new PotionContents(potion));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ItemStack getDefaultInstance() {
		return addToCanteen(super.getDefaultInstance(), Survive.THIRST_CONFIG.canteen_fill_amount, Potions.WATER);
	}

	public void setDrinksLeft(ItemStack stack, int drinks) {
		stack.set(SDataComponents.DRINKS_LEFT, Mth.clamp(drinks, 0, Survive.THIRST_CONFIG.canteen_fill_amount));
	}

	public void decrementDrinks(ItemStack stack) {
		setDrinksLeft(stack, stack.get(SDataComponents.DRINKS_LEFT) - 1);
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@Override
	public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
		Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
		if (player instanceof ServerPlayer) {
			CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, pStack);
		}

		if (!pLevel.isClientSide) {
			PotionContents potioncontents = pStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            potioncontents.forEachEffect(mobeffectinstance -> {
                if (mobeffectinstance.getEffect().value().isInstantenous()) {
                    mobeffectinstance.getEffect().value().applyInstantenousEffect(player, player, pEntityLiving, mobeffectinstance.getAmplifier(), 1.0);
                } else {
                    pEntityLiving.addEffect(mobeffectinstance);
                }
            });
		}

		if (player != null) {
			player.awardStat(Stats.ITEM_USED.get(this));
		}

		if (pStack.get(SDataComponents.DRINKS_LEFT) <= 1) {
			if (player == null || !player.getAbilities().instabuild) {
				pStack.shrink(1);
			}

			if (player == null || !player.getAbilities().instabuild) {
				if (pStack.isEmpty()) {
					return new ItemStack(SItems.CANTEEN);
				}

				if (player != null) {
					player.getInventory().add(new ItemStack(SItems.CANTEEN));
				}
			}
		}

		if (pStack.get(SDataComponents.DRINKS_LEFT) > 1) {
			if (player == null || !player.getAbilities().instabuild) {
				decrementDrinks(pStack);
			}
		}

		pEntityLiving.gameEvent(GameEvent.DRINK);
		return pStack;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getUseDuration(ItemStack stack, LivingEntity pEntity) {
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
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		ItemStack stack = pPlayer.getItemInHand(pHand);
		PotionContents potioncontents = stack.get(DataComponents.POTION_CONTENTS);
		if (Survive.POTION_FLUID_MAP.containsKey(potioncontents.potion().get())) {
			if (stack.get(SDataComponents.DRINKS_LEFT) < Survive.THIRST_CONFIG.canteen_fill_amount) {
				HitResult raytraceresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);
				BlockPos blockpos = ((BlockHitResult)raytraceresult).getBlockPos();
				if (pLevel.getFluidState(blockpos).is(FluidTags.WATER) && Survive.POTION_FLUID_MAP.get(potioncontents.potion().get()).contains(pLevel.getFluidState(blockpos).getType())) {
					setDrinksLeft(stack, Survive.THIRST_CONFIG.canteen_fill_amount);
				}
			}
			pPlayer.startUsingItem(pHand);
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, pPlayer.getItemInHand(pHand));
		} else {
			return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
		}
	}

	@Override
	public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
		pTooltipComponents.add(Component.translatable("tooltip.drinks_left").append(": "+pStack.get(SDataComponents.DRINKS_LEFT)).withStyle(ChatFormatting.AQUA));
		PotionContents potioncontents = pStack.get(DataComponents.POTION_CONTENTS);
		if (potioncontents.potion().isPresent()) {
			if (Survive.POTION_FLUID_MAP.containsKey(potioncontents.potion().get()))
				pTooltipComponents.add(Component.translatable(Potion.getName(potioncontents.potion(), this.getDescriptionId()+".effect.")).withStyle(ChatFormatting.YELLOW));
			else
				pTooltipComponents.add(Component.translatable(Potion.getName(potioncontents.potion(), "item.minecraft.potion.effect.")).withStyle(ChatFormatting.GOLD));
		}
        if (potioncontents != null) {
            potioncontents.addPotionTooltip(pTooltipComponents::add, 1.0F, pContext.tickRate());
        }
	}
}
