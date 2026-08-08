package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record DropData(Quality quality, BlockPos position, BlockState state, Player player, BlockState farmland) {
    public static final ThreadLocal<DropData> CURRENT = new ThreadLocal<>();
    public static final DropData SKIP = new DropData(Quality.NONE, BlockPos.ZERO, null, null, null);

    public static DropData create(final Quality quality, final BlockPos position, @Nullable final BlockState state, @Nullable final Entity entity, @Nullable final BlockState farmland) {
        return new DropData(quality, position, state, entity instanceof Player player ? player : null, farmland);
    }
}