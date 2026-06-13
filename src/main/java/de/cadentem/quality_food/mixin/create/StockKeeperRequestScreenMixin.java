package de.cadentem.quality_food.mixin.create;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StockKeeperRequestScreen.class)
public abstract class StockKeeperRequestScreenMixin {
    @Inject(method = "renderItemEntry", at = @At("TAIL"))
    private void quality_food$renderQualityIcon(final GuiGraphics graphics, final float scale, final BigItemStack entry, final boolean isStackHovered, final boolean isRenderingOrders, final CallbackInfo callback) {
        Quality quality = QualityUtils.getQuality(entry.stack);

        if (quality == Quality.NONE) {
            return;
        }

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 200);
        // No need to set coordinates since 'Create' sets up the position using 'PoseStack#translate'
        graphics.blitSprite(quality.getType().value().icon(), 0, 0, 16, 16);
        graphics.pose().popPose();
    }
}
