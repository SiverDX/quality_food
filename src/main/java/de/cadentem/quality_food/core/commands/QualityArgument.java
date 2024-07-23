package de.cadentem.quality_food.core.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QualityArgument implements ArgumentType<Quality> {
    @Override
    public Quality parse(final StringReader reader) throws CommandSyntaxException {
        return Quality.byName(reader.readString());
    }

    public static Quality get(final CommandContext<?> context) {
        Quality quality = context.getArgument("quality", Quality.class);
        return quality.level() != 0 ? quality : Quality.NONE;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(List.of(Quality.IRON.getName().toLowerCase(), Quality.GOLD.getName().toLowerCase(), Quality.DIAMOND.getName().toLowerCase()), builder);
    }
}
