package de.cadentem.quality_food.compat.harvestwithease;

import de.cadentem.quality_food.util.HarvestContext;
import de.cadentem.quality_food.util.QualityUtils;
import it.crystalnest.harvest_with_ease.api.event.HarvestEvents;
import net.minecraft.world.item.ItemStack;

public class ModEvents {
    public static void handleHarvestEvent(final HarvestEvents.HarvestDropsEvent event) {
        for (ItemStack stack : event.getDrops()) {
            QualityUtils.applyHarvestQuality(HarvestContext.create(stack, event.getLevel()).position(event.getPos()).state(event.getCrop()).player(event.getEntity()).build());
        }
    }
}
