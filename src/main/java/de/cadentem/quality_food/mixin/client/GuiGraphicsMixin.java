package de.cadentem.quality_food.mixin.client;

import de.cadentem.quality_food.component.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Shadow
    private @Final Minecraft minecraft;

    @Inject(method = "renderItem(Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At(value = "TAIL"))
    private void quality_food$renderIcon(final ItemStack stack, int x, int y, int seed, int guiOffset, final CallbackInfo callback) {
        quality_food$renderIcon(minecraft.player, minecraft.level, stack, x, y, seed, guiOffset);
    }

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V", at = @At(value = "TAIL"))
    private void quality_food$renderIcon(final LivingEntity entity, final Level level, final ItemStack stack, int x, int y, int seed, final CallbackInfo callback) {
        quality_food$renderIcon(entity, level, stack, x, y, seed, 0);
    }

    @Unique
    private void quality_food$renderIcon(final LivingEntity entity, final Level level, final ItemStack stack, int x, int y, int seed, int guiOffset) {
        Quality quality = QualityUtils.getQuality(stack);

        if (quality == Quality.NONE) {
            return;
        }

        GuiGraphics instance = (GuiGraphics) (Object) this;
        instance.pose().pushPose();
        instance.pose().translate(0, 0, 300);
        instance.blitSprite(quality.getType().icon(), x, y, 16, 16);
        instance.pose().popPose();
    }
}
