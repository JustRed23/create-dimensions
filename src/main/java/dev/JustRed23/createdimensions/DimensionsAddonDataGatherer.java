package dev.JustRed23.createdimensions;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = DimensionsAddon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DimensionsAddonDataGatherer {

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
    }
}
