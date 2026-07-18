package com.stereowalker.survive.world.level.block.entity;

import com.stereowalker.survive.world.item.CanteenItem;
import com.stereowalker.survive.world.item.HygieneItems;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.survive.world.level.block.DryingCauldronBlock;
import com.stereowalker.survive.world.level.block.DryingCauldronBlock.FluidToDry;
import com.stereowalker.survive.world.level.block.RealisticCampfireBlock;
import com.stereowalker.survive.world.level.block.SBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class DryingCauldronBlockEntity extends BlockEntity {

	ItemStack result;
	int resultCount;
	int waterContentLeft;

	public DryingCauldronBlockEntity(BlockPos pos, BlockState blockState) {
		super(SBlockEntityType.DRYING_CAULDRON, pos, blockState);
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("waterContentLeft", waterContentLeft);
		tag.putInt("resultCount", resultCount);
		tag.put("result", getResult().save(new CompoundTag()));
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		waterContentLeft = tag.getInt("waterContentLeft");
		resultCount = tag.getInt("resultCount");
		result = ItemStack.of((CompoundTag) tag.get("result"));
	}
	
	public static int calculateFromTicks(int ticks, int level) {
		return Mth.ceil(ticks * 300f * (level/3f)); //300 Is the average heat value...
	}
	
	public ItemStack getResult() {
		return result == null ? ItemStack.EMPTY : result.copy();
	}

	public static boolean setResult(Level level, ItemStack item, BlockPos pos, Player player, InteractionHand hand) {
		BlockState state = level.getBlockState(pos);
		if (item.getItem() == HygieneItems.POTASH_SOLUTION) {
			if (!(level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity)) {
				if (!level.isClientSide) {
					player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, new ItemStack(Items.GLASS_BOTTLE)));
					player.awardStat(Stats.FILL_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(item.getItem()));
					updateItem(level, pos, SoundEvents.BOTTLE_EMPTY);
					level.setBlockAndUpdate(pos, SBlocks.DRYING_CAULDRON.defaultBlockState());
				}
				if (level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity dbe) {
					dbe.result = new ItemStack(HygieneItems.POTASH);
					dbe.resultCount = 1;
					dbe.waterContentLeft = calculateFromTicks(24000*3, 1);
					return true;
				}
			}
			else if (level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity dbe && dbe.result.getItem() == HygieneItems.POTASH && state.getValue(LayeredCauldronBlock.LEVEL) < 3) {
				if (!level.isClientSide) {
					player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, new ItemStack(Items.GLASS_BOTTLE)));
					player.awardStat(Stats.USE_CAULDRON);
					player.awardStat(Stats.ITEM_USED.get(item.getItem()));
					level.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
					updateItem(level, pos, SoundEvents.BOTTLE_EMPTY);
				}
				dbe.resultCount += 1;
				dbe.waterContentLeft += calculateFromTicks(24000*3, 1);
				return true;
			}
		}
		else if (SDataComponents.BIOME_SOURCE_D.hasData(item) && level.registryAccess()
				.lookup(Registries.BIOME)
				.get().get(ResourceKey.create(Registries.BIOME, SDataComponents.BIOME_SOURCE_D.getData(item)))
				.get().is(BiomeTags.IS_OCEAN)) {
			if (item.getItem() == Items.WATER_BUCKET) {
				if (!(level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity)) {
					if (!level.isClientSide) {
						player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, new ItemStack(Items.BUCKET)));
						player.awardStat(Stats.FILL_CAULDRON);
						player.awardStat(Stats.ITEM_USED.get(item.getItem()));
						updateItem(level, pos, SoundEvents.BUCKET_EMPTY);
						level.setBlockAndUpdate(pos, SBlocks.DRYING_CAULDRON.defaultBlockState().setValue(DryingCauldronBlock.LEVEL, Integer.valueOf(3)));
					}
					if (level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity dbe) {
						dbe.result = new ItemStack(SItems.SEA_SALT);
						dbe.resultCount = 144;
						dbe.waterContentLeft = calculateFromTicks(24000*8, 3);
						dbe.markUpdated();
						return true;
					}
				}
			} else if (item.getItem() == Items.POTION && PotionUtils.getPotion(item) == Potions.WATER) {
				if (!(level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity)) {
					if (!level.isClientSide) {
						player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, new ItemStack(Items.GLASS_BOTTLE)));
						player.awardStat(Stats.FILL_CAULDRON);
						player.awardStat(Stats.ITEM_USED.get(item.getItem()));
						updateItem(level, pos, SoundEvents.BOTTLE_EMPTY);
						level.setBlockAndUpdate(pos, SBlocks.DRYING_CAULDRON.defaultBlockState());
					}
					if (level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity dbe) {
						dbe.result = new ItemStack(SItems.SEA_SALT);
						dbe.resultCount = 48;
						dbe.waterContentLeft = 19200000;
						dbe.markUpdated();
						return true;
					}
					else System.out.println("NO DBE");
				}
				else if (level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity dbe && dbe.result.getItem() == SItems.SEA_SALT && state.getValue(LayeredCauldronBlock.LEVEL) < 3) {
					if (!level.isClientSide) {
						player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, new ItemStack(Items.GLASS_BOTTLE)));
						player.awardStat(Stats.USE_CAULDRON);
						player.awardStat(Stats.ITEM_USED.get(item.getItem()));
						level.setBlockAndUpdate(pos, state.cycle(LayeredCauldronBlock.LEVEL));
						updateItem(level, pos, SoundEvents.BOTTLE_EMPTY);
					}
					dbe.resultCount += 48;
					dbe.waterContentLeft += 19200000;
					dbe.markUpdated();
					return true;
				}
			} else if ((item.getItem() == SItems.FILLED_CANTEEN || item.getItem() == SItems.NETHERITE_CANTEEN) && PotionUtils.getPotion(item) == Potions.WATER) {
				if (!(level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity)) {
					if (!level.isClientSide) {

						int drinksLeft = SDataComponents.DRINKS_LEFT_D.getData(item);
						if (drinksLeft > 3) {
							player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, CanteenItem.addToCanteen(item.copy(), drinksLeft - 3, PotionUtils.getPotion(item))));
						} else {
							player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, new ItemStack(item.getItem() == SItems.FILLED_NETHERITE_CANTEEN ? SItems.NETHERITE_CANTEEN : SItems.CANTEEN)));
						}

						player.awardStat(Stats.FILL_CAULDRON);
						player.awardStat(Stats.ITEM_USED.get(item.getItem()));
						updateItem(level, pos, SoundEvents.BOTTLE_EMPTY);
						level.setBlockAndUpdate(pos, SBlocks.DRYING_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, Math.min(drinksLeft, 3)));
					}
					if (level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity dbe) {
						dbe.result = new ItemStack(SItems.SEA_SALT);
						dbe.resultCount = 48;
						dbe.waterContentLeft = 19200000;
						dbe.markUpdated();
						return true;
					}
					else System.out.println("NO DBE");
				}
				else if (level.getBlockEntity(pos) instanceof DryingCauldronBlockEntity dbe && dbe.result.getItem() == SItems.SEA_SALT && state.getValue(LayeredCauldronBlock.LEVEL) < 3) {
					if (!level.isClientSide) {
						int cauldronLevelToFill = 3 - state.getValue(LayeredCauldronBlock.LEVEL);
						int drinksLeft = SDataComponents.DRINKS_LEFT_D.getData(item);
						if (drinksLeft > cauldronLevelToFill) {
							player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, CanteenItem.addToCanteen(item.copy(), drinksLeft - cauldronLevelToFill, PotionUtils.getPotion(item))));
						} else {
							player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, new ItemStack(item.getItem() == SItems.FILLED_NETHERITE_CANTEEN ? SItems.NETHERITE_CANTEEN : SItems.CANTEEN)));
						}

						player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, new ItemStack(Items.GLASS_BOTTLE)));
						player.awardStat(Stats.USE_CAULDRON);
						player.awardStat(Stats.ITEM_USED.get(item.getItem()));
						level.setBlockAndUpdate(pos, SBlocks.DRYING_CAULDRON.defaultBlockState().setValue(DryingCauldronBlock.LEVEL, Integer.valueOf(3)));
						updateItem(level, pos, SoundEvents.BOTTLE_EMPTY);
					}
					dbe.resultCount += 48;
					dbe.waterContentLeft += 19200000;
					dbe.markUpdated();
					return true;
				}
			}
		}
		return false;
	}
	
	private static void updateItem(Level level, BlockPos pos, SoundEvent sound) {
		level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
	}

	public void tick(ServerLevel level, BlockState state) {
		BlockState initialState = state;
		
		if (result.getItem() == SItems.SEA_SALT) {
			state = state.setValue(DryingCauldronBlock.FLUID, FluidToDry.SEA_SALT);
		}
		else if (result.getItem() == HygieneItems.POTASH) {
			state = state.setValue(DryingCauldronBlock.FLUID, FluidToDry.POTASH);
		}
		
		int sunPower = 0;
		int camfirePower = 0;
		BlockPos above = this.worldPosition.above();
		boolean flag = level.isRainingAt(above);
		if (level.isDay() && !level.isClientSide) {
			float f = this.getBrightness(level, above);
			if (!flag && level.canSeeSky(above)) {
				sunPower = Mth.ceil(f * (2f/3f)); //Maxes out at 10
			}
		}
		if (flag) sunPower -= 20;
		BlockState blockBelow = level.getBlockState(worldPosition.below());
		if (blockBelow.hasProperty(CampfireBlock.LIT) && blockBelow.getValue(CampfireBlock.LIT)) {
			if (blockBelow.getBlock() == Blocks.CAMPFIRE) {
				camfirePower = 50;
			}
			else if (blockBelow.getBlock() == Blocks.SOUL_CAMPFIRE) {
				camfirePower = 70;
			}
			else if (blockBelow.getBlock() == SBlocks.REALISTIC_CAMPFIRE) {
				camfirePower = blockBelow.getValue(RealisticCampfireBlock.HEAT) * 75; //Maxes at 300;
			}
		}
		int combinedPower = sunPower + camfirePower;
		if (combinedPower > 0) {
			waterContentLeft -= combinedPower;
			markUpdated();
		}
		state = state.setValue(DryingCauldronBlock.BOILING, Mth.clamp(Mth.ceil((float)combinedPower / 31f), 0, 10));

		if (waterContentLeft <= 0) {
			result.setCount(resultCount);
			level.addFreshEntity(new ItemEntity(level, this.worldPosition.getX()+0.5, this.worldPosition.getY()+0.5, this.worldPosition.getZ()+0.5, result.copyAndClear(), 0, 0, 0));
			level.setBlockAndUpdate(this.worldPosition, Blocks.CAULDRON.defaultBlockState());
		} else if (result.getItem() == SItems.SEA_SALT) {
			if (waterContentLeft <= calculateFromTicks(24000*8, 2) && state.getValue(DryingCauldronBlock.LEVEL) == 3) {
				state = state.setValue(DryingCauldronBlock.LEVEL, 2);
			} else if (waterContentLeft <= calculateFromTicks(24000*8, 1) && state.getValue(DryingCauldronBlock.LEVEL) == 2) {
				state = state.setValue(DryingCauldronBlock.LEVEL, 1);
			}
		}
		
		if (initialState != state) {
			level.setBlock(this.worldPosition, state, 3);
			setChanged(level, this.worldPosition, state);
			markUpdated();
		}
	}

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

	public float getBrightness(ServerLevel level, BlockPos pos) {
		return level.hasChunkAt(pos) ? level.getBrightness(LightLayer.SKY, pos) : 0.0F;
	}
}
