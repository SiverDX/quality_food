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

public record CookingParticles(BlockPos position, double qualityBonus) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CookingParticles> TYPE = new CustomPacketPayload.Type<>(QualityFood.location("cooking_particles"));

    public static final StreamCodec<ByteBuf, CookingParticles> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CookingParticles::position,
            ByteBufCodecs.DOUBLE,
            CookingParticles::qualityBonus,
            CookingParticles::new
    );

    public static void handleClient(final CookingParticles particles, final IPayloadContext context) {
        context.enqueueWork(() -> ClientProxy.handleCookingParticles(particles.position(), particles.qualityBonus()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
