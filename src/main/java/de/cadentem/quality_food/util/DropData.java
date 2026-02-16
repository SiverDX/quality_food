package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DropData(Quality quality, BlockState state, Player player, BlockState farmland) {
    public static final ThreadLocal<DropData> CURRENT = new ThreadLocal<>();
    public static final DropData SKIP = new DropData(Quality.NONE, null, null, null);

    public static DropData create(@NotNull final Quality quality, @Nullable final BlockState state, @Nullable final Entity entity, @Nullable final BlockState farmland) {
        return new DropData(quality, state, entity instanceof Player player ? player : null, farmland);
    }
}