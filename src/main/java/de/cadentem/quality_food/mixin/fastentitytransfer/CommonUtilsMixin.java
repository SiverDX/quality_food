package de.cadentem.quality_food.mixin.fastentitytransfer;

import com.christofmeg.fastentitytransfer.CommonUtils;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(CommonUtils.class)
public class CommonUtilsMixin {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // ignore
    @ModifyArg(method = "doLeftClickInteractions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 2))
    private static ItemStack quality_food$applyQuality(final ItemStack output, @Local final AbstractFurnaceBlockEntity furnace, @Local(argsOnly = true, ordinal = 0) final Optional<?> recipeOptional, @Local(argsOnly = true) final Player player) {
        if (furnace.getLevel() == null || (recipeOptional.isPresent() && recipeOptional.get() instanceof Recipe<?> recipe && ServerConfig.isNoQualityRecipe(recipe))) {
            return output;
        }

        Utils.useQuality(furnace, output, player);
        return output;
    }
}
