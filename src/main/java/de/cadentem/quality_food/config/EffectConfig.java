package de.cadentem.quality_food.config;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Predicate;

public class EffectConfig {
    public static final int ITEM = 0;
    public static final int EFFECT = 1;
    public static final int CHANCE = 2;
    public static final int DURATION = 3;
    public static final int AMPLIFIER = 4;
    public static final int PROBABILITY = 5;

    private final Effect effect;
    private Predicate<ItemStack> predicate;

    private EffectConfig(final Effect effect) {
        this.effect = effect;
    }

    @SuppressWarnings("ConstantConditions")
    public static EffectConfig create(final String configEntry) {
        String[] data = configEntry.split(";");
        ResourceLocation effectLocation = ResourceLocation.tryParse(data[EFFECT]);

        if (!ForgeRegistries.MOB_EFFECTS.containsKey(effectLocation)) {
            QualityFood.LOG.warn("Could not find mob effect [{}]", data[EFFECT]);
            return null;
        }

        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectLocation);
        double chance = Double.parseDouble(data[CHANCE]);
        int duration = Integer.parseInt(data[DURATION]);
        int amplifier = Integer.parseInt(data[AMPLIFIER]);
        double probability = Double.parseDouble(data[PROBABILITY]);

        EffectConfig config = new EffectConfig(new Effect(effect, chance, duration, amplifier, probability));

        if (data[ITEM].startsWith("#")) {
            TagKey<Item> testItem = TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation.tryParse(data[ITEM].substring(1)));
            config.predicate = stack -> stack.is(testItem);
        } else {
            Item testItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(data[ITEM]));
            config.predicate = stack -> stack.getItem() == testItem;
        }

        return config;
    }

    public boolean test(final ItemStack stack) {
        return predicate.test(stack);
    }

    public Effect getEffect() {
        return effect;
    }

    public record Effect(MobEffect effect, double chance, int duration, int amplifier, double probability) { /* Nothing to do */ }
}
