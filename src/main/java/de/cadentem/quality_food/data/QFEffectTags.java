package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeRegistryTagsProvider;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class QFEffectTags extends ForgeRegistryTagsProvider<MobEffect> {
    public static final TagKey<MobEffect> BENEFICIAL_BLACKLIST = new TagKey<>(Registry.MOB_EFFECT_REGISTRY, new ResourceLocation(QualityFood.MODID, "beneficial_blacklist"));
    public static final TagKey<MobEffect> HARMFUL_BLACKLIST = new TagKey<>(Registry.MOB_EFFECT_REGISTRY, new ResourceLocation(QualityFood.MODID, "harmful_blacklist"));

    public QFEffectTags(final DataGenerator generator, @Nullable final ExistingFileHelper fileHelper) {
        super(generator, ForgeRegistries.MOB_EFFECTS, QualityFood.MODID, fileHelper);
    }

    @Override
    protected void addTags() {
        tag(BENEFICIAL_BLACKLIST);
        tag(HARMFUL_BLACKLIST);
    }
}