package de.cadentem.quality_food.core.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.cadentem.quality_food.component.QFRegistries;
import de.cadentem.quality_food.component.QualityType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class QualityTypeArgument implements ArgumentType<QualityType> {
    private final HolderLookup.RegistryLookup<QualityType> lookup;

    public QualityTypeArgument(final CommandBuildContext context) {
        lookup = context.lookupOrThrow(QFRegistries.QUALITY_TYPE_REGISTRY);
    }

    @Override
    public QualityType parse(final StringReader reader) throws CommandSyntaxException {
        Optional<Holder.Reference<QualityType>> reference = lookup.get(ResourceKey.create(QFRegistries.QUALITY_TYPE_REGISTRY, ResourceLocation.read(reader)));
        return reference.map(Holder.Reference::value).orElse(QualityType.NONE);
    }

    public static QualityType get(final CommandContext<?> context) {
        return context.getArgument("quality_type", QualityType.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        List<String> suggestions = new ArrayList<>();
        lookup.listElementIds().forEach(element -> suggestions.add(element.location().toString()));
        return SharedSuggestionProvider.suggest(suggestions, builder);
    }
}
