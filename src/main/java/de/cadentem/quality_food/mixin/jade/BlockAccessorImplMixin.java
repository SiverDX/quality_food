package de.cadentem.quality_food.mixin.jade;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.impl.BlockAccessorImpl;

import java.util.function.Consumer;

/** Jade for 1.20 does not support Block server data */
@Mixin(value = BlockAccessorImpl.class, remap = false)
public abstract class BlockAccessorImplMixin {
    @ModifyVariable(method = "lambda$handleRequest$0", at = @At(value = "STORE"))
    private static BlockEntity test(final BlockEntity blockEntity, @Local(argsOnly = true) final BlockAccessor accessor, @Local(argsOnly = true) final Consumer<CompoundTag> responseSender) {
        if (blockEntity == null && Utils.isValidBlock(accessor.getBlockState())) {
            CompoundTag tag = accessor.getServerData();
            tag.putInt(QualityFood.concat("ordinal"), LevelData.get(accessor.getLevel(), accessor.getPosition()).ordinal());

            // Without this the overlay breaks
            tag.putInt("x", accessor.getPosition().getX());
            tag.putInt("y", accessor.getPosition().getY());
            tag.putInt("z", accessor.getPosition().getZ());

            responseSender.accept(tag);
        }

        return blockEntity;
    }
}
