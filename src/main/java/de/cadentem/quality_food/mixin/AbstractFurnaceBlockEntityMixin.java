package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends BaseContainerBlockEntity {
    protected AbstractFurnaceBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    /** Display particles to show how much quality the block has stored */
    @Inject(method = "serverTick", at = @At("TAIL"))
    private static void quality_food$handleParticles(final ServerLevel level, final BlockPos position, final BlockState state, final AbstractFurnaceBlockEntity furnace, final CallbackInfo callback) {
        Utils.sendParticles(level, furnace, position);
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void quality_food$incrementQuality(final ServerLevel level, final BlockPos position, final BlockState state, final AbstractFurnaceBlockEntity furnace, final CallbackInfo callback, @Local(name = "burnResult") final ItemStack result) {
        Utils.incrementQuality(furnace, List.of(furnace.getItem(0)), result);
    }
}
