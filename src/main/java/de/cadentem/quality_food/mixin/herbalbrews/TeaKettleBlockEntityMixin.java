package de.cadentem.quality_food.mixin.herbalbrews;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(TeaKettleBlockEntity.class)
public abstract class TeaKettleBlockEntityMixin extends BlockEntity {
    public TeaKettleBlockEntityMixin(final BlockEntityType<?> type, final BlockPos position, final BlockState state) {
        super(type, position, state);
    }

    @Inject(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE))
    private void quality_food$storeIngredients(final TeaKettleRecipe recipe, final CallbackInfo callback, @Local(name = "inputStack") final ItemStack input, @Share("ingredients") final LocalRef<List<ItemStack>> ingredients) {
        if (!Utils.isValidItem(input)) {
            return;
        }

        if (ingredients.get() == null) {
            ingredients.set(new ArrayList<>());
        }

        ingredients.get().add(input);
    }

    @ModifyVariable(method = "craft", at = @At(value = "STORE"), name = "recipeOutput", remap = false)
    private ItemStack quality_food$incrementQuality(final ItemStack recipeOutput, final TeaKettleRecipe recipe, @Share("ingredients") final LocalRef<List<ItemStack>> ingredients) {
        Utils.incrementQuality(this, Objects.requireNonNullElse(ingredients.get(), List.of()), recipe.getResultItem().getCount());
        ingredients.set(null);
        return recipeOutput;
    }


    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void quality_food$handleParticle(final Level level, final BlockPos position, final BlockState state, final CallbackInfo callback) {
        if (level instanceof ServerLevel serverLevel) {
            Utils.sendParticles(serverLevel, this, position);
        }
    }
}
