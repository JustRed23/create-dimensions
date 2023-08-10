package dev.JustRed23.createdimensions.register;

import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.JustRed23.createdimensions.DimensionsAddon;
import dev.JustRed23.createdimensions.blocks.FluidTransporter;
import dev.JustRed23.createdimensions.blocks.ItemTransporter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public final class CDBlocks {

    // This is just here to make sure the class is loaded
    public static void init() {}

    public static final BlockEntry<ItemTransporter> ITEM_TRANSPORTER = DimensionsAddon.registrate()
            .creativeModeTab(() -> CDCreativeTabs.MAIN)
            .block("dimensional_item_transporter", ItemTransporter::new)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BlockStressDefaults.setNoImpact())
            .transform(pickaxeOnly())
            .initialProperties(SharedProperties::copperMetal)
            .simpleItem()
            .register();

    public static final BlockEntry<FluidTransporter> FLUID_TRANSPORTER = DimensionsAddon.registrate()
            .creativeModeTab(() -> CDCreativeTabs.MAIN)
            .block("dimensional_fluid_transporter", FluidTransporter::new)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(p -> p.color(MaterialColor.PODZOL))
            .transform(BlockStressDefaults.setNoImpact())
            .transform(pickaxeOnly())
            .initialProperties(SharedProperties::copperMetal)
            .simpleItem()
            .register();

//    public static final BlockEntry<RotationTransporter> ROTATION_TRANSPORTER = DimensionsAddon.registrate()
//            .creativeModeTab(() -> CDCreativeTabs.MAIN)
//            .block("dimensional_rotation_transporter", RotationTransporter::new)
//            .properties(BlockBehaviour.Properties::noOcclusion)
//			  .properties(p -> p.color(MaterialColor.PODZOL))
//            .transform(BlockStressDefaults.setNoImpact())
//            .transform(axeOrPickaxe())
//            .initialProperties(SharedProperties::copperMetal)
//            .simpleItem()
//            .register();
}
