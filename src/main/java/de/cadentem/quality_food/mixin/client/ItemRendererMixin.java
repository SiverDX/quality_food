package de.cadentem.quality_food.mixin.client;

import de.cadentem.quality_food.util.OverlayUtils;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Render overlay */
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Inject(method = "tryRenderGuiItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V", shift = At.Shift.AFTER))
    private void quality_food$renderIcon(final LivingEntity entity, final ItemStack stack, int x, int y, int modelSeed, int blitOffset3D, final CallbackInfo callback) {
        if (!QualityUtils.hasQuality(stack) || OverlayUtils.isOverlay(stack)) {
            return;
        }

        blitOffset += 100;
        renderGuiItem(OverlayUtils.getOverlay(stack), x, y);
        blitOffset -= 100;
    }

    @Shadow
    public float blitOffset;

    @Shadow
    public abstract void renderGuiItem(final ItemStack stack, int x, int y);
}
