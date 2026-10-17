package de.cadentem.quality_food.core;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

public record EffectComponent(ConsumeEffect effect) implements TooltipComponent { }
