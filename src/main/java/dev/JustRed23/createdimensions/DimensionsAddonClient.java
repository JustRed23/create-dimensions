package dev.JustRed23.createdimensions;

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
    }
}
