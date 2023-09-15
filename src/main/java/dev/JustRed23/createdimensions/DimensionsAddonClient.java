package dev.JustRed23.createdimensions;

import dev.JustRed23.createdimensions.gui.overlay.SynchronizerOverlay;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class DimensionsAddonClient {

    public DimensionsAddonClient() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        DimensionsAddon.LOGGER.info("Hello from the client side!");
        registerOverlays();
    }

    private void registerOverlays() {
        OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HOTBAR_ELEMENT, "Create: Dimensions' Synchronizer Overlay", SynchronizerOverlay.OVERLAY);
    }
}
