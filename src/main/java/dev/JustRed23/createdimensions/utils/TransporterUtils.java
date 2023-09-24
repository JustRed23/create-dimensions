package dev.JustRed23.createdimensions.utils;

import com.simibubi.create.foundation.item.SmartInventory;
import dev.JustRed23.createdimensions.behaviour.ISync;
import dev.JustRed23.createdimensions.register.CDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class TransporterUtils {

    public static boolean handleItemUse(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        final ItemStack hand = pPlayer.getItemInHand(pHand);
        if (hand.isEmpty())
            return false;

        //useOn gets called on the item automatically, just return
        if (CDItems.TRANSPORTER_SYNCHRONIZER.isIn(hand))
            return true;
        if (CDItems.TRANSPORTER_CHUNK_UPGRADE.isIn(hand))
            return true;
        //TODO: custom item handling, API?

        return false;
    }

    public static void handleChunkLoading(@NotNull ISync transporter, Level level, SmartInventory upgradeInventory) {
        if (!(level instanceof ServerLevel srvLvl)) return;
        BlockEntity owner = (BlockEntity) transporter;

        if (owner.isRemoved() || upgradeInventory == null)
            return;

        //Don't load chunks if the transporter isn't connected
        if (!transporter.isConnected()) {
            transporter.setChunkLoaded(false);
            ChunkLoaderUtils.unloadSurroundingChunks(srvLvl, owner);
            return;
        }

        //Don't load chunks if the transporter doesn't have a chunk upgrade
        if (!CDItems.TRANSPORTER_CHUNK_UPGRADE.isIn(upgradeInventory.getStackInSlot(0))) {
            transporter.setChunkLoaded(false);
            ChunkLoaderUtils.unloadSurroundingChunks(srvLvl, owner);
            return;
        }

        if (transporter.isChunkLoaded()) return;

        transporter.setChunkLoaded(true);
        ChunkLoaderUtils.loadSurroundingChunks(srvLvl, owner);
    }

    public static void handleDestroyed(@NotNull ISync transporter, Level level) {
        if (!(level instanceof ServerLevel srvLvl)) return;
        BlockEntity owner = (BlockEntity) transporter;

        //Remove chunk loading capabilities
        if (transporter.isChunkLoaded()) {
            transporter.setChunkLoaded(false);
            ChunkLoaderUtils.unloadSurroundingChunks(srvLvl, owner);
        }
    }
}
