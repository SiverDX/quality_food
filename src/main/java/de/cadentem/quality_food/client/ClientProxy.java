package de.cadentem.quality_food.client;

import de.cadentem.quality_food.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ClientProxy {
    public static void handleCookingParticles(final BlockPos position, double queueSize) {
        if (!ClientConfig.PARTICLES.get()) {
            return;
        }

        ClientLevel level = Minecraft.getInstance().level;

        if (level == null) {
            return;
        }

        // Max. amount is based on a stack size of 64 (high particle count if a stack is theoretically fully cooked)
        int amount = (int) Math.ceil(queueSize * 5d / 64d);

        for (int i = 0; i < amount; i++) {
            double x = 0.5 + position.getX() + (level.getRandom().nextDouble() - 0.5);
            double y = 1.25 + position.getY() + (level.getRandom().nextDouble() / 2 - 0.25);
            double z = 0.5 + position.getZ() + (level.getRandom().nextDouble() - 0.5);

            level.addParticle(ParticleTypes.GLOW, x, y, z, 0, 0, 0);
        }
    }

    public static @Nullable Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }
}
