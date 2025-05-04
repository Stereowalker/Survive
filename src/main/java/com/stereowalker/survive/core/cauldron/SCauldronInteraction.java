package com.stereowalker.survive.core.cauldron;

import java.util.Map;

import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.HygieneItems;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.survive.world.level.block.SBlocks;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public interface SCauldronInteraction extends CauldronInteraction {
	public static CauldronInteraction.InteractionMap POTASH = CauldronInteraction.newInteractionMap("potash");
	public static CauldronInteraction.InteractionMap PURIFIED_WATER = CauldronInteraction.newInteractionMap("purified_water");

	CauldronInteraction FILL_PURIFIED_WATER = (p_175683_, p_175684_, p_175685_, p_175686_, p_175687_, p_175688_) -> {
		return CauldronInteraction.emptyBucket(p_175684_, p_175685_, p_175686_, p_175687_, p_175688_, SBlocks.PURIFIED_WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, Integer.valueOf(3)), SoundEvents.BUCKET_EMPTY);
	};

	static void addSurviveDefaultInteractions(Map<Item, CauldronInteraction> p_175648_) {
		p_175648_.put(SItems.PURIFIED_WATER_BUCKET, FILL_PURIFIED_WATER);
	}

	public static void bootStrap() {
		CauldronInteraction.addDefaultInteractions(POTASH.map());
		CauldronInteraction.addDefaultInteractions(PURIFIED_WATER.map());

		addSurviveDefaultInteractions(POTASH.map());
		addSurviveDefaultInteractions(PURIFIED_WATER.map());
		addSurviveDefaultInteractions(EMPTY.map());
		addSurviveDefaultInteractions(LAVA.map());
		addSurviveDefaultInteractions(POWDER_SNOW.map());
		addSurviveDefaultInteractions(WATER.map());

		PURIFIED_WATER.map().put(Items.BUCKET, (p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_) -> {
			return CauldronInteraction.fillBucket(p_175725_, p_175726_, p_175727_, p_175728_, p_175729_, p_175730_, new ItemStack(SItems.PURIFIED_WATER_BUCKET), (p_175660_) -> {
				return p_175660_.getValue(LayeredCauldronBlock.LEVEL) == 3;
			}, SoundEvents.BUCKET_FILL);
		});
		//Potash Solution Interactions
		fillEmptyCauldron(HygieneItems.POTASH_SOLUTION, new ItemStack(Items.GLASS_BOTTLE), SBlocks.POTASH_CAULDRON.defaultBlockState(), SoundEvents.BOTTLE_EMPTY);
		addToCauldron(POTASH.map(), HygieneItems.POTASH_SOLUTION, new ItemStack(Items.GLASS_BOTTLE), SoundEvents.BOTTLE_EMPTY);
		takeFromCauldron(POTASH.map(), Items.GLASS_BOTTLE, new ItemStack(HygieneItems.POTASH_SOLUTION), SoundEvents.BOTTLE_FILL);
		//Water Bowl Interactions
		takeFromCauldron(WATER.map(), Items.BOWL, new ItemStack(SItems.WATER_BOWL), SoundEvents.BOTTLE_FILL);
		addToCauldron(WATER.map(), SItems.WATER_BOWL, new ItemStack(Items.BOWL), SoundEvents.BOTTLE_EMPTY);
		fillEmptyCauldron(SItems.WATER_BOWL, new ItemStack(Items.BOWL), Blocks.WATER_CAULDRON.defaultBlockState(), SoundEvents.BOTTLE_EMPTY);
		//Purified Water Bowl Interactions
		takeFromCauldron(PURIFIED_WATER.map(), Items.BOWL, new ItemStack(SItems.PURIFIED_WATER_BOWL), SoundEvents.BOTTLE_FILL);
		addToCauldron(PURIFIED_WATER.map(), SItems.PURIFIED_WATER_BOWL, new ItemStack(Items.BOWL), SoundEvents.BOTTLE_EMPTY);
		fillEmptyCauldron(SItems.PURIFIED_WATER_BOWL, new ItemStack(Items.BOWL), SBlocks.PURIFIED_WATER_CAULDRON.defaultBlockState(), SoundEvents.BOTTLE_EMPTY);
		//Glass bottle Interactions
		takeFromCauldron(PURIFIED_WATER.map(), Items.GLASS_BOTTLE, PotionContents.createItemStack(Items.POTION, SPotions.PURIFIED_WATER.holder()), SoundEvents.BOTTLE_FILL);
		PURIFIED_WATER.map().put(Items.POTION, (p_175704_, p_175705_, p_175706_, p_175707_, p_175708_, p_175709_) -> {
			PotionContents potioncontents = p_175709_.get(DataComponents.POTION_CONTENTS); 
			if (p_175704_.getValue(LayeredCauldronBlock.LEVEL) != 3 && potioncontents.is(SPotions.PURIFIED_WATER.holder())) {
	            if (!p_175705_.isClientSide) {
	               p_175707_.setItemInHand(p_175708_, ItemUtils.createFilledResult(p_175709_, p_175707_, new ItemStack(Items.GLASS_BOTTLE)));
	               p_175707_.awardStat(Stats.USE_CAULDRON);
	               p_175707_.awardStat(Stats.ITEM_USED.get(p_175709_.getItem()));
	               p_175705_.setBlockAndUpdate(p_175706_, p_175704_.cycle(LayeredCauldronBlock.LEVEL));
	               p_175705_.playSound((Player)null, p_175706_, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
	               p_175705_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_175706_);
	            }

	            return ItemInteractionResult.sidedSuccess(p_175705_.isClientSide);
	         } else {
	            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	         }
	      });
		//This will most likely override the default potion action. Anybody messing with this will cause this mod's to not work
		EMPTY.map().put(Items.POTION, (blockstate, level, pos, player, interactionHand, p_175737_) -> {
			PotionContents potioncontents = p_175737_.get(DataComponents.POTION_CONTENTS);
			if (potioncontents == null || !potioncontents.is(Potions.WATER) || !potioncontents.is(SPotions.PURIFIED_WATER.holder())) {
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			} else {
				if (!level.isClientSide) {
					Item item = p_175737_.getItem();
					player.setItemInHand(interactionHand, ItemUtils.createFilledResult(p_175737_, player, new ItemStack(Items.GLASS_BOTTLE)));
					player.awardStat(Stats.USE_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(item));
					if (potioncontents.is(Potions.WATER))
						level.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState());
					else
						level.setBlockAndUpdate(pos, SBlocks.PURIFIED_WATER_CAULDRON.defaultBlockState());
					level.playSound((Player)null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
				}

				return ItemInteractionResult.sidedSuccess(level.isClientSide);
			}
		});

		//Canteen Interactions
		WATER.map().put(SItems.CANTEEN, (blockstate, level, pos, player, interactionHand, p_175723_) -> {
			if (!level.isClientSide) {
				Item item = p_175723_.getItem();
				player.setItemInHand(interactionHand, ItemUtils.createFilledResult(p_175723_, player, CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), blockstate.getValue(LayeredCauldronBlock.LEVEL), Potions.WATER)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
				level.playSound((Player)null, pos, SoundEvents.BOTTLE_FILL/*TODO: Setup canteen fill sound*/, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, pos);
			}

			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		});

		PURIFIED_WATER.map().put(SItems.CANTEEN, (blockstate, level, pos, player, interactionHand, p_175723_) -> {
			if (!level.isClientSide) {
				PotionContents potioncontents = p_175723_.get(DataComponents.POTION_CONTENTS);
				Item item = p_175723_.getItem();
				player.setItemInHand(interactionHand, ItemUtils.createFilledResult(p_175723_, player, CanteenItem.addToCanteen(new ItemStack(SItems.FILLED_CANTEEN), blockstate.getValue(LayeredCauldronBlock.LEVEL), SPotions.PURIFIED_WATER.holder())));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
				level.playSound((Player)null, pos, SoundEvents.BOTTLE_FILL/*TODO: Setup canteen fill sound*/, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, pos);
			}

			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		});

		EMPTY.map().put(SItems.FILLED_CANTEEN, (blockstate, level, pos, player, interactionHand, p_175737_) -> {
			PotionContents potioncontents = p_175737_.get(DataComponents.POTION_CONTENTS);
			if (potioncontents == null || !potioncontents.is(Potions.WATER) || !potioncontents.is(SPotions.PURIFIED_WATER.holder())) {
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			} else {
				if (!level.isClientSide) {
					CanteenItem item = (CanteenItem) p_175737_.getItem();
					int drinksLeft = SDataComponents.DRINKS_LEFT_D.getData(p_175737_);
					if (drinksLeft > 3) {
						player.setItemInHand(interactionHand, ItemUtils.createFilledResult(p_175737_, player, CanteenItem.addToCanteen(p_175737_.copy(), drinksLeft - 3, potioncontents)));
					} else {
						player.setItemInHand(interactionHand, ItemUtils.createFilledResult(p_175737_, player, new ItemStack(SItems.CANTEEN)));
					}
					player.awardStat(Stats.USE_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(item));
					if (potioncontents.is(Potions.WATER))
						level.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, Math.min(drinksLeft, 3)));
					else
						level.setBlockAndUpdate(pos, SBlocks.PURIFIED_WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, Math.min(drinksLeft, 3)));
					level.playSound((Player)null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
				}

				return ItemInteractionResult.sidedSuccess(level.isClientSide);
			}
		});
	}

	public static void takeFromCauldron(Map<Item, CauldronInteraction> interaction, Item requiredItem, ItemStack output, SoundEvent fillSound){
		interaction.put(requiredItem, (blockstate, level, pos, player, interactionHand, p_175723_) -> {
			if (!level.isClientSide) {
				Item item = p_175723_.getItem();
				player.setItemInHand(interactionHand, ItemUtils.createFilledResult(p_175723_, player, output.copy()));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				LayeredCauldronBlock.lowerFillLevel(blockstate, level, pos);
				level.playSound((Player)null, pos, fillSound/*TODO: Setup bowl fill sound*/, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, pos);
			}

			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		});
	}

	public static void addToCauldron(Map<Item, CauldronInteraction> interaction, Item requiredItem, ItemStack output, SoundEvent emptySound){
		interaction.put(requiredItem, (blockstate, level, pos, player, interactionHand, p_175709_) -> {
			if (blockstate.getValue(LayeredCauldronBlock.LEVEL) != 3) {
				if (!level.isClientSide) {
					player.setItemInHand(interactionHand, ItemUtils.createFilledResult(p_175709_, player, output.copy()));
					player.awardStat(Stats.USE_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(p_175709_.getItem()));
					level.setBlockAndUpdate(pos, blockstate.cycle(LayeredCauldronBlock.LEVEL));
					level.playSound((Player)null, pos, emptySound/*TODO: Setup bowl empty sound*/, SoundSource.BLOCKS, 1.0F, 1.0F);
					level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
				}

				return ItemInteractionResult.sidedSuccess(level.isClientSide);
			} else {
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
		});
	}

	public static void fillEmptyCauldron(Item requiredItem, ItemStack output, BlockState resultingCauldron, SoundEvent emptySound) {
		EMPTY.map().put(requiredItem, (blockstate, level, pos, player, interactionHand, p_175737_) -> {
			if (!level.isClientSide) {
				Item item = p_175737_.getItem();
				player.setItemInHand(interactionHand, ItemUtils.createFilledResult(p_175737_, player, output.copy()));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				level.setBlockAndUpdate(pos, resultingCauldron);
				level.playSound((Player)null, pos, emptySound, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
			}

			return ItemInteractionResult.sidedSuccess(level.isClientSide);
		});
	}
}
