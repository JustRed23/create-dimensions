package dev.JustRed23.createdimensions;

import dev.JustRed23.createdimensions.gui.overlay.SynchronizerOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.TextureStitchEvent;
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

    @SubscribeEvent
    public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS))
            event.addSprite(new ResourceLocation(DimensionsAddon.MODID, "item/empty_upgrade_slot"));
    }
}
