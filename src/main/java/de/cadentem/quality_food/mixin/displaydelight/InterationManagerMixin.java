package de.cadentem.quality_food.mixin.displaydelight;

import com.jkvin114.displaydelight.events.InterationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(InterationManager.class)
public abstract class InterationManagerMixin {
//    @Inject(method = "tryPlaceItem")

    /** TODO
     * - level data for itemstack list if quality > 0
     *     - need to display info in jade?
     * - InterationManager#tryPlaceItem... -> store itemstack in level data
     * - InterationManager#tryTakeItemWithBareHand -> retrieve stored itemstack
     * - technically need to store it in item data as well when picked up in creative etc. and mixed exist?
     *
     * */
}
