package de.cadentem.quality_food.mixin.vintagedelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.ribs.vintagedelight.block.entity.FermentingJarBlockEntity;
import net.ribs.vintagedelight.recipe.FermentingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FermentingJarBlockEntity.class)
public abstract class FermentingJarBlockEntityMixin extends BlockEntity {
    @Shadow @Final private ItemStackHandler itemHandler;

    public FermentingJarBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    @Inject(method = "craftItem", at = @At(value = "INVOKE", target = "Lnet/ribs/vintagedelight/block/entity/FermentingJarBlockEntity;placeOutput(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 0))
    private void quality_food$applyQuality(final CallbackInfo callback, @Local(name = "recipe") final FermentingRecipe recipe) {
        int resultStackSize = 1;

        if (recipe != null) {
            //noinspection DataFlowIssue -> level is not null
            resultStackSize = recipe.getResultItem(level.registryAccess()).getCount();
        }

        Utils.incrementQuality(this, Utils.collectIngredients(itemHandler, () -> 5), resultStackSize);
    }

    /** Display particles to show how much quality the block has stored */
    @Inject(method = "tick", at = @At("TAIL"))
    private void quality_food$handleParticles(final Level level, final BlockPos position, final BlockState state, final CallbackInfo callback) {
        if (level instanceof ServerLevel serverLevel) {
            Utils.sendParticles(serverLevel, this, position);
        }
    }
}
