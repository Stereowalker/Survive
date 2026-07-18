package com.stereowalker.survive.world.level.block.entity;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.stereowalker.survive.Survive;
import com.stereowalker.survive.world.level.block.RealisticCampfireBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class RealisticCampfireBlockEntity extends BlockEntity implements Clearable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int BURN_COOL_SPEED = 2;
    private static final int NUM_SLOTS = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[4];
    private final int[] cookingTime = new int[4];
    private int fuelTime = 0;
    private final RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    public RealisticCampfireBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SBlockEntityType.REALISIC_CAMPFIRE, pPos, pBlockState);
    }

    public static void cookTick(ServerLevel pLevel, BlockPos pPos, BlockState pState, RealisticCampfireBlockEntity pBlockEntity, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> recipeCache) {
        boolean flag = false;

        for (int i = 0; i < pBlockEntity.items.size(); i++) {
            ItemStack itemstack = pBlockEntity.items.get(i);
            if (!itemstack.isEmpty()) {
                flag = true;
                pBlockEntity.cookingProgress[i]++;
                if (pBlockEntity.cookingProgress[i] >= pBlockEntity.cookingTime[i]) {
                    SingleRecipeInput input = new SingleRecipeInput(itemstack);
                    ItemStack result = recipeCache.getRecipeFor(input, pLevel).map(r -> r.value().assemble(input)).orElse(itemstack);
                    if (result.isItemEnabled(pLevel.enabledFeatures())) {
                        Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), result);
                        pBlockEntity.items.set(i, ItemStack.EMPTY);
                        pLevel.sendBlockUpdated(pPos, pState, pState, 3);
                        pLevel.gameEvent(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(pState));
                    }
                }
            }
        }

        ///
        flag = true;
        pBlockEntity.fuelTime--;
        if (pBlockEntity.fuelTime <= 0 || pLevel.isRainingAt(pPos.above()) || !pState.getValue(RealisticCampfireBlock.LIT)) {
        	pLevel.setBlock(pPos, pState.setValue(RealisticCampfireBlock.LIT, false).setValue(RealisticCampfireBlock.HEAT, 0), 3);
        }
        else {
        	if (pBlockEntity.fuelTime >= 750 * 3)
        		pLevel.setBlock(pPos, pState.setValue(RealisticCampfireBlock.HEAT, 4), 3);
        	else if (pBlockEntity.fuelTime >= 750 * 2)
        		pLevel.setBlock(pPos, pState.setValue(RealisticCampfireBlock.HEAT, 3), 3);
        	else if (pBlockEntity.fuelTime >= 750)
        		pLevel.setBlock(pPos, pState.setValue(RealisticCampfireBlock.HEAT, 2), 3);
        	else if (pBlockEntity.fuelTime >= 0)
        		pLevel.setBlock(pPos, pState.setValue(RealisticCampfireBlock.HEAT, 1), 3);
        }
        
        if (flag) {
            setChanged(pLevel, pPos, pState);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level != null) {
            Containers.dropContents(this.level, pos, this.getItems());
        }
    }

    public static void cooldownTick(Level pLevel, BlockPos pPos, BlockState pState, RealisticCampfireBlockEntity pBlockEntity) {
        boolean flag = false;
        ///
        if (pBlockEntity.fuelTime <= 0) {
        	pLevel.setBlock(pPos, pState.setValue(RealisticCampfireBlock.LIT, false).setValue(RealisticCampfireBlock.HEAT, 0), 3);
        }
        else if (pState.getValue(RealisticCampfireBlock.LIT) && pBlockEntity.fuelTime > 0 && !pLevel.isRainingAt(pPos)) {
        	pLevel.setBlock(pPos, pState.setValue(RealisticCampfireBlock.HEAT, Mth.ceil(pBlockEntity.fuelTime / 1000f)), 3);
        }
        ///

        for (int i = 0; i < pBlockEntity.items.size(); i++) {
            if (pBlockEntity.cookingProgress[i] > 0) {
                flag = true;
                pBlockEntity.cookingProgress[i] = Mth.clamp(pBlockEntity.cookingProgress[i] - 2, 0, pBlockEntity.cookingTime[i]);
            }
        }

        if (flag) {
            setChanged(pLevel, pPos, pState);
        }
    }

    public static void particleTick(Level pLevel, BlockPos pPos, BlockState pState, RealisticCampfireBlockEntity pBlockEntity) {
        RandomSource randomsource = pLevel.getRandom();
        if (randomsource.nextFloat() < 0.11F) {
            for (int i = 0; i < randomsource.nextInt(2) + 2; i++) {
                RealisticCampfireBlock.makeParticles(pLevel, pPos, pState.getValue(RealisticCampfireBlock.SIGNAL_FIRE), false);
            }
        }

        int l = pState.getValue(RealisticCampfireBlock.FACING).get2DDataValue();

        for (int j = 0; j < pBlockEntity.items.size(); j++) {
            if (!pBlockEntity.items.get(j).isEmpty() && randomsource.nextFloat() < 0.2F) {
                Direction direction = Direction.from2DDataValue(Math.floorMod(j + l, 4));
                float f = 0.3125F;
                double d0 = (double)pPos.getX()
                    + 0.5
                    - (double)((float)direction.getStepX() * 0.3125F)
                    + (double)((float)direction.getClockWise().getStepX() * 0.3125F);
                double d1 = (double)pPos.getY() + 0.5;
                double d2 = (double)pPos.getZ()
                    + 0.5
                    - (double)((float)direction.getStepZ() * 0.3125F)
                    + (double)((float)direction.getClockWise().getStepZ() * 0.3125F);

                for (int k = 0; k < 4; k++) {
                    pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 5.0E-4, 0.0);
                }
            }
        }
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items.clear();
        ContainerHelper.loadAllItems(input, this.items);
        input.getIntArray("CookingTimes")
            .ifPresentOrElse(
                cookingTimes -> System.arraycopy(cookingTimes, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, cookingTimes.length)),
                () -> Arrays.fill(this.cookingProgress, 0)
            );
        input.getIntArray("CookingTotalTimes")
            .ifPresentOrElse(
                cookingTimes -> System.arraycopy(cookingTimes, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, cookingTimes.length)),
                () -> Arrays.fill(this.cookingTime, 0)
            );
        fuelTime = input.getIntOr("fuelTime", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items, true);
        output.putIntArray("CookingTimes", this.cookingProgress);
        output.putIntArray("CookingTotalTimes", this.cookingTime);
        output.putInt("fuelTime", fuelTime);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_registries) {
        CompoundTag var4;
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, p_registries);
            ContainerHelper.saveAllItems(output, this.items, true);
            var4 = output.buildResult();
        }

        return var4;
    }

    public Optional<RecipeHolder<CampfireCookingRecipe>> getCookableRecipe(ItemStack pStack) {
        return this.items.stream().noneMatch(ItemStack::isEmpty) && this.level instanceof ServerLevel
            ? Optional.empty()
            : this.quickCheck.getRecipeFor(new SingleRecipeInput(pStack), (ServerLevel) this.level);
    }

    public boolean placeFood(ServerLevel serverLevel, @Nullable LivingEntity sourceEntity, ItemStack placeItem) {
    	for (int slot = 0; slot < this.items.size(); slot++) {
            ItemStack item = this.items.get(slot);
            Survive.getInstance().getLogger().info("Please Work "+ slot+" "+item);
            if (item.isEmpty()) {
                Optional<RecipeHolder<CampfireCookingRecipe>> recipe = serverLevel.recipeAccess()
                    .getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SingleRecipeInput(placeItem), serverLevel);
                if (recipe.isEmpty()) {
                    return false;
                }

                this.cookingTime[slot] = recipe.get().value().cookingTime();
                this.cookingProgress[slot] = 0;
                this.items.set(slot, placeItem.consumeAndReturn(1, sourceEntity));
                serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(sourceEntity, this.getBlockState()));
                this.markUpdated();
                return true;
            }
        }

        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void dowse() {
        if (this.level != null) {
            this.markUpdated();
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getItems());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder p_components) {
        super.collectImplicitComponents(p_components);
        p_components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard("Items");
    }
    
    
    ///


    public boolean placeFuel(@Nullable LivingEntity pEntity, ItemStack pFood) {
        int fuelAdded = pEntity.level().fuelValues().burnDuration(pFood);
        if (pFood.getItem() != Items.LAVA_BUCKET && this.fuelTime + fuelAdded <= 3000) {
        	this.fuelTime += fuelAdded;
        	pFood.shrink(1);
        	this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(pEntity, this.getBlockState()));
            this.markUpdated();
        	return true;
        }

        return false;
    }
}