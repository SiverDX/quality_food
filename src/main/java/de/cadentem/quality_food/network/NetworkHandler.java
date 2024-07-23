package de.cadentem.quality_food.network;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    public static final String PROTOCOL_VERSION = "1.0.0";
//    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(QualityFood.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(CookingParticles.TYPE, CookingParticles.STREAM_CODEC, CookingParticles::handleClient);
//        CHANNEL.registerMessage(0, CookingParticles.class, CookingParticles::encode, CookingParticles::decode, CookingParticles::handle);
    }
}