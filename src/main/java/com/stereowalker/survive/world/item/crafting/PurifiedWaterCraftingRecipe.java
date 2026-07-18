package com.stereowalker.survive.world.item.crafting;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stereowalker.survive.world.item.alchemy.SPotions;
import com.stereowalker.unionlib.util.LoaderHelper;
import com.stereowalker.unionlib.world.item.crafting.NoRemainderShaplessRecipe;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PurifiedWaterCraftingRecipe extends NoRemainderShaplessRecipe {

	public PurifiedWaterCraftingRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients) {
		super(commonInfo, bookInfo, result, ingredients);
	}

	@Override
	public RecipeSerializer<PurifiedWaterCraftingRecipe> getSerializer() {
		return SRecipeSerializer.CRAFTING_SHAPELESS_WITH_PURIFIED_WATER;
	}
	
	public static final MapCodec<PurifiedWaterCraftingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i
			.group(Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
					CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
					ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result),
					Ingredient.CODEC.listOf(1, 8).fieldOf("ingredients").forGetter(o -> Lists.asList(LoaderHelper.createStackIngredient(PotionContents.createItemStack(Items.POTION, SPotions.PURIFIED_WATER.holder())), o.ingredients.toArray(new Ingredient[0]))))
			.apply((Applicative) i, PurifiedWaterCraftingRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, PurifiedWaterCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
			(StreamCodec) Recipe.CommonInfo.STREAM_CODEC, o -> o.commonInfo,
			(StreamCodec) CraftingRecipe.CraftingBookInfo.STREAM_CODEC, o -> o.bookInfo,
			(StreamCodec) ItemStackTemplate.STREAM_CODEC, o -> o.result,
			(StreamCodec) Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), o -> Lists.asList(LoaderHelper.createStackIngredient(PotionContents.createItemStack(Items.POTION, SPotions.PURIFIED_WATER.holder())), o.ingredients.toArray(new Ingredient[0])),
			PurifiedWaterCraftingRecipe::new);

}
