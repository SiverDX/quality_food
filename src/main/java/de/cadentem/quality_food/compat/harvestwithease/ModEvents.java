package de.cadentem.quality_food.compat.harvestwithease;

import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import it.crystalnest.harvest_with_ease.api.event.HarvestEvents;
import net.minecraft.world.item.ItemStack;

public class ModEvents {
    public static void handleHarvestEvent(final HarvestEvents.HarvestDropsEvent event) {
        for (ItemStack stack : event.getDrops()) {
            Quality blockQuality = LevelData.get(event.getLevel(), event.getPos(), true);
            QualityUtils.applyQuality(stack, event.getCrop(), blockQuality, event.getEntity(), event.getLevel().getBlockState(event.getPos().below()));
        }
    }
}
