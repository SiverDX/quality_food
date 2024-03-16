package de.cadentem.quality_food.core.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class QualityItemArgument extends ItemArgument {
    public QualityItemArgument(final CommandBuildContext context) {
        super(context);
    }

    public static QualityItemArgument item(final CommandBuildContext context) {
        return new QualityItemArgument(context);
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull final CommandContext<S> context, @NotNull final SuggestionsBuilder builder) {
        return super.listSuggestions(context, builder).thenApply(suggestions -> {
            suggestions.getList().removeIf(entry -> !QualityUtils.canHaveQuality(ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.getText())).getDefaultInstance()));
            return suggestions;
        });
    }
}
