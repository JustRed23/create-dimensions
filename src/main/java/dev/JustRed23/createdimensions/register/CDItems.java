package dev.JustRed23.createdimensions.register;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.JustRed23.createdimensions.DimensionsAddon;
import dev.JustRed23.createdimensions.items.SynchronizerCard;

public final class CDItems {

    // This is just here to make sure the class is loaded
    public static void init() {}

    public static final ItemEntry<?> TRANSPORTER_SYNCHRONIZER_CARD = DimensionsAddon.registrate()
            .item("transporter_synchronizer_card", SynchronizerCard::new)
            .properties(properties -> properties.stacksTo(2))
            .register();
}
