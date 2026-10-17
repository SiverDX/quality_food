package de.cadentem.quality_food.compat.jade;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.registry.QFComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;

public class QualityBlockProvider implements IBlockComponentProvider {
    private static final Identifier ID = QualityFood.location("quality_block");

    @Override
    public void appendTooltip(final ITooltip toolTip, final BlockAccessor accessor, final IPluginConfig config) {
        CompoundTag tag = accessor.getServerData();

        if (tag.contains(QualityFood.concat("type"))) {
            int level = tag.getInt(QualityFood.concat("level")).orElseThrow();

            if (level <= 0) {
                return;
            }

            Identifier location = Identifier.parse(tag.getString(QualityFood.concat("type")).orElseThrow());

            accessor.getLevel().holder(ResourceKey.create(QFComponents.QUALITY_TYPE_REGISTRY, location)).ifPresent(qualityTypeReference -> {
                toolTip.add(Component.translatable("quality_food.jade", qualityTypeReference.value().name()));
                toolTip.append(JadeUI.sprite(qualityTypeReference.value().icon(), 10, 10).offset(2, 1));
            });
        }
    }

    @Override
    public Identifier getUid() {
        return ID;
    }

}
