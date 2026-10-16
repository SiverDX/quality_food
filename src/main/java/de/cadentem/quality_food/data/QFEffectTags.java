package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class QFEffectTags extends TagsProvider<MobEffect> {
    public static final TagKey<MobEffect> BLACKLIST = TagKey.create(Registries.MOB_EFFECT, QualityFood.location("blacklist"));

    public QFEffectTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.MOB_EFFECT, provider, QualityFood.MODID);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(BLACKLIST);
    }
}
