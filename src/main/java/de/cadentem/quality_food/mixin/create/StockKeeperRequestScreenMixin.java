package de.cadentem.quality_food.mixin.create;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import de.cadentem.quality_food.mixin.client.GuiGraphicsAccess;
import de.cadentem.quality_food.util.OverlayUtils;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StockKeeperRequestScreen.class, remap = false)
public abstract class StockKeeperRequestScreenMixin {
    @Inject(method = "renderItemEntry", at = @At("TAIL"))
    private void quality_food$renderQualityIcon(final GuiGraphics graphics, final float scale, final BigItemStack entry, final boolean isStackHovered, final boolean isRenderingOrders, final CallbackInfo callback) {
        if (!QualityUtils.hasQuality(entry.stack) || OverlayUtils.isOverlay(entry.stack)) {
            return;
        }

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 50);
        ((GuiGraphicsAccess) graphics).qualityFood$renderItem(null, Minecraft.getInstance().level, OverlayUtils.getOverlay(entry.stack), 0, 0, 0, 0);
        graphics.pose().popPose();
    }
}
