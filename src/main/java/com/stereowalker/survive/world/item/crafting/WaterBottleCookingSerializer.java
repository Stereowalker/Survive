package com.stereowalker.survive.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe.Factory;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;

public class WaterBottleCookingSerializer extends SimpleCookingSerializer<WaterBottleSmeltingRecipe> {
    private final StreamCodec<RegistryFriendlyByteBuf, WaterBottleSmeltingRecipe> streamCodec;
    private final MapCodec<WaterBottleSmeltingRecipe> codec;
    
	public WaterBottleCookingSerializer(Factory<WaterBottleSmeltingRecipe> pFactory, int pCookingTime) {
		super(pFactory, pCookingTime);
		this.codec = RecordCodecBuilder.mapCodec(
	            p_296927_ -> p_296927_.group(
	                        Codec.STRING.optionalFieldOf("group", "").forGetter(p_296921_ -> ""),
	                        CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(p_296924_ -> CookingBookCategory.MISC),
	                        Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(p_296922_ -> 0f),
	                        Codec.INT.fieldOf("cookingtime").orElse(pCookingTime).forGetter(p_296919_ -> /*this.defaultCookingTime*/pCookingTime)
	                    )
	                    .apply(p_296927_, WaterBottleSmeltingRecipe::new)
	        );
        this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
	}

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, WaterBottleSmeltingRecipe> streamCodec() {
        return this.streamCodec;
    }

    @Override
    public MapCodec<WaterBottleSmeltingRecipe> codec() {
        return this.codec;
    }

	public WaterBottleSmeltingRecipe fromNetwork(RegistryFriendlyByteBuf pBuffer) {
		String s = pBuffer.readUtf();
		float f = pBuffer.readFloat();
		int i = pBuffer.readVarInt();
		return this.factory.create(s, CookingBookCategory.MISC, null, null, f, i);
	}

	public void toNetwork(RegistryFriendlyByteBuf pBuffer, WaterBottleSmeltingRecipe pRecipe) {
		pBuffer.writeUtf(pRecipe.getGroup());
		pBuffer.writeFloat(pRecipe.getExperience());
		pBuffer.writeVarInt(pRecipe.getCookingTime());
	}
}
