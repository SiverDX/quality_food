package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(value = CrushingWheelControllerBlockEntity.class, remap = false)
public abstract class CrushingWheelControllerBlockEntityMixin extends SmartBlockEntity {
    public CrushingWheelControllerBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    @ModifyArg(method = "applyRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/item/ItemHelper;addToList(Lnet/minecraft/world/item/ItemStack;Ljava/util/List;)V", ordinal = 0))
    private ItemStack quality_food$applyQuality(final ItemStack result, @Local(name = "input") final ItemStack input) {
        //noinspection DataFlowIssue -> level is present
        QualityUtils.applyQuality(result, List.of(input), null, getLevel().registryAccess());
        return result;
    }
}
