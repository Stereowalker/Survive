package com.stereowalker.survive.world.item.crafting;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;

public class WaterBottleCookingSerializer extends SimpleCookingSerializer<WaterBottleSmeltingRecipe> {
   public WaterBottleCookingSerializer(WaterBottleCookingSerializer.CookieBaker<WaterBottleSmeltingRecipe> pFactory, int pDefaultCookingTime) {
	   super(pFactory, pDefaultCookingTime);
   }

   @Override
   public WaterBottleSmeltingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
      String s = GsonHelper.getAsString(pJson, "group", "");
      float f = GsonHelper.getAsFloat(pJson, "experience", 0.0F);
      int i = GsonHelper.getAsInt(pJson, "cookingtime", this.defaultCookingTime);
      return this.factory.create(pRecipeId, s, null, null, f, i);
   }

   @Override
   public WaterBottleSmeltingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
      String s = pBuffer.readUtf();
      float f = pBuffer.readFloat();
      int i = pBuffer.readVarInt();
      return this.factory.create(pRecipeId, s, null, null, f, i);
   }

   @Override
   public void toNetwork(FriendlyByteBuf pBuffer, WaterBottleSmeltingRecipe pRecipe) {
      pBuffer.writeUtf(pRecipe.getGroup());
      pBuffer.writeFloat(pRecipe.getExperience());
      pBuffer.writeVarInt(pRecipe.getCookingTime());
   }
}
