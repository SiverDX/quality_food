package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class QFEntityTypeTags extends EntityTypeTagsProvider {
    public static final TagKey<EntityType<?>> ANIMAL_DATA_BLACKLIST = key("animal_data_blacklist");

    public QFEntityTypeTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider, @Nullable final ExistingFileHelper helper) {
        super(output, provider, QualityFood.MODID, helper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(ANIMAL_DATA_BLACKLIST);
    }

    @SuppressWarnings("SameParameterValue") // ignore for clarity
    private static TagKey<EntityType<?>> key(final String path) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(QualityFood.MODID, path));
    }
}
