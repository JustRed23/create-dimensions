package dev.JustRed23.createdimensions.blocks;

import dev.JustRed23.createdimensions.blocks.blockentities.ItemTransporterEntity;
import dev.JustRed23.createdimensions.register.CDBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ItemTransporter extends AbstractTransporter<ItemTransporterEntity> {

    public ItemTransporter(Properties pProperties) {
        super(pProperties);
    }

    public Class<ItemTransporterEntity> getBlockEntityClass() {
        return ItemTransporterEntity.class;
    }

    public BlockEntityType<? extends ItemTransporterEntity> getBlockEntityType() {
        return CDBlockEntities.ITEM_TRANSPORTER.get();
    }
}
