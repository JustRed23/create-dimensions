package dev.JustRed23.createdimensions.register;

import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.JustRed23.createdimensions.DimensionsAddon;
import dev.JustRed23.createdimensions.gui.impl.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class CDMenus {

    // This is just here to make sure the class is loaded
    public static void init() {}

    public static final MenuEntry<ItemTransporterMenu> ITEM_TRANSPORTER =
            register("dimensional_item_transporter", ItemTransporterMenu::new, () -> ItemTransporterScreen::new);

    public static final MenuEntry<FluidTransporterMenu> FLUID_TRANSPORTER =
            register("dimensional_fluid_transporter", FluidTransporterMenu::new, () -> FluidTransporterScreen::new);

    public static final MenuEntry<RotationTransporterMenu> ROTATION_TRANSPORTER =
            register("dimensional_rotation_transporter", RotationTransporterMenu::new, () -> RotationTransporterScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return DimensionsAddon.registrate()
                .menu(name, factory, screenFactory)
                .register();
    }
}
