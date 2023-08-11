package dev.JustRed23.createdimensions.register;

import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.JustRed23.createdimensions.DimensionsAddon;
import dev.JustRed23.createdimensions.blocks.FluidTransporter;
import dev.JustRed23.createdimensions.blocks.ItemTransporter;
import dev.JustRed23.createdimensions.blocks.RotationTransporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public final class CDBlocks {

    // This is just here to make sure the class is loaded
    public static void init() {}

    public static final BlockEntry<ItemTransporter> ITEM_TRANSPORTER =
            createTransporter("dimensional_item_transporter", ItemTransporter::new).register();

    public static final BlockEntry<FluidTransporter> FLUID_TRANSPORTER =
            createTransporter("dimensional_fluid_transporter", FluidTransporter::new).register();

    public static final BlockEntry<RotationTransporter> ROTATION_TRANSPORTER =
            createTransporter("dimensional_rotation_transporter", RotationTransporter::new).register();

    private static <T extends Block> BlockBuilder<T, CreateRegistrate> createTransporter(String name, NonNullFunction<BlockBehaviour.Properties, T> blockFactory) {
        return DimensionsAddon.registrate()
                .creativeModeTab(() -> CDCreativeTabs.MAIN)
                .block(name, blockFactory)
                .properties(BlockBehaviour.Properties::noOcclusion)
                .properties(p -> p.color(MaterialColor.PODZOL))
                .transform(BlockStressDefaults.setNoImpact())
                .transform(pickaxeOnly())
                .initialProperties(SharedProperties::copperMetal)
                .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
                .item()
                .transform(customItemModel());
    }
}
