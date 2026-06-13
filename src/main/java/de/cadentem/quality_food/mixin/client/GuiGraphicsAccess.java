package de.cadentem.quality_food.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccess {
    @Invoker("renderItem")
    void qualityFood$renderItem(@Nullable final LivingEntity entity, @Nullable final Level level, final ItemStack stack, int x, int y, int seed, int guiOffset);
}
