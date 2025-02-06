package de.cadentem.quality_food.util;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ContainerUtils {
    public static Collection<ItemStack> getIngredients(final List<Slot> slots, final Predicate<Slot> isSlotValid) {
        Set<ItemStack> ingredients = new HashSet<>();

        for (Slot slot : slots) {
            if (isSlotValid.test(slot)) {
                ingredients.add(slot.getItem());
            }
        }

        return ingredients;
    }
}
