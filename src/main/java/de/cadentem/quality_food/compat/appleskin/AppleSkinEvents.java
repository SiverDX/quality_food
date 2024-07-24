package de.cadentem.quality_food.compat.appleskin;

import de.cadentem.quality_food.util.FoodUtils;
import squeek.appleskin.api.event.FoodValuesEvent;

/** Appleskin queries the data component instead of using 'getFoodProperties' from the item */
public class AppleSkinEvents {
    public static void handleFoodProperties(final FoodValuesEvent event) {
        event.modifiedFoodProperties = FoodUtils.handleFoodProperties(event.itemStack, event.defaultFoodProperties);
    }
}
