package com.stereowalker.survive.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.survive.hooks.ColdStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin extends RandomizableContainerBlockEntity implements ColdStorage {
	protected BarrelBlockEntityMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}
	float coldness;
	long lastAccessed;

	@Inject(method = "load", at = @At("TAIL"))
	private void loadAdditional(CompoundTag pTag/*, HolderLookup.Provider pRegistries*/, CallbackInfo ci) {
		load2(pTag/*, pRegistries*/);
	}

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	private void saveAdditional(CompoundTag pTag/*, HolderLookup.Provider pRegistries*/, CallbackInfo ci) {
		save(pTag/*, pRegistries*/);
	}

	@Override
	public float coldness() { return coldness; }
	@Override
	public void setColdness(float value) { coldness = value; }
	@Override
	public long lastAccessed() { return lastAccessed; }
	@Override
	public void setLastAccessed(long value) { lastAccessed = value; }
	@Override
	public float lossFactor() { return .08f; }
	@Override
	public ItemStack get(int i) { return getItem(i);}
	@Override
	public void set(int i, ItemStack stack) { setItem(i, stack);}
	@Override
	public int slotCount() { return getContainerSize(); }
	@Override
	public float maxColdness() {return 1800;}

}
