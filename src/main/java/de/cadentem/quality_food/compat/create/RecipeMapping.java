package de.cadentem.quality_food.compat.create;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.HashMap;

public class RecipeMapping {
    /**
     * Create throws the {@link net.minecraft.world.item.crafting.RecipeHolder} away </br>
     * But it is needed to filter certain recipes
     */
    public static final HashMap<Recipe<?>, RecipeHolder<?>> RECIPES = new HashMap<>();
}
