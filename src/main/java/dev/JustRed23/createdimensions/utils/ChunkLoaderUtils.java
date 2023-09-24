package dev.JustRed23.createdimensions.utils;

import dev.JustRed23.createdimensions.DimensionsAddon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.jetbrains.annotations.NotNull;

public final class ChunkLoaderUtils {

    public static void loadChunk(@NotNull ServerLevel level, @NotNull BlockEntity owner, int chunkX, int chunkZ) {
        ForgeChunkManager.forceChunk(level, DimensionsAddon.MODID, owner.getBlockPos(), chunkX, chunkZ, true, true);
    }

    public static void unloadChunk(@NotNull ServerLevel level, @NotNull BlockEntity owner, int chunkX, int chunkZ) {
        ForgeChunkManager.forceChunk(level, DimensionsAddon.MODID, owner.getBlockPos(), chunkX, chunkZ, false, true);
    }

    /**
     * Loads the chunks around the owner in a 3x3 area
     */
    public static void loadSurroundingChunks(@NotNull ServerLevel level, @NotNull BlockEntity owner) {
        int chunkX = owner.getBlockPos().getX() >> 4;
        int chunkZ = owner.getBlockPos().getZ() >> 4;

        /*
        x x x
        - - -
        - - -
         */
        loadChunk(level, owner, chunkX - 1, chunkZ - 1);
        loadChunk(level, owner, chunkX, chunkZ - 1);
        loadChunk(level, owner, chunkX + 1, chunkZ - 1);

        /*
        - - -
        x x x
        - - -
         */
        loadChunk(level, owner, chunkX - 1, chunkZ);
        loadChunk(level, owner, chunkX, chunkZ);
        loadChunk(level, owner, chunkX + 1, chunkZ);

        /*
        - - -
        - - -
        x x x
         */
        loadChunk(level, owner, chunkX - 1, chunkZ + 1);
        loadChunk(level, owner, chunkX, chunkZ + 1);
        loadChunk(level, owner, chunkX + 1, chunkZ + 1);
    }

    /**
     * Unloads the chunks around the owner in a 3x3 area
     */
    public static void unloadSurroundingChunks(@NotNull ServerLevel level, @NotNull BlockEntity owner) {
        int chunkX = owner.getBlockPos().getX() >> 4;
        int chunkZ = owner.getBlockPos().getZ() >> 4;

        /*
        x x x
        - - -
        - - -
         */
        unloadChunk(level, owner, chunkX - 1, chunkZ - 1);
        unloadChunk(level, owner, chunkX, chunkZ - 1);
        unloadChunk(level, owner, chunkX + 1, chunkZ - 1);

        /*
        - - -
        x x x
        - - -
         */
        unloadChunk(level, owner, chunkX - 1, chunkZ);
        unloadChunk(level, owner, chunkX, chunkZ);
        unloadChunk(level, owner, chunkX + 1, chunkZ);

        /*
        - - -
        - - -
        x x x
         */
        unloadChunk(level, owner, chunkX - 1, chunkZ + 1);
        unloadChunk(level, owner, chunkX, chunkZ + 1);
        unloadChunk(level, owner, chunkX + 1, chunkZ + 1);
    }
}
