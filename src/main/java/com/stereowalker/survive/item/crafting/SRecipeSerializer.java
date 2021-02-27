package com.stereowalker.survive.item.crafting;

import java.util.ArrayList;
import java.util.List;

import com.stereowalker.survive.Survive;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.registries.IForgeRegistry;

public class SRecipeSerializer {
	public static List<IRecipeSerializer<?>> RECIPES = new ArrayList<IRecipeSerializer<?>>();

	public static final SpecialRecipeSerializer<CharcoalFilterRecipe> CRAFTING_SPECIAL_CHARCOAL_FILTERING = register("crafting_charcoal_filter", new SpecialRecipeSerializer<>(CharcoalFilterRecipe::new));

	public static void registerAll(IForgeRegistry<IRecipeSerializer<?>> registry) {
		for(IRecipeSerializer<?> effect : RECIPES) {
			registry.register(effect);
			Survive.debug("Recipe Serializer: \""+effect.getRegistryName().toString()+"\" registered");
		}
		Survive.debug("All Recipe Serializers Registered");
	}

	public static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String name, S recipeSerializer) {
		recipeSerializer.setRegistryName(Survive.location(name));
		RECIPES.add(recipeSerializer);
		return recipeSerializer;
	}
}
