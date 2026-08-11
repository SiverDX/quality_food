package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.AnimalData;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Cow.class)
public abstract class CowMixin extends Animal {
    protected CowMixin(final EntityType<? extends Animal> type, final Level level) {
        super(type, level);
    }

    @ModifyVariable(method = "mobInteract", at = @At("STORE"), name = "itemstack1")
    private ItemStack quality_food$applyQuality(final ItemStack milkBucket, @Local(argsOnly = true) final Player player) {
        QualityUtils.applyQuality(milkBucket, player, AnimalData.getPotential(this));
        return milkBucket;
    }
}
