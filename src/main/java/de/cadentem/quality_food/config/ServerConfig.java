package de.cadentem.quality_food.config;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.Quality;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static Map<Integer, QualityConfig> QUALITY_CONFIG = new HashMap<>();

    public static ForgeConfigSpec.BooleanValue ENABLE_FALLBACK;

    static {
        ENABLE_FALLBACK = BUILDER.comment("Enable the fallback logic to apply quality to crafted items (this may lead to the quality of other input not being considered)").define("enable_fallback", false);

        for (Quality quality : Quality.values()) {
            if (quality == Quality.NONE) {
                continue;
            }

            BUILDER.push(quality.name());

            QualityConfig config = new QualityConfig();
            config.chance = BUILDER.comment("The chance for a quality to occur (with no luck or other bonus)").defineInRange("chance", QualityConfig.getDefaultChance(quality), 0, 1);
            config.durationMultiplier = BUILDER.comment("By how much the duration of the effect will get multiplied (beneficial) or divided (harmful) for").defineInRange("duration_multiplier", QualityConfig.getDefaultDurationMultiplier(quality), 1, 100);
            config.probabilityAddition = BUILDER.comment("The addition (beneficial) or subtraction (harmful) for the probability (chance for the effect to apply)").defineInRange("probability_addition", QualityConfig.getDefaultProbabilityAddition(quality), 0, 1);
            config.amplifierAddition = BUILDER.comment("The addition (beneficial) or subtraction (harmful) for the amplifier (level of the effect)").defineInRange("amplifier_addition", QualityConfig.getDefaultAmplifierAddition(quality), 0, 255);
            config.nutritionMultiplier = BUILDER.comment("By how much the nutrition will get multiplied for").defineInRange("nutrition_multiplier", QualityConfig.getDefaultNutritionMultiplier(quality), 1, 100);
            config.saturationMultiplier = BUILDER.comment("By how much the saturation will get multiplied for").defineInRange("saturation_multiplier", QualityConfig.getDefaultSaturationMultiplier(quality), 1, 100);
            config.effectList = BUILDER.comment("List of effects this rarity can grant (<effect>;<chance>;<duration>;<amplifier>;<probability>)").defineList("effect_list", List.of(), ServerConfig::isEffectListValid);
            QUALITY_CONFIG.put(quality.ordinal(), config);
            BUILDER.pop();
        }

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void reloadConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            QUALITY_CONFIG.values().forEach(QualityConfig::initializeEffects);
            QualityFood.LOG.info("Reloaded configuration");
        }
    }

    private static boolean isEffectListValid(final Object object) {
        if (object instanceof String) {
            return isEffectValid(object);
        }

        return false;
    }

    private static boolean isEffectValid(final Object object) {
        if (object instanceof String string) {
            String[] data = string.split(";");

            if (data.length == 5) {
                if (!ResourceLocation.isValidResourceLocation(data[0])) {
                    return false;
                }

                if (isInvalidChance(/* Chance */ data[1])) {
                    return false;
                }

                if (isInvalidInteger(/* Duration */ data[2])) {
                    return false;
                }

                if (isInvalidInteger(/* Amplifier */ data[3])) {
                    return false;
                }

                if (isInvalidChance(/* Probability */ data[4])) {
                    return false;
                }

                return true;
            }
        }

        return false;
    }

    private static boolean isInvalidChance(final String value) {
        if (value == null) {
            return true;
        }

        try {
            double chance = Double.parseDouble(value);
            return chance < 0 || chance > 1;
        } catch (NumberFormatException ignored) { /* Nothing to do */ }

        return true;
    }

    private static boolean isInvalidInteger(final String value) {
        if (value == null) {
            return true;
        }

        if (value.contains(".") || value.contains(",")) {
            return true;
        }

        try {
            return Integer.parseInt(value) < 0;
        } catch (NumberFormatException ignored) { /* Nothing to do */ }

        return true;
    }
}
