package com.stereowalker.survive.world.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.stereowalker.survive.world.item.alchemy.SPotions;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class PurifiedWaterCraftingSerializer implements RecipeSerializer<PurifiedWaterCraftingRecipe> {
    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;
    public PurifiedWaterCraftingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
       String s = GsonHelper.getAsString(pJson, "group", "");
       NonNullList<Ingredient> nonnulllist = add(itemsFromJson(GsonHelper.getAsJsonArray(pJson, "ingredients")));
       CraftingBookCategory craftingbookcategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(pJson, "category", (String)null), CraftingBookCategory.MISC);
       if (nonnulllist.isEmpty()) {
          throw new JsonParseException("No ingredients for shapeless recipe");
       } else if (nonnulllist.size() > MAX_WIDTH * MAX_HEIGHT) {
          throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is " + (MAX_WIDTH * MAX_HEIGHT));
       } else {
          ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
          return new PurifiedWaterCraftingRecipe(pRecipeId, s, craftingbookcategory, itemstack, add(nonnulllist));
       }
    }

    private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
       NonNullList<Ingredient> nonnulllist = NonNullList.create();

       for(int i = 0; i < pIngredientArray.size(); ++i) {
          Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i));
          if (/* TODO: Wait for an implementation on forge's end to this net.minecraftforge.common.ForgeConfig.SERVER.skipEmptyShapelessCheck.get()*/ true || !ingredient.isEmpty()) {
             nonnulllist.add(ingredient);
          }
       }

       return nonnulllist;
    }

    public PurifiedWaterCraftingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
       String s = pBuffer.readUtf();
       CraftingBookCategory craftingbookcategory = pBuffer.readEnum(CraftingBookCategory.class);
       int i = pBuffer.readVarInt();
       NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

       for(int j = 0; j < nonnulllist.size(); ++j) {
          nonnulllist.set(j, Ingredient.fromNetwork(pBuffer));
       }

       ItemStack itemstack = pBuffer.readItem();
       return new PurifiedWaterCraftingRecipe(pRecipeId, s, craftingbookcategory, itemstack, add(nonnulllist));
    }

    public void toNetwork(FriendlyByteBuf pBuffer, PurifiedWaterCraftingRecipe pRecipe) {
       pBuffer.writeUtf(pRecipe.getGroup());
       NonNullList<Ingredient> nonnulllist = add(pRecipe.getIngredients());
       pBuffer.writeVarInt(nonnulllist.size());

       for(Ingredient ingredient : nonnulllist) {
          ingredient.toNetwork(pBuffer);
       }

       pBuffer.writeItem(pRecipe.getResultItem());
    }
	
	protected static NonNullList<Ingredient> add(NonNullList<Ingredient> ing){
		ItemStack potion = PotionUtils.setPotion(new ItemStack(Items.POTION), SPotions.PURIFIED_WATER);
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
 }
