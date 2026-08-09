package de.cadentem.quality_food.client;

import de.cadentem.quality_food.util.StorageRecipeCache;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void invalidateStorageRecipes(final RecipesUpdatedEvent event) {
        StorageRecipeCache.invalidate();
    }
}
