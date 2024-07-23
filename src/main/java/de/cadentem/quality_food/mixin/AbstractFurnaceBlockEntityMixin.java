package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends BaseContainerBlockEntity {
    protected AbstractFurnaceBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    /** Display particles to show how much quality the block has stored */
    @Inject(method = "serverTick", at = @At("TAIL"))
    private static void quality_food$handleParticles(final Level level, final BlockPos position, final BlockState state, final AbstractFurnaceBlockEntity furnace, final CallbackInfo callback) {
        if (level instanceof ServerLevel serverLevel) {
            Utils.sendParticles(serverLevel, furnace, position);
        }
    }

    /** Increment quality after cooking an item */
    @Inject(method = "burn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE))
    private static void quality_food$incrementQuality(final RegistryAccess access, @Nullable final RecipeHolder<?> recipe, final NonNullList<ItemStack> inventory, int maxStackSize, final AbstractFurnaceBlockEntity furnace, final CallbackInfoReturnable<Boolean> callback) {
        Utils.incrementQuality(furnace, inventory.get(0));
    }
}
