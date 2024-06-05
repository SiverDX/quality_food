package de.cadentem.quality_food.events;

import de.cadentem.quality_food.util.QualityUtils;
import it.crystalnest.harvest_with_ease.api.event.HarvestEvents;
import net.minecraft.world.item.ItemStack;

public class ModEvents {
    public static void handleHarvestEvent(final HarvestEvents.HarvestDropsEvent event) {
        for (ItemStack stack : event.getDrops()) {
            QualityUtils.applyQuality(stack, event.getCrop(), event.getEntity());
        }
    }
}
