package dev.JustRed23.createdimensions.behaviour;

import dev.JustRed23.createdimensions.blocks.blockentities.TransporterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface ISync {

    TransporterEntity.ConnectionStatus connectTo(BlockPos pos, ResourceKey<Level> dimension);
}
