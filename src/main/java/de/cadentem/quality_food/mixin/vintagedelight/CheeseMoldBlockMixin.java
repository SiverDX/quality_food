package de.cadentem.quality_food.mixin.vintagedelight;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.ribs.vintagedelight.block.CheeseMoldBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CheeseMoldBlock.class)
public abstract class CheeseMoldBlockMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void quality_food$skipClient(final CallbackInfoReturnable<ItemInteractionResult> callback, @Local(argsOnly = true) final Level level) {
        // Since the quality level data is server-only, continuing the logic as client would cause rendering issues
        // - The item count will decrease (fixed itself after relogging / interacting with the item slot)
        // - A placement attempt will be made (due to 'setBlock') and then reverted
        // Generally there is no logic here that needs to be done on the client - the only "issue" is that we have to return a successful interaction

        if (level.isClientSide()) {
            callback.setReturnValue(ItemInteractionResult.sidedSuccess(true));
        }
    }

    /** Only allow stacking of matching quality */
    @Definition(id = "itemStack", local = @Local(type = ItemStack.class, name = "itemStack"))
    @Definition(id = "getItem", method = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    @Definition(id = "CHEESE_CURDS", field = "Lnet/ribs/vintagedelight/init/items/ModItems;CHEESE_CURDS:Lnet/neoforged/neoforge/registries/DeferredItem;")
    @Definition(id = "get", method = "Lnet/neoforged/neoforge/registries/DeferredItem;get()Ljava/lang/Object;")
    @Expression("itemStack.getItem() == CHEESE_CURDS.get()")
    @ModifyExpressionValue(method = "useItemOn", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private boolean quality_food$canPlace(boolean original, @Local(argsOnly = true) final Level level, @Local(argsOnly = true) final BlockPos position, @Local(name = "level") int size, @Local(name = "itemStack") final ItemStack itemInHand) {
        if (!original) {
            return false;
        }

        if (size > 0) {
            Quality quality = LevelData.get(level, position);
            return quality == QualityUtils.getQuality(itemInHand);
        }

        return true;
    }

    /** Initialize or remove quality when adding / removing entries from the stack */
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void quality_food$handleQuality(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos position, final Player player, final InteractionHand hand, final BlockHitResult hit, final CallbackInfoReturnable<ItemInteractionResult> callback) {
        ItemStack stack = player.getItemInHand(hand);
        Quality quality = QualityUtils.getQuality(stack);

        if (stack.isEmpty()) {
            LevelData.remove(level, position);
        } else if (quality.level() > 0) {
            LevelData.set(level, position, quality);
        }
    }

    /** Apply quality to taken stacks */
    @ModifyVariable(method = "useWithoutItem", at = @At(value = "STORE"), name = "cheeseItem")
    private ItemStack quality_food$applyQuality(final ItemStack stack, @Local(argsOnly = true) final Level level, @Local(argsOnly = true) final BlockPos position) {
        Quality quality = LevelData.get(level, position, true);

        if (quality.level() > 0) {
            QualityUtils.applyQuality(stack, quality);
        }

        return stack;
    }

    /** Remove quality when empty */
    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void quality_food$removeQuality(final BlockState state, final Level level, final BlockPos position, final Player player, final BlockHitResult hit, final CallbackInfoReturnable<InteractionResult> callback) {
        LevelData.remove(level, position);
    }
}
