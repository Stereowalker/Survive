package com.stereowalker.survive.world.level.block.entity;

import com.stereowalker.survive.world.inventory.SaltBoxMenu;
import com.stereowalker.survive.world.item.SItems;
import com.stereowalker.survive.world.item.component.SDataComponents;
import com.stereowalker.survive.world.level.block.SaltBoxBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SaltBoxBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level p_155062_, BlockPos p_155063_, BlockState p_155064_) {
            SaltBoxBlockEntity.this.playSound(p_155064_, SoundEvents.BARREL_OPEN);
            SaltBoxBlockEntity.this.updateBlockState(p_155064_, true);
        }

        @Override
        protected void onClose(Level p_155072_, BlockPos p_155073_, BlockState p_155074_) {
            SaltBoxBlockEntity.this.playSound(p_155074_, SoundEvents.BARREL_CLOSE);
            SaltBoxBlockEntity.this.updateBlockState(p_155074_, false);
        }

        @Override
        protected void openerCountChanged(Level p_155066_, BlockPos p_155067_, BlockState p_155068_, int p_155069_, int p_155070_) {
        }

        @Override
        protected boolean isOwnContainer(Player p_155060_) {
            if (p_155060_.containerMenu instanceof SaltBoxMenu) {
                Container container = ((SaltBoxMenu)p_155060_.containerMenu).getContainer();
                return container == SaltBoxBlockEntity.this;
            } else {
                return false;
            }
        }
    };

    public SaltBoxBlockEntity(BlockPos pos, BlockState blockState) {
        super(SBlockEntityType.SALT_BOX, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items);
        }
        tag.putLong("lastGameTickUpdated", lastGameTickUpdated);
        for (int i = 0; i < 27; i++) {
            tag.putLong("sludge"+i, saltSludge[i]);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items);
        }
        lastGameTickUpdated = tag.getLong("lastGameTickUpdated");
        for (int i = 0; i < 27; i++) {
        	saltSludge[i] = tag.getInt("sludge"+i);
        }
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.barrel");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new SaltBoxMenu(id, player, this, data());
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    void updateBlockState(BlockState state, boolean open) {
        this.level.setBlock(this.getBlockPos(), state.setValue(SaltBoxBlock.OPEN, Boolean.valueOf(open)), 3);
    }

    void playSound(BlockState state, SoundEvent sound) {
        Vec3i vec3i = state.getValue(SaltBoxBlock.FACING).getNormal();
        double d0 = (double)this.worldPosition.getX() + 0.5 + (double)vec3i.getX() / 2.0;
        double d1 = (double)this.worldPosition.getY() + 0.5 + (double)vec3i.getY() / 2.0;
        double d2 = (double)this.worldPosition.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
        this.level.playSound(null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }
    
    ///
    public ContainerData data() {
		return new ContainerData() {
			@Override
			public void set(int pIndex, int pValue) {
				saltSludge[pIndex] = pValue;
			}
			
			@Override
			public int getCount() { return saltSludge.length; }
			
			@Override
			public int get(int pIndex) {
				return saltSludge[pIndex];
			}
		};
	}

    long lastGameTickUpdated;
    int[] saltSludge = new int[27];
    public void massivelyExtendLifespan() {
    	for (int i = 0; i < items.size(); i++) {
    		ItemStack stack = items.get(i);
    		if (stack.getItem() == SItems.SEA_SALT && saltSludge[i] <= 0) {
    			saltSludge[i] = 1000;
    			stack.shrink(1);
    		}
    	}
    	
    	long gameTime = level.getGameTime();
    	if (lastGameTickUpdated == 0) lastGameTickUpdated = gameTime;
    	long timeSinceLastOpened = gameTime - lastGameTickUpdated;
    	lastGameTickUpdated = gameTime;
    	for (int i = 0; i < items.size(); i++) {
    		ItemStack stack = items.get(i);
    		if (SDataComponents.FOOD_STATUS_D.hasData(stack)) {
        		int sidesCovered = 0;
        		for(int n : get8Neighbors(i)) {
        			if (n >= 0 && !SDataComponents.FOOD_STATUS_D.hasData(items.get(n)) && saltSludge[n] > 0) {
        				sidesCovered++;
        				saltSludge[n]--;
        			}
        		}
        		if (sidesCovered > 0) {
        			float perc = (0.0001f * sidesCovered) + 0.9991f;
                	float efficiency = timeSinceLastOpened * perc;
                	if (timeSinceLastOpened <= 2) efficiency = 1;
        			
        			final float eff = efficiency;
        			final float salt = timeSinceLastOpened * ((sidesCovered / 8f) / 96f);
        			SDataComponents.FOOD_STATUS_D.editData(stack, food_status -> food_status.extendTime(eff).increaseSalt(salt / 8f));
        		}
    		}
    	}
    }
    
    @Override
    public void setItem(int pIndex, ItemStack pStack) {
    	System.out.println("Index "+pIndex);
    	int[] saltSlots = new int[] {0, 8};
    	
    	super.setItem(pIndex, pStack);
    }
    
    private static final int COLS = 9;
    private static final int ROWS = 3;

    private static final int[][] OFFSETS = {{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}};
    
    public static int[] get8Neighbors(int index) {
    	int[] neighbors = new int[8];
    	if (index >= 0 && index < COLS * ROWS) {
    		int row = index / COLS;
    		int col = index % COLS;
    		for (int i = 0; i < OFFSETS.length; i++) {
    			int dx = OFFSETS[i][0];
    			int dy = OFFSETS[i][1];
    			int nr = row + dy;
    			int nc = col + dx;
    			if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS) {
    				neighbors[i] = -1;
    			} else {
    				neighbors[i] = nr * COLS + nc;
    			}
    		}
    	}
    	return neighbors;
    }
}
