package com.stereowalker.survive.world.item.crafting;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.IForgeRegistry;

public class SRecipeSerializer {
	public static List<RecipeSerializer<?>> RECIPES = new ArrayList<RecipeSerializer<?>>();

	public static final SimpleRecipeSerializer<CharcoalFilterRecipe> CRAFTING_SPECIAL_CHARCOAL_FILTERING = register("crafting_charcoal_filter", new SimpleRecipeSerializer<>(CharcoalFilterRecipe::new));

	public static void registerAll(IForgeRegistry<RecipeSerializer<?>> registry) {
		for(RecipeSerializer<?> effect : RECIPES) {
			registry.register(effect);
			Survive.getInstance().debug("Recipe Serializer: \""+effect.getRegistryName().toString()+"\" registered");
		}
		Survive.getInstance().debug("All Recipe Serializers Registered");
	}

	public static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String name, S recipeSerializer) {
		recipeSerializer.setRegistryName(Survive.getInstance().location(name));
		RECIPES.add(recipeSerializer);
		return recipeSerializer;
	}
}
