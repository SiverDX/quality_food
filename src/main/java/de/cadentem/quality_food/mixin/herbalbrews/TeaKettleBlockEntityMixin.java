package de.cadentem.quality_food.mixin.herbalbrews;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.herbalbrews.core.blocks.entity.TeaKettleBlockEntity;
import net.satisfy.herbalbrews.core.recipe.TeaKettleRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TeaKettleBlockEntity.class)
public abstract class TeaKettleBlockEntityMixin extends BlockEntity {
    public TeaKettleBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    @Inject(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE))
    private void quality_food$incrementQuality(final TeaKettleRecipe recipe, final CallbackInfo callback, @Local(name = "inputStack") final ItemStack input) {
        Utils.incrementQuality(this, input, recipe.getIngredients().size(), recipe.getResultItem().getMaxStackSize());
    }

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void quality_food$handleParticle(final Level level, final BlockPos position, final BlockState state, final CallbackInfo callback) {
        if (level instanceof ServerLevel serverLevel) {
            Utils.sendParticles(serverLevel, this, position);
        }
    }
}
