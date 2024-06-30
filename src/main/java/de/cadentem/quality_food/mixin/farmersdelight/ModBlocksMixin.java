package de.cadentem.quality_food.mixin.farmersdelight;

import de.cadentem.quality_food.compat.farmersdelight.FarmersDelightBaleBlock;
import de.cadentem.quality_food.compat.farmersdelight.FarmersDelightBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vectorwing.farmersdelight.common.block.RiceBaleBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;

/** To identify the blocks which need the quality state */
@Mixin(value = ModBlocks.class, remap = false)
public abstract class ModBlocksMixin {
    @Redirect(method = {/* Carrot */ "lambda$static$6", /* Potato */ "lambda$static$7", /* Beetroot */ "lambda$static$8", /* Cabbage */ "lambda$static$9", /* Tomato */ "lambda$static$10", /* Onion */ "lambda$static$11", /* Rice bag */ "lambda$static$13"}, at = @At(value = "NEW", args = "class=net/minecraft/world/level/block/Block"))
    private static Block quality_food$handleCrates(final BlockBehaviour.Properties properties) {
        return new FarmersDelightBlock(properties);
    }

    @Redirect(method = "lambda$static$12", at = @At(value = "NEW", args = "class=vectorwing/farmersdelight/common/block/RiceBaleBlock"))
    private static RiceBaleBlock quality_food$handleRiceBale(final BlockBehaviour.Properties properties) {
        return new FarmersDelightBaleBlock(properties);
    }
}
