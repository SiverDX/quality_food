package de.cadentem.quality_food.compat.harvest_with_ease;

import de.cadentem.quality_food.util.QualityUtils;
import it.crystalnest.harvest_with_ease.api.event.HarvestEvents;
import net.minecraft.world.item.ItemStack;

public class Events {
    public static void handleHarvestEvent(final HarvestEvents.HarvestDropsEvent event) {
        for (ItemStack stack : event.getDrops()) {
            QualityUtils.applyQuality(stack, event.getCrop(), event.getEntity(), event.getLevel().getBlockState(event.getPos().below()));
        }
    }
}
