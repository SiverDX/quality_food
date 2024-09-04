package de.cadentem.quality_food.mixin.cratedelight;

import com.axperty.cratedelight.block.ModBlocks;
import de.cadentem.quality_food.compat.PropertiesExtension;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ModBlocks.class, remap = false)
public abstract class ModBlocksMixin {
    @ModifyArg(method = {/* Carrot */ "lambda$static$0", /* Potato */ "lambda$static$1", /* Beetroot */ "lambda$static$2", /* Apple */ "lambda$static$3", /* Berry */ "lambda$static$4", /* Blueberry */ "lambda$static$5", /* Glow Berry */ "lambda$static$7", /* Egg */ "lambda$static$8", /* Duck Egg */ "lambda$static$9"
            , /* Emu Egg */ "lambda$static$10", /* Terrapin Egg */ "lambda$static$11", /* Crocodile Egg */ "lambda$static$12", /* Caiman Egg */ "lambda$static$13", /* Platypus Egg */ "lambda$static$14", /* Kiwi Egg */ "lambda$static$15", /* Kiwifruit */ "lambda$static$16", /* Banana */ "lambda$static$17", /* Salmon */ "lambda$static$18", /* Cod */ "lambda$static$19"
            , /* Catfish */ "lambda$static$20", /* Bass */ "lambda$static$21", /* End Fish */ "lambda$static$22", /* Red Mushroom */ "lambda$static$23", /* Brown Mushroom */ "lambda$static$24", /* Golden Carrot */ "lambda$static$25", /* Golden Apple */ "lambda$static$26", /* Cocoa Beans */ "lambda$static$27", /* Sugar */ "lambda$static$28"
            , /* Wheat Flour */ "lambda$static$30", /* Cinder Flour */ "lambda$static$32"
    }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V", remap = true))
    private static BlockBehaviour.Properties quality_food$markQualityBlock(final BlockBehaviour.Properties properties) {
        return ((PropertiesExtension) properties).quality_food$qualityBlock();
    }
}
