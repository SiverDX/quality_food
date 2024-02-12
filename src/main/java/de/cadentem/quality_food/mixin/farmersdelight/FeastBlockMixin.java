package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.core.Rarity;
import de.cadentem.quality_food.util.RarityUtils;
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
    private static final IntegerProperty food_quality$RARITY = IntegerProperty.create(RarityUtils.STATE_RARITY_TAG, 0, Rarity.values().length - 1);

    @ModifyArg(method = "createBlockStateDefinition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/StateDefinition$Builder;add([Lnet/minecraft/world/level/block/state/properties/Property;)Lnet/minecraft/world/level/block/state/StateDefinition$Builder;"))
    private Property<?>[] addRarityProperty(final Property<?>[] properties) {
        Property<?>[] modified = new Property[properties.length + 1];
        System.arraycopy(properties, 0, modified, 0, properties.length);
        modified[modified.length - 1] = food_quality$RARITY;
        return modified;
    }

    @ModifyReturnValue(method = "getServingItem", at = @At("RETURN"), remap = false)
    private ItemStack applyRarity(final ItemStack stack, /* Method parameters: */ final BlockState state) {
        RarityUtils.applyRarity(stack, Rarity.get(state.getValue(food_quality$RARITY)));
        return stack;
    }

    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState test(final BlockState original, /* Method parameters: */ final BlockPlaceContext context) {
        return original.setValue(food_quality$RARITY, RarityUtils.getRarity(context.getItemInHand()).ordinal());
    }
}
