package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

/** Apply quality to cut items */
@Mixin(value = CuttingBoardBlockEntity.class, remap = false)
public abstract class CuttingBoardBlockEntityMixin extends SyncedBlockEntity {
    public CuttingBoardBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    @ModifyArg(method = "lambda$processStoredItemUsingTool$2", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"), index = 1)
    private ItemStack quality_food$applyQuality(final ItemStack stack, @Local(argsOnly = true) @Nullable final Player player) {
        //noinspection DataFlowIssue -> level will have been accessed by this point, therefor cannot be null
        QualityUtils.applyQuality(stack, List.of(getStoredItem()), player, level.registryAccess());
        return stack;
    }

    @Shadow public abstract ItemStack getStoredItem();
}
