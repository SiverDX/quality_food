package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.block.FeastBlock;

@Mixin(FeastBlock.class)
public class FeastBlockMixin {
    @Unique
    private static final IntegerProperty food_quality$QUALITY = IntegerProperty.create(QualityUtils.STATE_QUALITY_TAG, 0, Quality.values().length - 1);

    @ModifyArg(method = "createBlockStateDefinition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"))
    private Property<?>[] food_quality$addQualityProperty(final Property<?>[] properties) {
        Property<?>[] modified = new Property[properties.length + 1];
        System.arraycopy(properties, 0, modified, 0, properties.length);
        modified[modified.length - 1] = food_quality$QUALITY;
        return modified;
    }

    @ModifyReturnValue(method = "getServingItem", at = @At("RETURN"), remap = false)
    private ItemStack food_quality$applyQualityToItem(final ItemStack stack, /* Method parameters: */ final BlockState state) {
        QualityUtils.applyQuality(stack, Quality.get(state.getValue(food_quality$QUALITY)));
        return stack;
    }

    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState food_quality$applyQualityToBlock(final BlockState original, /* Method parameters: */ final BlockPlaceContext context) {
        return original.setValue(food_quality$QUALITY, QualityUtils.getQuality(context.getItemInHand()).ordinal());
    }
}
