package de.cadentem.quality_food.mixin.client;

import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Inject(method = "tryRenderGuiItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V", shift = At.Shift.AFTER))
    private void renderIcon(final LivingEntity entity, final ItemStack stack, int x, int y, int modelSeed, int blitOffset3D, final CallbackInfo callback) {
        if (!QualityUtils.hasQuality(stack)) {
            return;
        }

        blitOffset += 100;
        renderGuiItem(QualityUtils.getOverlay(stack), x, y);
        blitOffset -= 100;
    }

    @Shadow
    public float blitOffset;

    @Shadow
    public abstract void renderGuiItem(ItemStack pStack, int pX, int pY);
}
