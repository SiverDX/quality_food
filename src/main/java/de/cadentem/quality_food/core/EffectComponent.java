package de.cadentem.quality_food.core;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record EffectComponent(Pair<MobEffectInstance, Float> possibleEffect) implements TooltipComponent { }
