package de.cadentem.quality_food.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = At.Shift.AFTER))
    private void quality_food$renderIcon(final LivingEntity entity, final Level level, final ItemStack stack, int x, int y, int seed, int guiOffset, final CallbackInfo callback, @Local final BakedModel model) {
        Quality quality = QualityUtils.getQuality(stack);

        if (quality == Quality.NONE) {
            return;
        }

        GuiGraphics instance = (GuiGraphics) (Object) this;
        instance.pose().pushPose();
        instance.pose().translate(0, 0, 200 + (model.isGui3d() ? guiOffset : 0));
        instance.blitSprite(quality.getType().icon(), x, y, 16, 16);
        instance.pose().popPose();
    }
}
