package com.stereowalker.survive.world.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class PurifiedWaterCraftingSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<PurifiedWaterCraftingRecipe> {
    private static final ResourceLocation NAME = new ResourceLocation("minecraft", "crafting_shapeless");
    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;
    public PurifiedWaterCraftingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
       String s = GsonHelper.getAsString(pJson, "group", "");
       NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "ingredients"));
       if (nonnulllist.isEmpty()) {
          throw new JsonParseException("No ingredients for shapeless recipe");
       } else if (nonnulllist.size() > MAX_WIDTH * MAX_HEIGHT) {
          throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is " + (MAX_WIDTH * MAX_HEIGHT));
       } else {
          ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
          return new PurifiedWaterCraftingRecipe(pRecipeId, s, itemstack, nonnulllist);
       }
    }

    private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
       NonNullList<Ingredient> nonnulllist = NonNullList.create();

       for(int i = 0; i < pIngredientArray.size(); ++i) {
          Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i));
          if (net.minecraftforge.common.ForgeConfig.SERVER.skipEmptyShapelessCheck.get() || !ingredient.isEmpty()) {
             nonnulllist.add(ingredient);
          }
       }

       return nonnulllist;
    }

    public PurifiedWaterCraftingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
       String s = pBuffer.readUtf();
       int i = pBuffer.readVarInt();
       NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

       for(int j = 0; j < nonnulllist.size(); ++j) {
          nonnulllist.set(j, Ingredient.fromNetwork(pBuffer));
       }

       ItemStack itemstack = pBuffer.readItem();
       return new PurifiedWaterCraftingRecipe(pRecipeId, s, itemstack, nonnulllist);
    }

    public void toNetwork(FriendlyByteBuf pBuffer, PurifiedWaterCraftingRecipe pRecipe) {
       pBuffer.writeUtf(pRecipe.getGroup());
       pBuffer.writeVarInt(pRecipe.getIngredients().size());

       for(Ingredient ingredient : pRecipe.getIngredients()) {
          ingredient.toNetwork(pBuffer);
       }

       pBuffer.writeItem(pRecipe.getResultItem());
    }
 }
