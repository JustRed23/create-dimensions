package dev.JustRed23.createdimensions.behaviour;

import dev.JustRed23.createdimensions.blocks.blockentities.TransporterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface ISync {

    void syncWithConnected();

    TransporterEntity.ConnectionStatus connectTo(BlockPos pos, ResourceKey<Level> dimension);

    boolean isConnected();

    BlockPos getConnection();
    ResourceKey<Level> getDimension();

    void setMode(@NotNull TransporterEntity.TransportationMode mode);
    TransporterEntity.TransportationMode getMode();
    void switchMode();
}
