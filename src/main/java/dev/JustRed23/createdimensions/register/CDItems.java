package dev.JustRed23.createdimensions.register;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.JustRed23.createdimensions.DimensionsAddon;
import dev.JustRed23.createdimensions.items.ChunkLoaderCard;
import dev.JustRed23.createdimensions.items.SynchronizerItem;

public final class CDItems {

    // This is just here to make sure the class is loaded
    public static void init() {}

    public static final ItemEntry<SynchronizerItem> TRANSPORTER_SYNCHRONIZER = DimensionsAddon.registrate()
            .item("transporter_synchronizer", SynchronizerItem::new)
            .properties(properties -> properties.stacksTo(1))
            .register();

    public static final ItemEntry<ChunkLoaderCard> TRANSPORTER_CHUNK_UPGRADE = DimensionsAddon.registrate()
            .item("transporter_chunk_upgrade", ChunkLoaderCard::new)
            .properties(properties -> properties.stacksTo(2))
            .register();
}
