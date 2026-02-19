package de.cadentem.quality_food.core;

import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.codecs.QualityType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public record Modification(Type type, float amount) {
    public static final Modification NONE = additive(0);

    public static Modification additive(float amount) {
        return new Modification(Type.ADDITIVE, amount);
    }

    public static Modification multiplicative(float amount) {
        return new Modification(Type.MULTIPLICATIVE, amount);
    }

    public double apply(final double chance) {
        return switch (type) {
            case ADDITIVE -> chance + amount;
            case MULTIPLICATIVE -> chance * amount;
        };
    }

    public static Modification luck(@Nullable final Player player) {
        if (player == null) {
            return NONE;
        }

        float multiplier = (float) (player.getAttributeValue(Attributes.LUCK) * ServerConfig.LUCK_MULTIPLIER.get());
        return Modification.multiplicative(Math.max(1, multiplier));
    }

    public static Modification harvestOrSeedMultiplier(final Holder<QualityType> type, final ItemStack stack) {
        if (stack.is(Tags.Items.CROPS)) {
            return Modification.multiplicative((float) type.value().cropMultiplier());
        } else if (stack.is(Tags.Items.SEEDS)) {
            return Modification.multiplicative((float) type.value().seedMultiplier());
        }

        return NONE;
    }

    public static Modification farmland(final BlockState crop, @Nullable final BlockState farmland) {
        if (farmland == null) {
            return NONE;
        }

        return Modification.multiplicative((float) ServerConfig.getFarmlandMultiplier(crop, farmland));
    }

    public enum Type {
        ADDITIVE,
        MULTIPLICATIVE
    }
}
