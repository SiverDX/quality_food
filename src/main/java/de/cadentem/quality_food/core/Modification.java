package de.cadentem.quality_food.core;

import de.cadentem.quality_food.config.QualityConfig;
import de.cadentem.quality_food.config.ServerConfig;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public record Modification(Type type, float amount) {

    public static final Modification NONE = additive(0);

    public static Modification additive(float amount) {
        return new Modification(Type.ADDITIVE, amount);
    }

    public static Modification multiplicative(float amount) {
        return new Modification(Type.MULTIPLICATIVE, amount);
    }

    public static Modification oddsMultiplier(float amount) {
        return new Modification(Type.ODDS_MULTIPLICATIVE, amount);
    }

    public double apply(final double chance) {
        return switch (type) {
            case ADDITIVE -> chance + amount;

            case MULTIPLICATIVE -> chance * amount;

            case ODDS_MULTIPLICATIVE -> {
                // For a stable increase of the chances across the board
                if (chance <= 0) {
                    yield 0;
                }

                if (chance >= 1) {
                    yield 1;
                }

                double odds = chance / (1 - chance);
                odds *= amount;

                yield odds / (1 + odds);
            }
        };
    }

    public static Modification luck(@Nullable final Player player) {
        if (player == null) {
            return NONE;
        }

        float multiplier = (float) (player.getAttributeValue(Attributes.LUCK) * ServerConfig.LUCK_MULTIPLIER.get());

        if (multiplier < 0) {
            // TODO :: Consider adding negative impacts of luck
            return NONE;
        }

        return multiplicative(1 + multiplier);
    }

    public static Modification harvestOrSeedMultiplier(final Quality quality, final ItemStack stack) {
        // We don't add a baseline 1 for these multipliers
        // Since a configured multiplier of 0.5 is supposed to actually lower the chance
        if (stack.is(Tags.Items.CROPS)) {
            return Modification.multiplicative(QualityConfig.getCropMultiplier(quality));
        } else if (stack.is(Tags.Items.SEEDS)) {
            return Modification.multiplicative(QualityConfig.getSeedMultiplier(quality));
        }

        return NONE;
    }

    public static Modification farmland(final BlockState crop, @Nullable final BlockState farmland) {
        if (farmland == null) {
            return NONE;
        }

        // Basically the same reason as with the 'harvestOrSeedMultiplier'
        return Modification.multiplicative(ServerConfig.getFarmlandMultiplier(crop, farmland));
    }

    public static Modification potential(final double potential) {
        if (potential <= 0) {
            return NONE;
        }

        return oddsMultiplier((float) (1 + potential * ServerConfig.ANIMAL_POTENTIAL_IMPACT.get()));
    }

    /** @throws IllegalArgumentException If the {@link Modification#type} does not match */
    public Modification combine(final Modification modification) {
        if (this == Modification.NONE) {
            return modification;
        }

        if (modification == Modification.NONE) {
            return this;
        }

        if (type == modification.type) {
            return switch (type) {
                case ADDITIVE -> additive(amount + modification.amount);
                case MULTIPLICATIVE -> multiplicative(amount * modification.amount);
                case ODDS_MULTIPLICATIVE -> oddsMultiplier(amount * modification.amount);
            };
        }

        throw new IllegalArgumentException("Combinations of different modification types is not yet supported");
    }

    public enum Type {
        ADDITIVE,
        MULTIPLICATIVE,
        ODDS_MULTIPLICATIVE
    }
}
