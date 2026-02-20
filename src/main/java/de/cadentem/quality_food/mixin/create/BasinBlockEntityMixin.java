package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BasinBlockEntity.class)
public abstract class BasinBlockEntityMixin {
    // TODO :: could also add a flag in 'GoggleOverlayRenderer'
    //          and then modify 'getDescriptionId' of the item or sth. like that
    @ModifyArg(method = "addToGoggleTooltip", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/lang/LangBuilder;add(Lnet/minecraft/network/chat/MutableComponent;)Lnet/createmod/catnip/lang/LangBuilder;"))
    private MutableComponent quality_food$appendQuality(final MutableComponent component, @Local(name = "stackInSlot") final ItemStack stack) {
        Quality quality = QualityUtils.getQuality(stack);

        if (!QualityUtils.isValidQuality(quality)) {
            return component;
        }

        return component.append(Component.translatable("quality_food.create_goggle", quality.getType().value().name()));
    }
}
