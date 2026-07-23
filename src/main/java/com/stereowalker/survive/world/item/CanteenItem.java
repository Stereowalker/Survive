package com.stereowalker.survive.world.item;

import java.util.function.Consumer;

import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.item.component.SDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CanteenItem extends PotionItem {
	boolean isNetherite;

	public CanteenItem(Properties properties, boolean isNetherite) {
		super(properties);
		this.isNetherite = isNetherite;
	}

	public static ItemStack addToCanteen(ItemStack stack, int drinks, PotionContents potion) {
		SDataComponents.DRINKS_LEFT_D.setData(stack, drinks);
		stack.set(DataComponents.POTION_CONTENTS, potion);
		return stack;
	}

	public static ItemStack addToCanteen(ItemStack stack, int drinks, Holder<Potion> potion) {
		return addToCanteen(stack, drinks, new PotionContents(potion));
	}

	@Override
	public ItemStack getDefaultInstance() {
		return addToCanteen(super.getDefaultInstance(), Survive.THIRST_CONFIG.canteenFillAmount(isNetherite), Potions.WATER);
	}

	public void setDrinksLeft(ItemStack stack, int drinks) {
		SDataComponents.DRINKS_LEFT_D.setData(stack, Mth.clamp(drinks, 0, Survive.THIRST_CONFIG.canteenFillAmount(isNetherite)));
	}

	public void decrementDrinks(ItemStack stack) {
		setDrinksLeft(stack, SDataComponents.DRINKS_LEFT_D.getData(stack) - 1);
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

		if (player != null) {
			player.awardStat(Stats.ITEM_USED.get(this));
		}

		if (SDataComponents.DRINKS_LEFT_D.getData(pStack)  <= 1) {
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

		else if (SDataComponents.DRINKS_LEFT_D.getData(pStack) > 1) {
			if (player == null || !player.getAbilities().instabuild) {
				decrementDrinks(pStack);
			}
		}

		pEntityLiving.gameEvent(GameEvent.DRINK);
		return pStack;
	}

	/**
	 * Called when this item is used when targeting a Block
	 */
	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		Level level = pContext.getLevel();
		BlockPos blockpos = pContext.getClickedPos();
		Player player = pContext.getPlayer();
		ItemStack itemstack = pContext.getItemInHand();
		PotionContents potioncontents = itemstack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
		BlockState blockstate = level.getBlockState(blockpos);
		if (pContext.getClickedFace() != Direction.DOWN && blockstate.is(BlockTags.CONVERTABLE_TO_MUD) && potioncontents.is(Potions.WATER)) {
			level.playSound(null, blockpos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
			if (SDataComponents.DRINKS_LEFT_D.getData(itemstack) <= 1) {
				player.setItemInHand(pContext.getHand(), ItemUtils.createFilledResult(itemstack, player, new ItemStack(SItems.CANTEEN)));
			}
			else if (SDataComponents.DRINKS_LEFT_D.getData(itemstack) > 1) {
				decrementDrinks(itemstack);
			}
			player.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
			if (!level.isClientSide()) {
				ServerLevel serverlevel = (ServerLevel)level;

				for (int i = 0; i < 5; i++) {
					serverlevel.sendParticles(
							ParticleTypes.SPLASH,
							(double)blockpos.getX() + level.getRandom().nextDouble(),
							(double)(blockpos.getY() + 1),
							(double)blockpos.getZ() + level.getRandom().nextDouble(),
							1,
							0.0,
							0.0,
							0.0,
							1.0
							);
				}
			}

			level.playSound(null, blockpos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(null, GameEvent.FLUID_PLACE, blockpos);
			level.setBlockAndUpdate(blockpos, Blocks.MUD.defaultBlockState());
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.PASS;
		}
	}

	/**
	 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have different names based on their damage or NBT.
	 */
	@Override
	public Component getName(ItemStack itemStack) {
		return itemStack.getComponents().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
	}

	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
	 * {@link #onItemUse}.
	 */
	@Override
	public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		ItemStack stack = pPlayer.getItemInHand(pHand);
		PotionContents potioncontents = stack.get(DataComponents.POTION_CONTENTS);
		if (Survive.POTION_FLUID_MAP.containsKey(potioncontents.potion().get())) {
			if (SDataComponents.DRINKS_LEFT_D.getData(stack) < Survive.THIRST_CONFIG.canteenFillAmount(isNetherite)) {
				HitResult raytraceresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);
				BlockPos blockpos = ((BlockHitResult)raytraceresult).getBlockPos();
				if (pLevel.getFluidState(blockpos).is(FluidTags.WATER) && Survive.POTION_FLUID_MAP.get(potioncontents.potion().get()).contains(pLevel.getFluidState(blockpos).getType())) {
					setDrinksLeft(stack, Survive.THIRST_CONFIG.canteenFillAmount(isNetherite));
				}
			}
			pPlayer.startUsingItem(pHand);
			return InteractionResult.SUCCESS;
		} else {
			return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
		}
	}

	@Override
	public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, TooltipDisplay display, Consumer<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
		pTooltipComponents.accept(Component.translatable("tooltip.drinks_left").append(": "+SDataComponents.DRINKS_LEFT_D.getData(pStack)).withStyle(ChatFormatting.AQUA));
		PotionContents potioncontents = pStack.get(DataComponents.POTION_CONTENTS);
		if (potioncontents.potion().isPresent()) {
			if (Survive.POTION_FLUID_MAP.containsKey(potioncontents.potion().get()))
				pTooltipComponents.accept(Component.translatable("").append(potioncontents.getName(this.getDescriptionId()+".effect.")).withStyle(ChatFormatting.YELLOW));
			else
				pTooltipComponents.accept(Component.translatable("").append(potioncontents.getName("item.minecraft.potion.effect.")).withStyle(ChatFormatting.GOLD));
		}
//		if (potioncontents != null) {
//			potioncontents.addToTooltip(pContext, pTooltipComponents, pTooltipFlag, pStack.getComponents());
//		}
	}
}
