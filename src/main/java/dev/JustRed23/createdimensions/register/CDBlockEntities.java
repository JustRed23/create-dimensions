package dev.JustRed23.createdimensions.register;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.JustRed23.createdimensions.DimensionsAddon;
import dev.JustRed23.createdimensions.blocks.blockentities.FluidTransporterEntity;
import dev.JustRed23.createdimensions.blocks.blockentities.ItemTransporterEntity;

public final class CDBlockEntities {

    // This is just here to make sure the class is loaded
    public static void init() {}

    public static final BlockEntityEntry<ItemTransporterEntity> ITEM_TRANSPORTER = DimensionsAddon.registrate()
            .blockEntity("dimensional_item_transporter", ItemTransporterEntity::new)
            .validBlock(CDBlocks.ITEM_TRANSPORTER)
            .register();

    public static final BlockEntityEntry<FluidTransporterEntity> FLUID_TRANSPORTER = DimensionsAddon.registrate()
            .blockEntity("dimensional_fluid_transporter", FluidTransporterEntity::new)
            .validBlock(CDBlocks.FLUID_TRANSPORTER)
            .register();

//    public static final BlockEntityEntry<RotationTransporterEntity> ROTATION_TRANSPORTER = DimensionsAddon.registrate()
//            .blockEntity("dimensional_rotation_transporter", RotationTransporterEntity::new)
//            .validBlock(CDBlocks.ROTATION_TRANSPORTER)
//            .register();
}
