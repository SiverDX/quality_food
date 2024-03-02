package de.cadentem.quality_food.events;

import crystalspider.harvestwithease.api.event.HarvestWithEaseEvent;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;

public class ModEvents {
    public static void handleHarvestEvent(final HarvestWithEaseEvent.HarvestDrops event) {
        for (ItemStack stack : event.drops) {
            QualityUtils.applyQuality(stack, event.getTargetBlock(), event.getEntity());
        }
    }
}
