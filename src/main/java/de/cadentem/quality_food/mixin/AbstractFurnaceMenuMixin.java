package de.cadentem.quality_food.mixin;

import com.google.common.util.concurrent.AtomicDouble;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.BlockData;
import de.cadentem.quality_food.capability.AttachmentHandler;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Allow material (with quality) to grant its bonus when shift-clicking the crafting result */
@Mixin(AbstractFurnaceMenu.class)
public abstract class AbstractFurnaceMenuMixin extends RecipeBookMenu<SingleRecipeInput, AbstractCookingRecipe> {
    public AbstractFurnaceMenuMixin(final MenuType<?> type, int containerId) {
        super(type, containerId);
    }

    @Unique
    private BlockPos quality_food$blockEntityPosition;

    @Inject(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractFurnaceMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Player player, int slotIndex, final CallbackInfoReturnable<ItemStack> callback, @Local(ordinal = 1) final ItemStack stack) {
        AtomicDouble bonus = new AtomicDouble(0);

        if (quality_food$blockEntityPosition != null && !player.level().isClientSide()) {
            BlockEntity blockEntity = player.level().getBlockEntity(quality_food$blockEntityPosition);

            if (blockEntity != null) {
                BlockData blockData = blockEntity.getData(AttachmentHandler.BLOCK_DATA);
                bonus.set(blockData.useQuality());
                blockEntity.setChanged();
            }
        }

        QualityUtils.applyQuality(stack, player, Bonus.additive(bonus.floatValue()));
    }

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/inventory/RecipeBookType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V", at = @At("RETURN"))
    private void quality_food$storeBlockEntityPosition(final MenuType<?> menuType, final RecipeType<? extends AbstractCookingRecipe> recipeType, final RecipeBookType recipeBookType, int containerId, final Inventory playerInventory, final Container container, final ContainerData data, final CallbackInfo callback) {
        quality_food$blockEntityPosition = Utils.getBlockEntityPosition();
    }
}
