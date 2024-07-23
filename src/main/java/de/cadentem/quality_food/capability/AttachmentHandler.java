package de.cadentem.quality_food.capability;

import de.cadentem.quality_food.QualityFood;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentHandler {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, QualityFood.MODID);
    public static final Supplier<AttachmentType<BlockData>> BLOCK_DATA = ATTACHMENT_TYPES.register("block_data", () -> AttachmentType.serializable(BlockData::new).build());
}