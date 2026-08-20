package de.cadentem.quality_food.mixin.vintagedelight;

import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.ribs.vintagedelight.block.entity.FermentingJarBlockEntity;
import net.ribs.vintagedelight.recipe.FermentingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FermentingJarBlockEntity.class, remap = false)
public abstract class FermentingJarBlockEntityMixin extends BlockEntity {
    @Shadow @Final private SimpleContainer inputInventory;

    public FermentingJarBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    @Inject(method = "craftItem", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemStackHandler;insertItem(ILnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    private void quality_food$applyQuality(final FermentingRecipe recipe, final CallbackInfo callback) {
        //noinspection DataFlowIssue -> level cannot be null here
        Utils.incrementQuality(this, Utils.collectIngredients(inputInventory, () -> 5), recipe.getResultItem(level.registryAccess()));
    }

    /** Display particles to show how much quality the block has stored */
    @Inject(method = "tick", at = @At("TAIL"))
    private void quality_food$handleParticles(final Level level, final BlockPos position, final BlockState state, final CallbackInfo callback) {
        if (level instanceof ServerLevel serverLevel) {
            Utils.sendParticles(serverLevel, this, position);
        }
    }
}
