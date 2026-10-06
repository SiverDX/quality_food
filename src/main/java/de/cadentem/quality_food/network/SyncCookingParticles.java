package de.cadentem.quality_food.network;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncCookingParticles(BlockPos position, double queueSize) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncCookingParticles> TYPE = new CustomPacketPayload.Type<>(QualityFood.location("sync_cooking_particles"));

    public static final StreamCodec<ByteBuf, SyncCookingParticles> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncCookingParticles::position,
            ByteBufCodecs.DOUBLE, SyncCookingParticles::queueSize,
            SyncCookingParticles::new
    );

    public static void handleClient(final SyncCookingParticles particles, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleCookingParticles(particles.position(), particles.queueSize()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
