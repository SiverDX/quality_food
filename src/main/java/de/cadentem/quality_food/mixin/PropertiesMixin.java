package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.compat.PropertiesExtension;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockBehaviour.Properties.class)
public abstract class PropertiesMixin implements PropertiesExtension {
    @Unique
    private boolean quality_food$isQualityBlock;

    @Override
    public BlockBehaviour.Properties quality_food$qualityBlock() {
        quality_food$isQualityBlock = true;
        return (BlockBehaviour.Properties) (Object) this;
    }

    @Override
    public boolean quality_food$isQualityBlock() {
        return quality_food$isQualityBlock;
    }
}
