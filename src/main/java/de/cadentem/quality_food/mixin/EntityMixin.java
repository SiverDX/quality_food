package de.cadentem.quality_food.mixin;

import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import de.cadentem.quality_food.core.attachments.AnimalData;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Apply quality to laid eggs */
@Mixin(Entity.class)
public class EntityMixin {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final ServerLevel level, final ItemStack stack, final Vec3 offset, final CallbackInfoReturnable<ItemEntity> callback) {
        if ((Object) this instanceof Chicken chicken && stack.is(Tags.Items.EGGS)) {
            QualityUtils.applyQuality(stack, null, AnimalData.getPotential(chicken), chicken.registryAccess());
        }
    }
}
