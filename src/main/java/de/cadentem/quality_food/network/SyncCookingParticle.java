package de.cadentem.quality_food.network;

import de.cadentem.quality_food.client.ClientProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncCookingParticle(BlockPos position, double qualityBonus) {
    public void encode(final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(position);
        buffer.writeDouble(qualityBonus);
    }

    public static SyncCookingParticle decode(final FriendlyByteBuf buffer) {
        return new SyncCookingParticle(buffer.readBlockPos(), buffer.readDouble());
    }

    public static void handle(final SyncCookingParticle packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientProxy.handleCookingParticles(packet.position(), packet.qualityBonus()));
        }

        context.setPacketHandled(true);
    }
}
