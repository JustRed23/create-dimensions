package dev.JustRed23.createdimensions.register;

import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.JustRed23.createdimensions.DimensionsAddon;
import dev.JustRed23.createdimensions.blocks.FluidTransporter;
import dev.JustRed23.createdimensions.blocks.ItemTransporter;
import dev.JustRed23.createdimensions.blocks.RotationTransporter;

public final class CDBlocks {

    // This is just here to make sure the class is loaded
    public static void init() {}

    public static final BlockEntry<ItemTransporter> ITEM_TRANSPORTER = DimensionsAddon.registrate()
            .creativeModeTab(() -> CDCreativeTabs.MAIN)
            .block("dimensional_item_transporter", ItemTransporter::new)
            .initialProperties(SharedProperties::copperMetal)
            .simpleItem()
            .register();

    public static final BlockEntry<FluidTransporter> FLUID_TRANSPORTER = DimensionsAddon.registrate()
            .creativeModeTab(() -> CDCreativeTabs.MAIN)
            .block("dimensional_fluid_transporter", FluidTransporter::new)
            .initialProperties(SharedProperties::copperMetal)
            .simpleItem()
            .register();

    public static final BlockEntry<RotationTransporter> ROTATION_TRANSPORTER = DimensionsAddon.registrate()
            .creativeModeTab(() -> CDCreativeTabs.MAIN)
            .block("dimensional_rotation_transporter", RotationTransporter::new)
            .initialProperties(SharedProperties::copperMetal)
            .simpleItem()
            .register();
}
