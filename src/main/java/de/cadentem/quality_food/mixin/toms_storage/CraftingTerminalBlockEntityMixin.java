package de.cadentem.quality_food.mixin.toms_storage;

import com.tom.storagemod.block.entity.CraftingTerminalBlockEntity;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

/** Apply quality when items are converted from / to their storage variants */
@Mixin(CraftingTerminalBlockEntity.class)
public abstract class CraftingTerminalBlockEntityMixin extends BlockEntity {
    @Shadow(remap = false) @Final private CraftingContainer craftMatrix;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow private Optional<RecipeHolder<CraftingRecipe>> currentRecipe;

    public CraftingTerminalBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @ModifyArg(method = "onCraftingMatrixChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 1))
    private ItemStack quality_food$handleConversion(final ItemStack stack) {
        QualityUtils.handleConversion(stack, craftMatrix, currentRecipe.get(), level != null ? level.registryAccess() : null);
        return stack;
    }
}
