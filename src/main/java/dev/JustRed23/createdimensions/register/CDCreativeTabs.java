package dev.JustRed23.createdimensions.register;

import dev.JustRed23.createdimensions.DimensionsAddon;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public final class CDCreativeTabs {

    // This is just here to make sure the class is loaded
    public static void init() {}

    public static final CreativeModeTab MAIN = new CreativeModeTab(DimensionsAddon.MODID + ".main") {
        public @NotNull ItemStack makeIcon() {
            return Items.MINECART.getDefaultInstance();
        }
    };
}
