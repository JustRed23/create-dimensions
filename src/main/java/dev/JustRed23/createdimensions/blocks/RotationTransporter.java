package dev.JustRed23.createdimensions.blocks;

import dev.JustRed23.createdimensions.blocks.blockentities.RotationTransporterEntity;
import dev.JustRed23.createdimensions.register.CDBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RotationTransporter extends AbstractTransporter<RotationTransporterEntity> {

    public RotationTransporter(Properties pProperties) {
        super(pProperties);
    }

    public Class<RotationTransporterEntity> getBlockEntityClass() {
        return RotationTransporterEntity.class;
    }

    public BlockEntityType<? extends RotationTransporterEntity> getBlockEntityType() {
        return CDBlockEntities.ROTATION_TRANSPORTER.get();
    }
}
