package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** Quality eggs have higher chance to spawn chicken */
@Mixin(ThrownEgg.class)
public abstract class ThrownEggMixin extends ThrowableItemProjectile {
    public ThrownEggMixin(final EntityType<? extends ThrowableItemProjectile> type, final Level level) {
        super(type, level);
    }

    @ModifyConstant(method = "onHit", constant = @Constant(intValue = 8))
    private int quality_food$modifyBaseChance(int original) {
        Quality quality = QualityUtils.getQuality(getItem());

        if (quality.level() > 0) {
            return Math.max(1, original - (quality.level() * 2));
        }

        return original;
    }

    @ModifyConstant(method = "onHit", constant = @Constant(intValue = 32))
    private int quality_food$modifyExtraChance(int original) {
        Quality quality = QualityUtils.getQuality(getItem());

        if (quality.level() > 0) {
            return Math.max(1, original - (quality.level() * 4));
        }

        return original;
    }
}
