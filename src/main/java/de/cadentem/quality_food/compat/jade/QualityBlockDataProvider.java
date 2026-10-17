package de.cadentem.quality_food.compat.jade;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class QualityBlockDataProvider implements IServerDataProvider<BlockAccessor> {
    @Override
    public Identifier getUid() {
        return QualityFood.location("quality_block");
    }

    @Override
    public void appendServerData(final CompoundTag tag, final BlockAccessor accessor) {
        Quality quality = accessor.getLevel().getData(AttachmentHandler.LEVEL_DATA).get(accessor.getPosition());

        if (quality != Quality.NONE) {
            tag.putString(QualityFood.concat("type"), quality.type().toString());
            tag.putInt(QualityFood.concat("level"), quality.level());
        }
    }

    @Override
    public boolean shouldRequestData(final BlockAccessor accessor) {
        return Utils.isValidBlock(accessor.getBlock());
    }
}
