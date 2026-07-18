package com.stereowalker.survive.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.unionlib.util.LoaderHelper;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

public class WaterBottleSmeltingRecipe extends SmeltingRecipe {
	public static final MapCodec<SmeltingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                    AbstractCookingRecipe.CookingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
                    Ingredient.CODEC.fieldOf("ingredient").forGetter((_) -> LoaderHelper.createStackIngredient(PotionContents.createItemStack(Items.POTION, Potions.WATER))),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter((_) -> ItemStackTemplate.fromNonEmptyStack(PotionContents.createItemStack(Items.POTION, SPotions.PURIFIED_WATER.holder()))),
                    Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(AbstractCookingRecipe::experience),
                    Codec.INT.fieldOf("cookingtime").orElse(200).forGetter(AbstractCookingRecipe::cookingTime)
                )
                .apply(i, SmeltingRecipe::new)
        );
    public static final StreamCodec<RegistryFriendlyByteBuf, SmeltingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC,
            o -> o.commonInfo,
            AbstractCookingRecipe.CookingBookInfo.STREAM_CODEC,
            o -> o.bookInfo,
            Ingredient.CONTENTS_STREAM_CODEC,
            (_) -> LoaderHelper.createStackIngredient(PotionContents.createItemStack(Items.POTION, Potions.WATER)),
            ItemStackTemplate.STREAM_CODEC,
            (_) -> ItemStackTemplate.fromNonEmptyStack(PotionContents.createItemStack(Items.POTION, SPotions.PURIFIED_WATER.holder())),
            ByteBufCodecs.FLOAT,
            AbstractCookingRecipe::experience,
            ByteBufCodecs.INT,
            AbstractCookingRecipe::cookingTime,
            SmeltingRecipe::new
        );
	public WaterBottleSmeltingRecipe(Recipe.CommonInfo commonInfo, AbstractCookingRecipe.CookingBookInfo bookInfo, Ingredient ingredient, ItemStackTemplate result, float experience, int cookingTime) {
		super(commonInfo, bookInfo, ingredient, result, experience, cookingTime);
	}
	
//	@Override
//	public boolean matches(SingleRecipeInput pInv, Level pLevel) {
//		if (pInv.getItem(0).has(DataComponents.POTION_CONTENTS) && pInv.getItem(0).get(DataComponents.POTION_CONTENTS).potion().get() == Potions.WATER) return this.ingredient.test(pInv.getItem(0)); else return false;
//	}

	@Override
	public RecipeSerializer<SmeltingRecipe> getSerializer() {
		return SRecipeSerializer.PURIFIED_WATER_BOTTLE;
	}

}
