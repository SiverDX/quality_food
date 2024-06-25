package de.cadentem.quality_food.mixin.sophisticatedcore;

import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

/** Apply quality when crafting with shift-click */
@Mixin(StorageContainerMenuBase.class)
public abstract class StorageContainerMenuBaseMixin extends AbstractContainerMenu {
    protected StorageContainerMenuBaseMixin(@Nullable final MenuType<?> type, int containerId) {
        super(type, containerId);
    }

    @ModifyVariable(method = "quickMoveStack", at = @At("STORE"), ordinal = 2)
    private ItemStack quality_food$applyQuality(final ItemStack stack) {
        getOpenContainer().ifPresent(container -> {
            if (container instanceof CraftingUpgradeContainer craftingContainer) {
                QualityUtils.applyQuality(stack, player, Bonus.additive(QualityUtils.getQualityBonus(craftingContainer.getSlots(), slot -> !(slot instanceof ResultSlot))));
            }
        });

        return stack;
    }

    @Shadow(remap = false) @Final protected Player player;
    @Shadow(remap = false) public abstract Optional<UpgradeContainerBase<?, ?>> getOpenContainer();
}
