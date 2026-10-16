package de.cadentem.quality_food.mixin.client;

import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsMixin {
    @Inject(method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("TAIL"))
    private void quality_food$renderIcon(final LivingEntity entity, final Level level, final ItemStack stack, int x, int y, int seed, final CallbackInfo callback) {
        Quality quality = QualityUtils.getQuality(stack);
        if (quality == Quality.NONE) {
            return;
        }
        GuiGraphicsExtractor graphics = (GuiGraphicsExtractor) (Object) this;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, quality.getType().value().icon(), x, y, 16, 16);
    }
}
