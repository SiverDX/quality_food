package de.cadentem.quality_food.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cadentem.quality_food.util.OverlayUtils;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Shadow @Final private PoseStack pose;

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = At.Shift.AFTER))
    private void quality_food$renderIcon(final LivingEntity entity, final Level level, final ItemStack stack, final int x, final int y, final int seed, final int guiOffset, final CallbackInfo callback, @Local final BakedModel model) {
        if (!QualityUtils.hasQuality(stack) || OverlayUtils.isOverlay(stack)) {
            return;
        }

        int offset = 50 + (model.isGui3d() ? guiOffset : 0);

        pose.pushPose();
        pose.translate(0, 0, offset);
        renderItem(entity, level, OverlayUtils.getOverlay(stack), x, y, seed, guiOffset);
        pose.popPose();
    }

    @Shadow
    protected abstract void renderItem(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, int x, int y, int seed, int guiOffset);
}
