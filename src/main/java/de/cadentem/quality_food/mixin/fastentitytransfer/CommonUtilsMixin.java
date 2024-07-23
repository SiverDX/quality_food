package de.cadentem.quality_food.mixin.fastentitytransfer;

//import com.christofmeg.fastentitytransfer.CommonUtils;
//import com.google.common.util.concurrent.AtomicDouble;
//import com.llamalad7.mixinextras.sugar.Local;
//import de.cadentem.quality_food.capability.BlockDataProvider;
//import de.cadentem.quality_food.config.ServerConfig;
//import de.cadentem.quality_food.core.Bonus;
//import de.cadentem.quality_food.util.QualityUtils;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Recipe;
//import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.ModifyArg;
//
//import java.util.Optional;
//
//@Mixin(CommonUtils.class)
//public class CommonUtilsMixin {
//    @ModifyArg(method = "doLeftClickInteractions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 2))
//    private static ItemStack quality_food$applyQuality(final ItemStack output, @Local final AbstractFurnaceBlockEntity furnace, @Local(argsOnly = true, ordinal = 0) Optional<?> recipeOptional) {
//        if (furnace.getLevel() == null || (recipeOptional.isPresent() && recipeOptional.get() instanceof Recipe<?> recipe && ServerConfig.isNoQualityRecipe(recipe))) {
//            return output;
//        }
//
//        AtomicDouble bonus = new AtomicDouble(0);
//        BlockDataProvider.getCapability(furnace).ifPresent(data -> bonus.set(data.useQuality()));
//        QualityUtils.applyQuality(output, Bonus.additive(bonus.floatValue()));
//        return output;
//    }
//}
