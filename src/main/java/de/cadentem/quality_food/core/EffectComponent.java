package de.cadentem.quality_food.core;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record EffectComponent(FoodProperties.PossibleEffect possibleEffect) implements TooltipComponent { }
