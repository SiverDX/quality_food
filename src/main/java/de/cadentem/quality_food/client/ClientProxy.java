package de.cadentem.quality_food.client;

import de.cadentem.quality_food.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ClientProxy {
    public static void handleCookingParticles(final BlockPos position, double qualityBonus) {
        if (!ClientConfig.PARTICLES.get()) {
            return;
        }

        ClientLevel level = Minecraft.getInstance().level;

        if (level == null) {
            return;
        }

        int amount = 1;

        if (qualityBonus == 1) {
            amount = 5;
        } else if (qualityBonus >= 0.75) {
            amount = 4;
        } else if (qualityBonus >= 0.5) {
            amount = 3;
        } else if (qualityBonus >= 0.25) {
            amount = 2;
        }

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
