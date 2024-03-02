package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Apply quality to laid eggs */
@Mixin(Entity.class)
public class EntityMixin {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final ItemStack stack, float offsetY, final CallbackInfoReturnable<ItemEntity> callback) {
        if ((Object) this instanceof Chicken chicken && stack.is(Tags.Items.EGGS)) {
            QualityUtils.applyQuality(stack, chicken, Bonus.additive(0.05f));
        }
    }
}
