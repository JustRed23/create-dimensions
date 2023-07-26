package dev.JustRed23.createdimensions.blocks;

import dev.JustRed23.createdimensions.blocks.blockentities.FluidTransporterEntity;
import dev.JustRed23.createdimensions.register.CDBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FluidTransporter extends AbstractTransporter<FluidTransporterEntity> {

    public FluidTransporter(Properties pProperties) {
        super(pProperties);
    }

    public Class<FluidTransporterEntity> getBlockEntityClass() {
        return FluidTransporterEntity.class;
    }

    public BlockEntityType<? extends FluidTransporterEntity> getBlockEntityType() {
        return CDBlockEntities.FLUID_TRANSPORTER.get();
    }
}
