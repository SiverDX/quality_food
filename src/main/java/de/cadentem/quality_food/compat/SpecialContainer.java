package de.cadentem.quality_food.compat;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpecialContainer extends SimpleContainer {
    private final List<ItemStack> ingredients;

    public SpecialContainer(int size) {
        super(size);
        ingredients = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public void setItem(final int index, @NotNull final ItemStack stack) {
        super.setItem(index, stack);
        ingredients.set(index, stack);
    }

    public List<ItemStack> getIngredients() {
        return ingredients;
    }
}
