package de.cadentem.quality_food.events;

import de.cadentem.quality_food.attachments.LevelData;
import de.cadentem.quality_food.util.QualityUtils;
import it.crystalnest.harvest_with_ease.api.event.HarvestEvents;
import net.minecraft.world.item.ItemStack;

public class ModEvents {
    public static void handleHarvestEvent(final HarvestEvents.HarvestDropsEvent event) {
        for (ItemStack stack : event.getDrops()) {
            QualityUtils.applyQuality(stack, LevelData.get(event.getLevel(), event.getPos(), true), event.getCrop(), event.getEntity(), event.getLevel().getBlockState(event.getPos().below()));
        }
    }
}
