package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.compat.PropertiesExtension;
import de.cadentem.quality_food.compat.QualityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin implements QualityBlock {
    @Unique
    private boolean quality_food$isQualityBlock;

    @Override
    public void quality_food$setQualityBlock(boolean isQualityBlock) {
        this.quality_food$isQualityBlock = isQualityBlock;
    }

    @Override
    public boolean quality_food$isQualityBlock() {
        return quality_food$isQualityBlock;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void quality_food$handleQuality(final BlockBehaviour.Properties properties, final CallbackInfo callback) {
        if (((PropertiesExtension) properties).quality_food$isQualityBlock()) {
            quality_food$setQualityBlock(true);
        }
    }
}
