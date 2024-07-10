package com.stereowalker.survive.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stereowalker.survive.world.item.alchemy.SPotions;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PurifiedWaterCraftingSerializer implements RecipeSerializer<PurifiedWaterCraftingRecipe> {

	private static PurifiedWaterCraftingRecipe fromNetwork(RegistryFriendlyByteBuf p_335962_) {
		String s = p_335962_.readUtf();
		CraftingBookCategory craftingbookcategory = p_335962_.readEnum(CraftingBookCategory.class);
		int i = p_335962_.readVarInt();
		NonNullList<Ingredient> nonnulllist = add(NonNullList.withSize(i, Ingredient.EMPTY));
		nonnulllist.replaceAll(p_327214_ -> Ingredient.CONTENTS_STREAM_CODEC.decode(p_335962_));
		ItemStack itemstack = ItemStack.STREAM_CODEC.decode(p_335962_);
		return new PurifiedWaterCraftingRecipe(s, craftingbookcategory, itemstack, add(nonnulllist));
	}

	private static void toNetwork(RegistryFriendlyByteBuf p_329239_, PurifiedWaterCraftingRecipe p_44282_) {
		p_329239_.writeUtf(p_44282_.getGroup());
		p_329239_.writeEnum(p_44282_.category());
		NonNullList<Ingredient> nonnulllist = add(p_44282_.getIngredients());
		p_329239_.writeVarInt(nonnulllist.size());

		for (Ingredient ingredient : nonnulllist) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(p_329239_, ingredient);
		}

		ItemStack.STREAM_CODEC.encode(p_329239_, p_44282_.getResultItem(null));
	}

	protected static NonNullList<Ingredient> add(NonNullList<Ingredient> ing){
		ItemStack potion = PotionContents.createItemStack(Items.POTION, SPotions.PURIFIED_WATER.holder());
		if (ing.get(ing.size()-1).test(potion)) {
			return ing;
		} else {
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(ing.size()+1, Ingredient.EMPTY);
			for(int j = 0; j < ing.size(); ++j) {
				nonnulllist.set(j, ing.get(j));
			}
			nonnulllist.set(ing.size(), Ingredient.of(potion));
			return nonnulllist;
		}
	}


	private static final MapCodec<PurifiedWaterCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
			p_327212_ -> p_327212_.group(
					Codec.STRING.optionalFieldOf("group", "").forGetter(p_299460_ -> p_299460_.getGroup()),
					CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_297437_ -> p_297437_.category()),
					ItemStack.STRICT_CODEC.fieldOf("result").forGetter(p_300770_ -> p_300770_.getResultItem(null)),
					Ingredient.CODEC_NONEMPTY
					.listOf()
					.fieldOf("ingredients")
					.flatXmap(
							p_297969_ -> {
								Ingredient[] aingredient = p_297969_.stream().filter(p_298915_ -> !p_298915_.isEmpty()).toArray(Ingredient[]::new);
								if (aingredient.length == 0) {
									return DataResult.error(() -> "No ingredients for shapeless recipe");
								} else {
									return aingredient.length > /*ShapedRecipe.MAX_WIDTH*/3 * /*ShapedRecipe.MAX_HEIGHT*/3
											? DataResult.error(() -> "Too many ingredients for shapeless recipe")
													: DataResult.success(add(NonNullList.of(Ingredient.EMPTY, aingredient)));
								}
							},
							DataResult::success
							)
					.forGetter(p_298509_ -> add(p_298509_.getIngredients()))
					)
			.apply(p_327212_, PurifiedWaterCraftingRecipe::new)
			);
	public static final StreamCodec<RegistryFriendlyByteBuf, PurifiedWaterCraftingRecipe> STREAM_CODEC = StreamCodec.of(
			PurifiedWaterCraftingSerializer::toNetwork, PurifiedWaterCraftingSerializer::fromNetwork
			);

	@Override
	public MapCodec<PurifiedWaterCraftingRecipe> codec() {
		return CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, PurifiedWaterCraftingRecipe> streamCodec() {
		return STREAM_CODEC;
	}
}
