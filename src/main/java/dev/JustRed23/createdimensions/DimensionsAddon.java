package dev.JustRed23.createdimensions;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import dev.JustRed23.createdimensions.register.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DimensionsAddon.MODID)
public class DimensionsAddon {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "createdimensions";

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    public DimensionsAddon() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        REGISTRATE.registerEventListeners(modEventBus);
        registerStuff(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> DimensionsAddonClient::new);
    }

    private void registerStuff(IEventBus modEventBus) {
        CDCreativeTabs.init();
        CBPartialModels.init();
        CDBlockEntities.init();
        CDBlocks.init();
        CDItems.init();
    }

    @SubscribeEvent
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        LOGGER.info("Hello from the common side!");
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }
}