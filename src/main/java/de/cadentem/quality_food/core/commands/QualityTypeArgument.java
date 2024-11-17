package de.cadentem.quality_food.core.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.registry.QFComponents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class QualityTypeArgument implements ArgumentType<Holder<QualityType>> {
    private final HolderLookup.RegistryLookup<QualityType> lookup;

    public QualityTypeArgument(final CommandBuildContext context) {
        lookup = context.lookupOrThrow(QFComponents.QUALITY_TYPE_REGISTRY);
    }

    @Override
    public @Nullable Holder<QualityType> parse(final StringReader reader) throws CommandSyntaxException {
        Optional<Holder.Reference<QualityType>> optional = lookup.get(ResourceKey.create(QFComponents.QUALITY_TYPE_REGISTRY, ResourceLocation.read(reader)));

        if (optional.isPresent()) {
            return optional.get();
        } else {
            return Holder.direct(QualityType.NONE);
        }
    }

    public static Holder<QualityType> get(final CommandContext<?> context) {
        //noinspection unchecked -> type is valid
        return (Holder<QualityType>) context.getArgument("quality_type", Holder.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        List<String> suggestions = new ArrayList<>();
        lookup.listElementIds().forEach(element -> suggestions.add(element.location().toString()));
        return SharedSuggestionProvider.suggest(suggestions, builder);
    }
}
